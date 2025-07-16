package com.microservice.payment_service.controller;


import com.microservice.payment_service.config.VNPayConfig;
import com.microservice.payment_service.dto.PaymentDto;
import com.microservice.payment_service.dto.PaymentVNPayDetailDto;
import com.microservice.payment_service.dto.request.InvoiceDto;
import com.microservice.payment_service.service.impl.PaymentServiceImpl;
import com.microservice.payment_service.service.impl.PaymentVNPayDetailServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentServiceImpl paymentService;
    private final PaymentVNPayDetailServiceImpl paymentVNPayDetailService;

    public PaymentController(PaymentServiceImpl paymentService, PaymentVNPayDetailServiceImpl paymentVNPayDetailService) {
        this.paymentService = paymentService;
        this.paymentVNPayDetailService = paymentVNPayDetailService;
    }

    @PostMapping("/create_payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> paymentData, HttpServletRequest request) throws UnsupportedEncodingException {
        System.out.println("paymentData: " + paymentData);
        if (paymentData == null || !paymentData.containsKey("order")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "fail",
                    "message", "Invalid payment data"
            ));
        }

        Map<String, Object> order = (Map<String, Object>) paymentData.get("order");
        Map<String, Object> orderData = (Map<String, Object>) order.get("data"); // Access the nested 'data' map
        String paymentMethod = (String) orderData.get("paymentMethod");
        Integer invoiceId = (Integer) orderData.get("invoiceId");
        Number totalNumber = (Number) orderData.get("total"); // Safely retrieve total as Number

        if (invoiceId == null || totalNumber == null || paymentMethod == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "fail",
                    "message", "Missing invoiceId, total or paymentMethod"
            ));
        }

        Double total = totalNumber.doubleValue(); // Convert to Double
        InvoiceDto invoiceDto = new InvoiceDto(invoiceId, total);

        // Phân loại phương thức thanh toán
        switch (paymentMethod) {
            case "Bank Transfer":
                // Tạo PaymentDto từ dữ liệu truyền vào
                PaymentDto paymentDto = new PaymentDto();
                paymentDto.setOrderId(invoiceId);
                paymentDto.setPaymentMethod("VNPay");
                paymentDto.setStatus("PENDING");

                paymentService.createPayment(paymentDto);
                return createVNPayPayment(invoiceDto, request);

            case "Cash on Delivery":
                PaymentDto paymentDto1 = new PaymentDto();
                paymentDto1.setOrderId(invoiceId);
                paymentDto1.setPaymentMethod("COD");
                paymentDto1.setStatus("PENDING");
                paymentService.createPayment(paymentDto1);
                return ResponseEntity.ok(Map.of(
                        "status", "ok",
                        "message", "COD payment created successfully"
                ));

            default:
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "fail",
                        "message", "Unsupported payment method: " + paymentMethod
                ));
        }
    }


    public ResponseEntity<?> createVNPayPayment(
            @RequestBody InvoiceDto invoiceDto,
            HttpServletRequest request) throws UnsupportedEncodingException {

        System.out.println("invoiceDto: " + invoiceDto);


        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = String.valueOf(invoiceDto.getInvoiceId());
        String vnp_IpAddr = VNPayConfig.getIpAddress(request);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "other";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf((long) (invoiceDto.getTotal() * 100 * 25000)));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", String.valueOf(invoiceDto.getInvoiceId()));
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Successfully");
        response.put("URL", paymentUrl);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/payment_info")
    public ResponseEntity<?> transaction(@RequestParam String vnp_Amount, @RequestParam String vnp_BankCode,
                                         @RequestParam String vnp_TxnRef, @RequestParam String vnp_OrderInfo,
                                         @RequestParam String vnp_ResponseCode) {
        return ResponseEntity.ok(Map.of(
                "status", vnp_ResponseCode.equals("00") ? "ok" : "fail",
                "message", vnp_ResponseCode.equals("00") ? "Payment Successful" : "Payment Failed",
                "invoiceId", vnp_TxnRef,
                "data", vnp_OrderInfo
        ));
    }
    //   http://localhost:8081/api/payment/payment-return?vnp_Amount=322000000&vnp_BankCode=NCB&vnp_BankTranNo=VNP14926649&vnp_CardType=ATM&vnp_OrderInfo=15&vnp_PayDate=20250425172635&vnp_ResponseCode=00&vnp_TmnCode=57322TUD&vnp_TransactionNo=14926649&vnp_TransactionStatus=00&vnp_TxnRef=15&vnp_SecureHash=4cc2a63a990b6f43c9feff2ac3c8051514923cc86fa3e93aab8aaf3ac3b8ebe08c45b9cfa90e759270671f089645d2e14a66e529aa2311a3f48f5a2a755392c1
    @GetMapping("/payment-return")
    public ResponseEntity<?> handlePaymentReturn(
            @RequestParam String vnp_Amount,
            @RequestParam String vnp_BankCode,
            @RequestParam String vnp_TxnRef,
            @RequestParam String vnp_OrderInfo,
            @RequestParam String vnp_ResponseCode,
            @RequestParam String vnp_TransactionNo,
            @RequestParam String vnp_PayDate,
            @RequestParam String vnp_SecureHash) {

        // sử lý vnp_oderInfo lấy ra id
        System.out.println("vnp_OrderInfo: " + vnp_OrderInfo);

        PaymentDto p =  paymentService.getPaymentByOrderId(Integer.parseInt(vnp_OrderInfo));


        if (vnp_ResponseCode.equals("00")) {
            PaymentVNPayDetailDto dto = new PaymentVNPayDetailDto();
            dto.setPaymentId(p.getPaymentId());
            dto.setTransactionId(vnp_TransactionNo);
            dto.setBankCode(vnp_BankCode);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime paymentTime = LocalDateTime.parse(vnp_PayDate, formatter);
            dto.setPaymentTime(paymentTime);
            dto.setVnPayResponse(vnp_ResponseCode);
            System.out.println(dto);

            paymentVNPayDetailService.createPaymentVNPayDetail(dto);



            //UPDATE PAYMENT
            p.setStatus("SUCCESS");
            paymentService.updatePayment(p);
            // chuyển trang

            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "Payment Successful",
                    "invoiceId", vnp_TxnRef,
                    "data", vnp_OrderInfo
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "status", "fail",
                    "message", "Payment Failed",
                    "invoiceId", vnp_TxnRef,
                    "data", vnp_OrderInfo
            ));

        }


    }

}

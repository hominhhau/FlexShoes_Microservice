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
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentServiceImpl paymentService;
    private final PaymentVNPayDetailServiceImpl paymentVNPayDetailService;

    public PaymentController(PaymentServiceImpl paymentService, PaymentVNPayDetailServiceImpl paymentVNPayDetailService) {
        this.paymentService = paymentService;
        this.paymentVNPayDetailService = paymentVNPayDetailService;
    }

    @PostMapping("/create_payment")
    public ResponseEntity<?> createPayment(@RequestBody PaymentDto paymentDto, HttpServletRequest request) throws UnsupportedEncodingException {
        if ("VNPay".equalsIgnoreCase(paymentDto.getPaymentMethod())) {

            // get data invoice to invoiceID

            InvoiceDto invoiceDto = (InvoiceDto) paymentService.getInfoOrder(paymentDto.getOrderId());

            if (invoiceDto == null) {
                // thong báo lôi
                ResponseEntity.badRequest().body(invoiceDto);
            } else {
                paymentService.createPayment(paymentDto);
                return createVNPayPayment(invoiceDto, request);
            }
        }

        Boolean result = paymentService.createPayment(paymentDto);
        return result ? ResponseEntity.ok(paymentDto) : ResponseEntity.badRequest().body(paymentDto);
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
        vnp_Params.put("vnp_Amount", String.valueOf((long) (invoiceDto.getTotal() * 100)));
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
//http://localhost:8081/api/payment/payment-return?vnp_Amount=80500000&vnp_BankCode=NCB&vnp_BankTranNo=VNP14840014&vnp_CardType=ATM&vnp_OrderInfo=11&vnp_PayDate=20250311124610&vnp_ResponseCode=00&vnp_TmnCode=57322TUD&vnp_TransactionNo=14840014&vnp_TransactionStatus=00&vnp_TxnRef=11&vnp_SecureHash=599e78908b1f1ba80b46436394a398ee51a1b8ac5a3ba0bc5aca12a090c4f2b91eba2241fee8bf3f0a8fdead3789950bee2d52f6e21699502b634b576190f165
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

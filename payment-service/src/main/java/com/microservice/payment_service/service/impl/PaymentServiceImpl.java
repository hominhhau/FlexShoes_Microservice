package com.microservice.payment_service.service.impl;

import com.microservice.payment_service.constant.AppConstant;
import com.microservice.payment_service.dto.PaymentDto;
import com.microservice.payment_service.dto.request.InvoiceDto;
import com.microservice.payment_service.entity.Payment;
import com.microservice.payment_service.repository.PaymentRepository;
import com.microservice.payment_service.service.PaymentService;
import com.microservice.payment_service.mapper.PaymentMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentServiceImpl implements PaymentService {

  PaymentRepository paymentRepository;
  RestTemplate restTemplate;
  com.microservice.payment_service.mapper.PaymentMapper paymentMapper;

    @Override
    @Transactional
    public boolean createPayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.paymentDtoToPayment(paymentDto);
        paymentRepository.save(payment);
        return true;
        }



    @Override
    public boolean deletePayment(int paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            return false;
        }
        paymentRepository.deleteById(paymentId);
        return true;
    }

    @Override
    public boolean updateStatusByPaymentId(int paymentId, String status) {
        return false;
    }

    @Override
    public Boolean updatePayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.paymentDtoToPayment(paymentDto);
        paymentRepository.save(payment);
        return true;
    }

//    @Override
//    public boolean updateStatusByPaymentId(int paymentId, int status) {
//
//        if(!paymentRepository.existsById(paymentId)) {
//            return false;
//        }else{
//            paymentRepository.updateStatusByPaymentId(paymentId, status);
//        }
//
//    }



    @Override
    public InvoiceDto getInfoOrder(int orderId) {
        System.out.println("orderId: " + orderId);
        String url = AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_HOST + orderId;
        System.out.println("url: " + url);

        // Gửi yêu cầu GET và nhận dữ liệu dưới dạng Map
        Map<String, Object> map = restTemplate.getForObject(url, Map.class);
        System.out.println("map: " + map);

        // Lấy phần kết quả từ map
        Map<String, Object> result = (Map<String, Object>) map.get("result");

        // Tạo đối tượng InvoiceDto và gán các giá trị vào các thuộc tính
        InvoiceDto invoiceDto = new InvoiceDto();

        // Gán các thuộc tính cơ bản vào InvoiceDto
        invoiceDto.setInvoiceId((Integer) result.get("invoiceId"));
        invoiceDto.setTotal((Double) result.get("total"));

        return invoiceDto;
    }

    @Override
    public PaymentDto getPaymentByOrderId(int orderId) {
      return paymentMapper.paymentToPaymentDto(paymentRepository.findByOrderId(orderId));
    }


}




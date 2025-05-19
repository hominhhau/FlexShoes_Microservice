package com.microservice.notification_service.service;

import com.microservice.notification_service.dto.reponse.EmailResponse;
import com.microservice.notification_service.dto.request.EmailRequest;
import com.microservice.notification_service.dto.request.Recipient;
import com.microservice.notification_service.dto.request.SendEmailRequest;
import com.microservice.notification_service.dto.request.Sender;
import com.microservice.notification_service.repository.httpclient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    @Autowired
    private Environment env;

    EmailClient emailClient;



    public String getConfigImport() {
        return env.getProperty("SENDINBLUE_API_KEY");
    }

    public EmailResponse sendEmail(SendEmailRequest request) {

        String apiKey = getConfigImport();

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("SENDINBLUE_API_KEY is not configured");
        }

        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("Flex Shoes")
                        .email("chautinh05122@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        try {


            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e){
            throw new ApplicationContextException("Failed to send email", e);
        }
    }

    public void sendRegistrationSuccessEmail(Recipient recipient) {
        String subject = "Đăng ký tài khoản thành công";
        String htmlContent = "<html>\n" +
                "      <body style=\"margin:0;padding:0;font-family:'Segoe UI',sans-serif;background-color:#f4f4f4;\">\n" +
                "        <table width=\"100%\" bgcolor=\"#f4f4f4\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "          <tr>\n" +
                "            <td align=\"center\">\n" +
                "              <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#ffffff\" style=\"margin:20px auto;border-radius:10px;overflow:hidden;box-shadow:0 0 10px rgba(0,0,0,0.1);\">\n" +
                "                <tr>\n" +
                "                  <td bgcolor=\"#1e88e5\" style=\"padding:20px;text-align:center;color:#ffffff;\">\n" +
                "                    <h1 style=\"margin:0;font-size:24px;\">\uD83C\uDF89 Flex Shoes - Chào mừng bạn!</h1>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                  <td style=\"padding:30px;\">\n" +
                "                    <p style=\"font-size:16px;line-height:1.6;color:#333333;\">Xin chào <strong>" + recipient.getName() + "</strong>,</p>\n" +
                "                    <p style=\"font-size:16px;line-height:1.6;color:#333333;\">\n" +
                "                      Bạn đã đăng ký tài khoản thành công trên hệ thống <strong>Flex Shoes</strong>. Chúng tôi rất vui khi có bạn đồng hành!\n" +
                "                    </p>\n" +
                "                    <p style=\"font-size:16px;line-height:1.6;color:#333333;\">\n" +
                "                      Hãy khám phá những sản phẩm giày thể thao mới nhất, các chương trình ưu đãi và nhiều điều tuyệt vời khác.\n" +
                "                    </p>\n" +
                "                    <div style=\"text-align:center;margin:30px 0;\">\n" +
                "                      <a href=\"https://flexshoes.io.vn\" style=\"background-color:#1e88e5;color:#ffffff;text-decoration:none;padding:12px 24px;border-radius:5px;font-size:16px;display:inline-block;\">\n" +
                "                        Khám phá ngay\n" +
                "                      </a>\n" +
                "                    </div>\n" +
                "                    <p style=\"font-size:14px;color:#777777;\">Nếu bạn có bất kỳ câu hỏi nào, hãy liên hệ với chúng tôi qua email hoặc số điện thoại hỗ trợ.</p>\n" +
                "                    <p style=\"font-size:16px;line-height:1.6;color:#333333;\">Trân trọng,<br/>Đội ngũ Flex Shoes</p>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                  <td bgcolor=\"#eeeeee\" style=\"padding:15px;text-align:center;font-size:12px;color:#999999;\">\n" +
                "                    © 2025 Flex Shoes. All rights reserved.\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </body>\n" +
                "    </html>";

        SendEmailRequest request = SendEmailRequest.builder()
                .to(recipient)
                .subject(subject)
                .htmlContent(htmlContent)
                .build();

        sendEmail(request);
    }
}
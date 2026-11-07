package com.webdev.webdevelopment.auth.LoginRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;

//    @Value("${resend.api.key}")
//    private String resendApiKey;
//
//    @Value("${app.mail.from}")
//    private String fromEmail;


    private String resendApiKey="re_Rb8gpQVa_HuYFdB8NzDhmVVjM6mJaWXhV";


    private String fromEmail="support@mail.inoviqtech.in";


    public EmailService() {
        this.restClient = RestClient.create();
    }

    public void sendVerificationCode(String email, String code) {

        String htmlContent = """
                <!DOCTYPE html>
                <html>
                <body style="margin:0;padding:0;background-color:#EEF2F8;
                            font-family:Arial,Helvetica,sans-serif;color:#101C35;">

                  <div style="max-width:600px;margin:30px auto;
                              background:#FFFFFF;border-radius:12px;
                              overflow:hidden;border:1px solid #D7E1EF;">

                    <div style="background:#101C35;padding:28px 30px;
                                color:#FFFFFF;">
                      <div style="font-size:28px;font-weight:bold;">
                        A+ AnantPlus
                      </div>
                      <div style="font-size:12px;letter-spacing:4px;
                                  color:#91B8F0;margin-top:6px;">
                        DEVELOPMENT
                      </div>
                    </div>

                    <div style="padding:35px 30px;">

                      <div style="font-size:26px;font-weight:bold;
                                  margin-bottom:18px;color:#101C35;">
                        Verify your email address
                      </div>

                      <p style="font-size:15px;line-height:1.8;
                                color:#405574;">
                        Welcome to AnantPlus! Please use the verification
                        code below to complete your account registration.
                      </p>

                      <div style="margin:28px 0;padding:25px 15px;
                                  text-align:center;background:#EEF4FC;
                                  border:1px solid #C9D9F0;border-radius:10px;">

                        <div style="font-size:12px;font-weight:bold;
                                    letter-spacing:3px;color:#244A82;">
                          YOUR VERIFICATION CODE
                        </div>

                        <div style="font-size:36px;font-weight:bold;
                                    letter-spacing:8px;color:#101C35;
                                    margin:18px 0;">
                          %s
                        </div>

                        <div style="font-size:13px;color:#48658E;">
                          This code expires in 10 minutes.
                        </div>
                      </div>

                      <p style="font-size:14px;line-height:1.8;
                                color:#405574;">
                        For your security, never share this code with anyone.
                        If you did not request an account, you can ignore
                        this email.
                      </p>

                      <hr style="border:none;border-top:1px solid #D7E1EF;
                                 margin:28px 0;">

                      <p style="font-size:13px;line-height:1.7;
                                color:#48658E;">
                        Need help? Contact the AnantPlus support team
                        through the official website.
                      </p>
                    </div>

                    <div style="background:#101C35;padding:22px 30px;
                                color:#B8C9E3;font-size:12px;line-height:1.8;">
                      <strong style="color:#FFFFFF;">AnantPlus Development</strong>
                      <br>
                      Thoughtful websites and software for growing teams.
                      <br>
                      Automated account security email. Please do not reply.
                    </div>

                  </div>
                </body>
                </html>
                """.formatted(code);

        String textContent =
                "Your AnantPlus verification code is: " + code
                        + ". It expires in 10 minutes.";

        try {
            restClient.post()
                    .uri("https://api.resend.com/emails")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + resendApiKey
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", fromEmail,
                            "to", List.of(email),
                            "subject", "Verify Your Email | AnantPlus",
                            "html", htmlContent,
                            "text", textContent
                    ))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientException e) {
            throw new IllegalStateException(
                    "Failed to send verification email through Resend.",
                    e
            );
        }
    }
}
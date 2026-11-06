package com.webdev.webdevelopment.Services;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/services")
public class MyServiceController {
    private final MyServiceService myServicesService;


    public MyServiceController(MyServiceService myServicesService) {
        this.myServicesService = myServicesService;
    }

    @GetMapping("/my-services")
    public ResponseEntity<List<MyServiceResponse>> getMyServices(
            Authentication authentication) {

        String email = authentication.getName();

        List<MyServiceResponse> services =
                myServicesService.getMyServices(email);

        return ResponseEntity.ok(services);
    }
    @GetMapping
    public ResponseEntity<List<MyServiceResponse>> getAllServices() {

        List<MyServiceResponse> services =
                myServicesService.getAllServices();

        return ResponseEntity.ok(services);
    }
}

package com.webdev.webdevelopment.Services;

import com.webdev.webdevelopment.order.OrderEntity;
import com.webdev.webdevelopment.order.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MyServiceService {
    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;

    public MyServiceService(OrderRepository orderRepository, ServiceRepository serviceRepository) {
        this.orderRepository = orderRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional()
    public List<MyServiceResponse> getMyServices(String email) {

        List<OrderEntity> orders = orderRepository.findMyOrders(email);

        return orders.stream()

                // Get all items from every order
                .flatMap(order -> order.getItems().stream())

                // Convert each OrderItem into MyServiceResponse
                .map(item -> new MyServiceResponse(

                        item.getOrder().getId(),

                        item.getService().getId(),

                        item.getServiceTitle(),

                        item.getOrder().getStatus(),

                        item.getOrder().getCreatedAt(),

                        item.getPrice(),

                        item.getSubtotal(),

                        item.getQuantity(),

                        item.getService().getFeatures()

                ))

                .toList();
    }

    public List<MyServiceResponse> getAllServices() {

        List<ServiceEntity> services = serviceRepository.findAll();

        return services.stream()
                .map(service -> {
                    MyServiceResponse response = new MyServiceResponse();

                    response.setId(service.getId());
                    response.setTitle(service.getTitle());
                    response.setPrice(service.getPrice());

                    return response;
                })
                .toList();
    }
}

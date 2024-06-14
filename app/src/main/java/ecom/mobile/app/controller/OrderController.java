package ecom.mobile.app.controller;

import ecom.mobile.app.model.Cart;
import ecom.mobile.app.model.ItemOrder;
import ecom.mobile.app.model.Order;
import ecom.mobile.app.model.User;
import ecom.mobile.app.security.CustomUserDetails;
import ecom.mobile.app.service.serviceInterface.OrderService;
import ecom.mobile.app.service.serviceInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;

    private User getUserRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return userService.findByAccountEmail(customUserDetails.getEmail());
    }

    @PostMapping("/create-order")
    public Order createOrder(@RequestBody Order order) {
        order.setUser(getUserRequest());
        order.getItemOrders().forEach(itemOrder ->
                itemOrder.setOrder(order));
        return orderService.createOrder(order);
    }

    @GetMapping("/order-all")
    public List<Order> getOrdersByUser() {
        return orderService.getOrdersByUserId(getUserRequest().getId());
    }

    @GetMapping("/orders")
    public List<Order> getOrdersByUserAndStatus(@RequestParam("status") String status) {
        return orderService.getOrdersByUserIdAndStatus(
                getUserRequest().getId(),
                status
        );
    }

    @GetMapping("/orders/search")
    public List<Order> getOrdersByUserAndProductName(@RequestParam("key") String productName) {
        return orderService.getOrderByUserIdAndProductName(
                getUserRequest().getId(),
                productName
        );
    }

    @GetMapping("/order/get/{order_id}")
    public Order getOrderById(@PathVariable("order_id") int order_id) {
        return orderService.getOrderById(order_id);
    }

    @PostMapping("/order/update/{order_id}")
    void updateOrderStatus(@PathVariable("order_id") int order_id, @RequestBody String status) {
        orderService.updateOrderStatus(order_id, status);
    }
}

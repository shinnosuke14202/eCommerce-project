package ecom.mobile.app.service.serviceInterface;

import ecom.mobile.app.model.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);

    List<Order> getAllOrder();

    List<Order> getOrdersByUserId(int uid);

    List<Order> getOrdersByUserIdAndStatus(int uid, String status);

    List<Order> getOrderByUserIdAndProductName(int uid, String productName);

    Order getOrderById(int oid);

    void updateOrderStatus(int oid, String status);

}

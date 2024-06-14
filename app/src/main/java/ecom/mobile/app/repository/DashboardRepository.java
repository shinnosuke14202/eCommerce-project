package ecom.mobile.app.repository;

import ecom.mobile.app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Order, Integer> {

    @Query(
            value = "SELECT min(o.id) as id, DATE(create_time) as order_date, SUM(total_price) AS total_price " +
                    "FROM orders o " +
                    "INNER JOIN payment p ON p.id = o.pay_id AND DATE(create_time) BETWEEN ?1 AND ?2 " +
                    "GROUP BY DATE(create_time) " +
                    "order by DATE(create_time) ASC",
            nativeQuery = true
    )
    public List<Object[]> getRevenueForDashboard(String startDate, String endDate);

    @Query(
            value = "select id, click_after_suggest_byai, title from product p order by click_after_suggest_byai desc limit ?1",
            nativeQuery = true
    )
    public List<Object[]> getSuggestByAiData(int number);

}

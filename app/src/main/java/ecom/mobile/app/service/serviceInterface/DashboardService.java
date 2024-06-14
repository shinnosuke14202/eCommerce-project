package ecom.mobile.app.service.serviceInterface;

import ecom.mobile.app.model.Order;
import ecom.mobile.app.model.Revenue;
import ecom.mobile.app.model.SuggestAi;

import java.util.List;

public interface DashboardService {
    List<Revenue> getRevenueForDashboard(String startDate, String endDate);

    List<SuggestAi> getSuggestByAiData(int number);

}

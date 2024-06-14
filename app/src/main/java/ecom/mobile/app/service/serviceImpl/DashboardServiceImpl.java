package ecom.mobile.app.service.serviceImpl;

import ecom.mobile.app.model.Order;
import ecom.mobile.app.model.Revenue;
import ecom.mobile.app.model.SuggestAi;
import ecom.mobile.app.repository.DashboardRepository;
import ecom.mobile.app.service.serviceInterface.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Override
    public List<Revenue> getRevenueForDashboard(String startDate, String endDate) {
        List<Object[]> result = dashboardRepository.getRevenueForDashboard(startDate, endDate);
        List<Revenue> revenueList = new ArrayList<>();
        for (Object[] r : result) {

            int id = (int) r[0];
            Date orderDate = (Date) r[1];
            BigDecimal totalPrice = (BigDecimal) r[2];

            Revenue revenue = new Revenue(id, orderDate.toString(), totalPrice.toString());

            revenueList.add(revenue);
        }
        return revenueList;
    }

    @Override
    public List<SuggestAi> getSuggestByAiData(int number) {
        List<Object[]> result = dashboardRepository.getSuggestByAiData(number);
        List<SuggestAi> suggestAiList = new ArrayList<>();
        for (Object[] r : result) {

            int id = (int) r[0];
            int click_after_suggest_byai = (int) r[1];
            String title = (String) r[2];

            SuggestAi suggestAi = new SuggestAi(id, click_after_suggest_byai, title);

            suggestAiList.add(suggestAi);
        }
        return suggestAiList;
    }

}

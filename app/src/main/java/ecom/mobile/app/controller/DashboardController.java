package ecom.mobile.app.controller;

import ecom.mobile.app.model.Revenue;
import ecom.mobile.app.model.SuggestAi;
import ecom.mobile.app.service.serviceInterface.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard/revenue/{startDate}/{endDate}")
    public List<Revenue> getRevenueForDashboard(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        return dashboardService.getRevenueForDashboard(startDate, endDate);
    }

    @GetMapping("/dashboard/suggestAi/{number}")
    public List<SuggestAi> getSuggestByAiData(@PathVariable("number") int number) {
        return dashboardService.getSuggestByAiData(number);
    }

}

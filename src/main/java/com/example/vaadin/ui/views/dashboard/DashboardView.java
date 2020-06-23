package com.example.vaadin.ui.views.dashboard;

import com.example.vaadin.backend.service.CompanyService;
import com.example.vaadin.backend.service.ContactService;
import com.example.vaadin.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Map;

@PageTitle("Dashboard | Contact Manager")
@Route(value = "dashboard",layout = MainLayout.class)
public class DashboardView extends VerticalLayout {
    private final ContactService contactService;
    private final CompanyService companyService;

    public DashboardView(ContactService contactService, CompanyService companyService) {
        this.contactService  = contactService;
        this.companyService  = companyService;
        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        add(
                getContactStats(),
                getCompanyChart()
        );


    }

    private Component getCompanyChart() {
        Chart chart = new Chart(ChartType.PIE);
        DataSeries dataSeries = new DataSeries();
        Map<String,Integer> stats = companyService.getStats();
        stats.forEach((companyName, empCount) -> dataSeries.add(new DataSeriesItem(companyName,empCount)));
        chart.getConfiguration().setSeries(dataSeries);
        return  chart;

    }

    private Span getContactStats() {
        Span stats = new Span(contactService.count() + " contacts");
        stats.addClassName("contact-stats");
        return stats;

    }
}

package com.github.vanroy.cloud.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.InternalResourceView;

/**
 * Created by julien on 06/11/15.
 */
@Controller
public class DashboardController {

    @RequestMapping("${spring.cloud.dashboard.context:/}")
    public InternalResourceView home() {
        return new InternalResourceView("dashboard.html");
    }
}

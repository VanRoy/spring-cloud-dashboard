package com.github.vanroy.cloud.dashboard.controller;

import com.github.vanroy.cloud.dashboard.config.CloudDashboardProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

/**
 * Dashboard home controller
 * @author Julien Roy
 */
@Controller
public class DashboardController {

    @Autowired
    CloudDashboardProperties properties;

    @RequestMapping("${spring.cloud.dashboard.context:/}")
    public ModelAndView home() {
        return new ModelAndView("dashboard")
            .addObject("refreshTimeout", properties.getRefreshTimeout());
    }
}

package com.github.vanroy.cloud.dashboard.controller;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewController.class);

	private final String CSS_FILE_PROP = "eureka.dashboard.customCssPath";
	private final String DEFAULT_CSS_FILE = "redirect:/css/custom-style.css";

	@Resource
	private Environment env;

	private String cssPath;

	@PostConstruct
	public void init() {
		cssPath = env.getProperty(CSS_FILE_PROP);

		if (cssPath == null || !new ClassPathResource(cssPath).exists())
			cssPath = DEFAULT_CSS_FILE;

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Custom CSS for Eureka Dashboard will be loaded from : " + cssPath + ".");
	}

	@RequestMapping("/custom-style")
	public String getCss() {
		return cssPath;
	}
}

package com.project.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/our-agent")
    public String ourAgentPage() {
        return "our-agent";
    }

    @GetMapping("viewdetails")
    public String viewdetails() {
        return "viewdetails";
    }
}


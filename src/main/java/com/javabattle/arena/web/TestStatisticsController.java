package com.javabattle.arena.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestStatisticsController {
    
    @GetMapping("/statistics")
    public String statisticsPage() {
        return "statistics";
    }
}

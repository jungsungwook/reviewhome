package com.memeki.reviewhome.error.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping(value = "/error")
public class ErrorApiController {
    @GetMapping("/")
    public String getMethodName(@RequestParam String param) {
        return param;
    }
    
}

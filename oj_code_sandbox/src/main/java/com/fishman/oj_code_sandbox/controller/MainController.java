package com.fishman.oj_code_sandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MainController {


    @GetMapping("/heath")
    public String healthCheck(){
        return "ok";
    }
}

package com.github.deenr.contribu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class TestController {
    @GetMapping("/test")
    public String test() {
        return "Server is up and running!";
    }
}

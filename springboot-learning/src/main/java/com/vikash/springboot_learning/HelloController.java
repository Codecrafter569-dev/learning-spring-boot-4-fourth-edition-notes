package com.vikash.springboot_learning;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String home() {
        return "This is my spring Boot .";
    }
    @GetMapping("/Bolo")
    public String bye(){
      return "This is my New Program";
    }
    @GetMapping("/name")
    public String name(){
      return "<h1>My Name is Vikash Kumar</h1>";
    }

}
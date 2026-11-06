package com.vikash.springboot_learning;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class WebContoler{
  @GetMapping("/hello/index")
  public String hello(){
    return "index";
  }
}
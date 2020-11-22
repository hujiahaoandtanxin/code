package com.outPost.controller;

import com.outPost.reposity.JgnameReposity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
public class MyInterfaceController {

    @Autowired
    JgnameReposity jgnameReposity;

    @RequestMapping("/omss/viewEmpByName")
    public List<String> viewEmpByName(String name){
        return jgnameReposity.findByName(name);
    }

    @RequestMapping("/test")
    public  String  test(){
        return "test";
    }
}

package com.es.Controller;

import com.es.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EsController {

    @Autowired
    EsService esService;

    @RequestMapping("/queryEsAll")
    public String testQueryAll(){
        return esService.testQueryAll();
    }


}

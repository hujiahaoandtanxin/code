package com.outPost.controller;

import com.jiahao.export.BuilderFactory;
import com.jiahao.export.Decorator;
import com.jiahao.markdown.Block;
import com.outPost.service.MdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MdController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MdService mdService;

    @RequestMapping("/md/save")
    public void savaMarkdownText(String content){
        String outputFilePath = "/Users/hujh/Downloads/mdtest.docx";
        List<Block> list = mdService.analyze(content);
        String ext = getExtOfFile(outputFilePath);
        Decorator decorator = BuilderFactory.build(ext);
        decorator.beginWork(outputFilePath);
        decorator.decorate(list);
        decorator.afterWork(outputFilePath);
    }

    private static String getExtOfFile(String outputFilePath) {
        if (outputFilePath == null) {
            return "";
        }
        int i = outputFilePath.lastIndexOf(".");
        if (i < 0) {
            return "";
        }
        return outputFilePath.substring(i + 1);
    }

}

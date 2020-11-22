package com.utils;


import com.jiahao.export.FileFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class MdUtils {

    public static void main(String[] args) {
        try {
            FileFactory.produce(new File("/Users/hujh/Downloads/mdtest.md"), "/Users/hujh/Downloads/mdtest.docx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}

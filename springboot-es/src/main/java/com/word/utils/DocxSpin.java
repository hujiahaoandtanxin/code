package com.word.utils;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class DocxSpin {

    public static void main(String[] args) {
        String path = "/Users/hujh/Downloads/docx案例.docx";
        int docxCount = getDocxCount(path);
        System.out.println("文档共段落数:"+docxCount);
        for (int i = 0;i<docxCount;i++){
            creatNewDocx(path,i,docxCount);
        }
    }

    public static void creatNewDocx(String path,int savePos,int docxCount){
        FileInputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(path);
            XWPFDocument doc = new XWPFDocument(in);//得到word文档的信息
            List<IBodyElement> bodyElements = doc.getBodyElements();
            System.out.println(bodyElements.size());
            int delPos = 0;
            for(int i =0;i<docxCount;i++){
                if(i == savePos){
                    delPos++;
                }
                doc.removeBodyElement(delPos);
            }
            System.out.println(bodyElements.size());
            out = new FileOutputStream("/Users/hujh/Downloads/合并/docx案例"+savePos
                    + ".docx");
            doc.write(out);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getDocxCount(String path){
        FileInputStream in = null;
        int size = 0;
        try {
            in = new FileInputStream(path);
            XWPFDocument doc = new XWPFDocument(in);//得到word文档的信息
            List<IBodyElement> bodyElements = doc.getBodyElements();
            size =  bodyElements.size();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }
}

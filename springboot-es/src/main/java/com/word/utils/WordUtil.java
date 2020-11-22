package com.word.utils;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.springframework.beans.BeanUtils;

import java.io.*;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 对文字段落进行克隆（图片和表格目前没看）
 */
public class WordUtil {

    public static void main(String[] args) {
        test1("/Users/hujh/Downloads/docx案例.docx");
    }

    public static void test1(String filePath){
        int pos = 0;
        int tablePos = 0;
        Map<Integer,XWPFParagraph> map = new LinkedHashMap<>();
        Map<Integer,XWPFTable> tableMap = new LinkedHashMap<>();
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath);
            // 处理docx格式 即office2007以后版本
            if (filePath.toLowerCase().endsWith("docx")) {
                //word 2007 图片不会被读取， 表格中的数据会被放在字符串的最后
                XWPFDocument xwpf = new XWPFDocument(in);//得到word文档的信息
                Iterator<IBodyElement> elementsIterator = xwpf.getBodyElementsIterator();
                while (elementsIterator.hasNext()){
                    IBodyElement element = elementsIterator.next();
                    if(element instanceof XWPFTable){
                        XWPFTable xwpfTable = (XWPFTable)element;
                        tableMap.put(tablePos++,xwpfTable);
                    }else if(element instanceof XWPFParagraph){
                        XWPFParagraph xwpfParagraph = (XWPFParagraph)element;
                        String text = xwpfParagraph.getText();
                        map.put(pos++,xwpfParagraph);
                    }
                }
                createDocxByParagraph(map,"/Users/hujh/Downloads/docx克隆片段-Paragraph.docx");
                createDocxByTable(tableMap,"/Users/hujh/Downloads/docx克隆片段-Table.docx");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createDocxByParagraph(Map<Integer,XWPFParagraph> map,String outPath){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(outPath));
            XWPFDocument document= new XWPFDocument();
            Iterator<Map.Entry<Integer, XWPFParagraph>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                document.createParagraph();
                Map.Entry<Integer, XWPFParagraph> next = iterator.next();
                int pos = next.getKey();
                XWPFParagraph xwpfParagraph = next.getValue();
                document.setParagraph(xwpfParagraph,pos);
            }
            document.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void createDocxByTable(Map<Integer,XWPFTable> map,String outPath){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(outPath));
            XWPFDocument document= new XWPFDocument();
            List<POIXMLDocumentPart> relations = document.getRelations();

            Iterator<Map.Entry<Integer, XWPFTable>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                document.createTable();
                Map.Entry<Integer, XWPFTable> next = iterator.next();
                int pos = next.getKey();
                XWPFTable xwpfTable = next.getValue();
                addBorder(xwpfTable);
                document.setTable(pos,xwpfTable);
            }
            document.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void addBorder(XWPFTable table){
        CTTblBorders borders = table.getCTTbl().getTblPr().addNewTblBorders();
        CTBorder hBorder = borders.addNewInsideH();
        hBorder.setVal(STBorder.Enum.forString("single"));  // 线条类型
        hBorder.setSz(new BigInteger("1")); // 线条大小
        hBorder.setColor("000000"); // 设置颜色

        CTBorder vBorder = borders.addNewInsideV();
        vBorder.setVal(STBorder.Enum.forString("single"));
        vBorder.setSz(new BigInteger("1"));
        vBorder.setColor("000000");

        CTBorder lBorder = borders.addNewLeft();
        lBorder.setVal(STBorder.Enum.forString("single"));
        lBorder.setSz(new BigInteger("1"));
        lBorder.setColor("000000");

        CTBorder rBorder = borders.addNewRight();
        rBorder.setVal(STBorder.Enum.forString("single"));
        rBorder.setSz(new BigInteger("1"));
        rBorder.setColor("000000");

        CTBorder tBorder = borders.addNewTop();
        tBorder.setVal(STBorder.Enum.forString("single"));
        tBorder.setSz(new BigInteger("1"));
        tBorder.setColor("000000");

        CTBorder bBorder = borders.addNewBottom();
        bBorder.setVal(STBorder.Enum.forString("single"));
        bBorder.setSz(new BigInteger("1"));
        bBorder.setColor("000000");
    }
}

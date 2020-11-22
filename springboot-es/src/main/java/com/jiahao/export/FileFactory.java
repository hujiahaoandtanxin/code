package com.jiahao.export;

import com.jiahao.markdown.Block;
import com.jiahao.markdown.MDAnalyzer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 文档生成工厂
 *
 */
public class FileFactory {

    public static String Encoding = "UTF-8";

    public static void produce(File file, String outputFilePath) throws FileNotFoundException, UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Encoding));

        produce(reader, outputFilePath);
    }

    public static void produce(InputStream is, String outputFilePath) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(Encoding)));

        produce(reader, outputFilePath);
    }

    public static void produce(String mdText, String outputFilePath) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new StringReader(mdText));

        produce(reader, outputFilePath);
    }

    public static void produce(BufferedReader reader, String outputFilePath) throws FileNotFoundException {
        List<Block> list = MDAnalyzer.analyze(reader);
        produce(list, outputFilePath);
    }

    public static void produce(List<Block> list, String outputFilePath) throws FileNotFoundException {
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

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

//		WordFactory.produce("#大标题\n###小标题\n```\npublic void main()\n    system.out.println(\"hello\")\n```\n123123\n```\n正文，这里是**_正文_**测试测试**粗体**哦\n> 这里是引用，**粗体**_斜体__**粗斜合一**_**123_12", "/Users/cevin/Downloads/simple4.docx");
        FileFactory.produce(new File("/Users/cevin/Downloads/测试.md"), "/Users/cevin/Downloads/simple.docx");
//		WordFactory.produce("> ###123123\n> 123","/Users/cevin/Downloads/simple2.docx");
    }
}

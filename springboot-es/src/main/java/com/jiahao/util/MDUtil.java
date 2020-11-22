package com.jiahao.util;

import com.jiahao.export.HTMLDecorator;
import com.jiahao.markdown.Block;
import com.jiahao.markdown.MDAnalyzer;

import java.io.*;
import java.util.List;

public class MDUtil {

	public static String markdown2Html(File file){
		BufferedReader reader = null;
		String lineStr = null;
		try {
			reader = new BufferedReader(new FileReader(file));StringBuffer sb = new StringBuffer();
			while ((lineStr = reader.readLine())!=null) {
				sb.append(lineStr).append("\n");
			}
			return markdown2Html(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static String markdown2Html(String mdStr){
		if(mdStr==null){
			return null;
		}
		BufferedReader reader = new BufferedReader(new StringReader(mdStr));
		List<Block> list = MDAnalyzer.analyze(reader);
		
		HTMLDecorator decorator = new HTMLDecorator();
		
		decorator.decorate(list);
		
		return decorator.outputHtml();
	}
	
	public static void main(String[] args) {
//		System.out.println(MDUtil.markdown2Html(new File("test_file/md_for_test.md")));
		System.out.println(MDUtil.markdown2Html(new File("test_file/test_file2.md")));
//		System.out.println(MDUtil.markdown2Html("   [Nodejs 文档](https://nodejs.org/documentation/)"));
	}
}

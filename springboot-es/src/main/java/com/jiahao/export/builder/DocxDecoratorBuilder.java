package com.jiahao.export.builder;

import com.jiahao.export.Decorator;
import com.jiahao.export.DocxDecorator;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DocxDecoratorBuilder implements DecoratorBuilder {

	public Decorator build() {
		return new DocxDecorator(new XWPFDocument());
	}

}

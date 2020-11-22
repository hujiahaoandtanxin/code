package com.jiahao.export.builder;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.jiahao.export.Decorator;
import com.jiahao.export.PDFDecorator5x;

public class PDFDecoratorBuilder5x implements DecoratorBuilder {

	public Decorator build() {
		return new PDFDecorator5x(new Document(PageSize.A4));
	}

}

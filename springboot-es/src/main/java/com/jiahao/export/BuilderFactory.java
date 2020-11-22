package com.jiahao.export;

import com.jiahao.export.builder.DecoratorBuilder;

/**
 * 文档装饰器的工厂，使用反射来生成相应装饰器。使用反射的原因是为了降低MD2File的包耦合度
 * 2015年5月15日 下午10:00:39
 */
public class BuilderFactory {
	
	private static DecoratorBuilder initDecoratorBuilder(String className){
		try {
			@SuppressWarnings("rawtypes")
			Class clazz = Class.forName(className);
			return (DecoratorBuilder)clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String DOC_BUILDER_CLASS_NAME = "com.jiahao.export.builder.DocDecoratorBuilder";
	private static final String DOCX_BUILDER_CLASS_NAME = "com.jiahao.export.builder.DocxDecoratorBuilder";
	private static final String PDF_5X_BUILDER_CLASS_NAME = "com.jiahao.export.builder.PDFDecoratorBuilder5x";
	private static final String HTML_BUILDER_CLASS_NAME = "com.jiahao.export.builder.HTMLDecoratorBuilder";
	
	public static Decorator build(String ext) {
		DecoratorBuilder decoratorBuilder;
		if(ext.equalsIgnoreCase("docx")){
			decoratorBuilder = initDecoratorBuilder(DOCX_BUILDER_CLASS_NAME);
		}else if(ext.equalsIgnoreCase("doc")){
			decoratorBuilder = initDecoratorBuilder(DOC_BUILDER_CLASS_NAME);
		}else if(ext.equalsIgnoreCase("pdf")){
			decoratorBuilder = initDecoratorBuilder(PDF_5X_BUILDER_CLASS_NAME);
		}else if(ext.equalsIgnoreCase("html") ||ext.equalsIgnoreCase("htm")){
			decoratorBuilder = initDecoratorBuilder(HTML_BUILDER_CLASS_NAME);
		}else{
			throw new RuntimeException("请确认输出的文档为docx，doc，pdf，html的文档格式");
		}
		Decorator decorator = decoratorBuilder.build();
		return decorator;
	}

}

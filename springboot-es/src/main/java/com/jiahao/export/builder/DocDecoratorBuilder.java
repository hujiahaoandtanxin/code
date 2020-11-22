package com.jiahao.export.builder;

import com.jiahao.export.Decorator;

public class DocDecoratorBuilder implements DecoratorBuilder {

	public Decorator build() {
		throw new RuntimeException("暂未支持word doc文档的导出");
	}

}

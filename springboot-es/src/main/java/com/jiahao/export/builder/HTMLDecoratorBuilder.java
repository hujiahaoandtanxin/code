package com.jiahao.export.builder;

import com.jiahao.export.Decorator;
import com.jiahao.export.HTMLDecorator;

public class HTMLDecoratorBuilder implements DecoratorBuilder {

	public Decorator build() {
		return new HTMLDecorator();
	}

}

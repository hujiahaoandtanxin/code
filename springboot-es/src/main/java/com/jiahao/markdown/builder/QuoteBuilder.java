package com.jiahao.markdown.builder;

import com.jiahao.markdown.BlockType;
import com.jiahao.markdown.MDToken;

public class QuoteBuilder extends ListBuilder {

	public QuoteBuilder(String content){
		super(content, BlockType.QUOTE);
	}

	@Override
	public int computeCharIndex(String str) {
		return str.indexOf(MDToken.QUOTE);
	}
}

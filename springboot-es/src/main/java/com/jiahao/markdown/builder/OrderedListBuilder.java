package com.jiahao.markdown.builder;

import com.jiahao.markdown.BlockType;

public class OrderedListBuilder extends ListBuilder {

	public OrderedListBuilder(String content){
		super(content, BlockType.ORDERED_LIST);
	}
	
	@Override
	public int computeCharIndex(String str) {
		return str.indexOf(" ");
	}

}

package com.jiahao.markdown.builder;

import com.jiahao.markdown.Block;
import com.jiahao.markdown.BlockType;
import com.jiahao.markdown.MDAnalyzer;
import com.jiahao.markdown.ValuePart;

import java.util.List;

public class CommonTextBuilder implements BlockBuilder {

	private String content;
	public CommonTextBuilder(String content){
		this.content = content;
	}
	
	public Block bulid() {
		Block block = new Block();
		
		block.setType(BlockType.NONE);
		List<ValuePart> list = MDAnalyzer.analyzeTextLine(content);
		block.setValueParts(list);
		
		return block;
	}

	public boolean isRightType() {
		return true;
	}

}

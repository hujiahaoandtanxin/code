package com.jiahao.markdown.builder;

import com.jiahao.markdown.Block;
import com.jiahao.markdown.BlockType;
import com.jiahao.markdown.ValuePart;

public class CodeBuilder implements BlockBuilder {

	private String content;
	public CodeBuilder(String content){
		this.content = content;
	}
	
	public Block bulid() {
		Block block = new Block();
		block.setType(BlockType.CODE);
		block.setValueParts(new ValuePart(content));
		return block;
	}

	public boolean isRightType() {
		return false;
	}

}

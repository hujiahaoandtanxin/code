package com.jiahao.export;

import com.jiahao.markdown.Block;

import java.util.List;

public interface Decorator {
	
	public void beginWork(String outputFilePath);
	
	public void decorate(List<Block> list);
	
	public void afterWork(String outputFilePath);
	
}

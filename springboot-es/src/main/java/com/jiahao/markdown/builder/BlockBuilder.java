package com.jiahao.markdown.builder;

import com.jiahao.markdown.Block;

/**
 * markdown语法块
 */
public interface BlockBuilder {

	/**
	 * 创建语法块
	 * @return 结果
	 */
	public Block bulid();
	
	/**
	 * 检查内容是否属于当前语法块
	 * @return 结果
	 */
	public boolean isRightType();
}

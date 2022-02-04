package com.landao.inspector.model.enums;

/**
 * 字符串裁剪类型
 */
public enum TrimType {
    /**
     * 前后都裁剪,一般字段
     */
    All,
    /**
     * 我感觉不会出现这种情况,但是留给你了
     */
    Head,
    /**
     * 一般是长的评论或文章字段
     */
    Trail

}

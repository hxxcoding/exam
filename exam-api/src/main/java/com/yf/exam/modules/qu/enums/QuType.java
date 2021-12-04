package com.yf.exam.modules.qu.enums;


/**
 * 题目类型
 * @author bool 
 * @date 2019-10-30 13:11
 */
public interface QuType {

    /**
     * 单选题
     */
    Integer RADIO = 1;

    /**
     * 多选题
     */
    Integer MULTI = 2;

    /**
     * 判断题
     */
    Integer JUDGE = 3;

    /**
     * 操作题
     */
    Integer SAQ = 4;

    /**
     * 填空题
     */
    Integer BLANK = 5;
}

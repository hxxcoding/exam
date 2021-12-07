package com.yf.exam.modules.exam.enums;


/**
 * 试卷状态
 * @author bool 
 * @date 2019-10-30 13:11
 */
public interface ExamState {


    /**
     * 进行中
     */
    Integer ENABLE = 0;

    /**
     * 已禁用
     */
    Integer DISABLED = 1;

    /**
     * 待开始
     */
    Integer READY_START = 2;

    /**
     * 已结束
     */
    Integer OVERDUE = 3;

    
}

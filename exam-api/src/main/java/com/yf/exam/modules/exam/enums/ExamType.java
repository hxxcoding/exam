package com.yf.exam.modules.exam.enums;

/**
 * <p>
 *    考试类型枚举类
 * </p>
 *
 * @author Xiaoxiao Hu
 * @date 2022/1/22 12:17
 */
public interface ExamType {

    /**
     * 模拟考试 / 练习
     */
    Integer PRACTICE = 0;

    /**
     * 正式考试 / 最终考试
     */
    Integer FINAL_EXAM = 1;
}

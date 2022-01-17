package com.yf.exam.modules.paper.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *  office题得分详情响应类
 * </p>
 *
 * @author Xiaoxiao Hu
 * @date 2022/1/16 14:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value="office题得分详情响应类", description="office题得分详情响应类")
public class PaperQuOfficePointsRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 判分方法
     */
    private String method;

    /**
     * 判分位置
     */
    private String pos;

    /**
     * 题目答案
     */
    private String pointAnswer;

    /**
     * 学生提交的答案
     */
    private String userAnswer;

    /**
     * 题目分数
     */
    private Integer pointScore;

    /**
     * 学生获得的分数
     */
    private Integer userScore;
}

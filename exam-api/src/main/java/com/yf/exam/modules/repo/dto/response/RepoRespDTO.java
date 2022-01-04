package com.yf.exam.modules.repo.dto.response;

import com.yf.exam.modules.repo.dto.RepoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* <p>
* 题库请求类
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 13:23
*/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="题库分页请求类", description="题库分页请求类")
public class RepoRespDTO extends RepoDTO {

    private static final long serialVersionUID = 1L;

}

package com.yf.exam.modules.paper.dto.response;

import com.yf.exam.core.annon.Dict;
import com.yf.exam.modules.paper.dto.PaperDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* <p>
* 试卷详细信息响应类 新增realName userName deptName字段
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 17:31
*/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="试卷详细信息响应类", description="试卷详细信息响应类")
public class PaperDetailRespDTO extends PaperDTO {

    @ApiModelProperty(value = "用户ID", required=true)
    private String userId;

    @ApiModelProperty(value = "用户ID", required=true)
    private String realName;

    @ApiModelProperty(value = "用户ID", required=true)
    private String userName;

    @ApiModelProperty(value = "部门ID", required=true)
    private String departId;

    @ApiModelProperty(value = "部门名称", required=true)
    private String deptName;


}

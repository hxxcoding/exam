package com.yf.exam.ability.upload.dto;

import com.yf.exam.core.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 删除文件请求
 * @author Xiaoxiao Hu
 * @date 2021-12-04 23:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="文件删除请求", description="文件删除请求")
public class FileUrlReqDTO extends BaseDTO{

    @ApiModelProperty(value = "文件完整的URL地址", required=true)
    private String url;

}

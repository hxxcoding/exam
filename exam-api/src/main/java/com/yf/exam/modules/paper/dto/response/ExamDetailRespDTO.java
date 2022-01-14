package com.yf.exam.modules.paper.dto.response;

import com.yf.exam.modules.paper.dto.PaperDTO;
import com.yf.exam.modules.paper.dto.ext.OnlinePaperQuDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Calendar;
import java.util.List;

@Data
@ApiModel(value="考试详情", description="考试详情")
public class ExamDetailRespDTO extends PaperDTO {


    @ApiModelProperty(value = "单选题列表", required=true)
    private List<OnlinePaperQuDTO> radioList;

    @ApiModelProperty(value = "多选题列表", required=true)
    private List<OnlinePaperQuDTO> multiList;

    @ApiModelProperty(value = "判断题列表", required=true)
    private List<OnlinePaperQuDTO> judgeList;

    @ApiModelProperty(value = "简答题列表", required=true)
    private List<OnlinePaperQuDTO> saqList;

    @ApiModelProperty(value = "填空题列表", required=true)
    private List<OnlinePaperQuDTO> blankList;

    @ApiModelProperty(value = "word操作题列表", required=true)
    private List<OnlinePaperQuDTO> wordList;

    @ApiModelProperty(value = "excel操作题列表", required=true)
    private List<OnlinePaperQuDTO> excelList;

    @ApiModelProperty(value = "ppt操作题列表", required=true)
    private List<OnlinePaperQuDTO> pptList;


    @ApiModelProperty(value = "剩余结束秒数", required=true)
    public Long getLeftSeconds(){

        // 结束时间
        Calendar cl = Calendar.getInstance();
        cl.setTime(this.getLimitTime());

        return (cl.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

}

package com.yf.exam.modules.paper.dto.export;

import com.yf.exam.core.api.dto.BaseDTO;
import com.yf.exam.core.utils.excel.annotation.ExcelField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *  用于试卷成绩导出的数据结构
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/1/25 15:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PaperExportDTO extends BaseDTO {

    @ExcelField(title = "序号", align = 2, sort = 1)
    private Integer no;
    @ExcelField(title = "考试名称", align = 2, sort = 2)
    private String title;
    @ExcelField(title = "考生姓名", align = 2, sort = 3)
    private String realName;
    @ExcelField(title = "学号", align = 2, sort = 4)
    private String userName;
    @ExcelField(title = "班级", align = 2, sort = 5)
    private String deptName;
    @ExcelField(title = "考场座位号", align = 2, sort = 6)
    private String seat;
    @ExcelField(title = "考试时间", align = 2, sort = 7)
    private String timeRange;
    @ExcelField(title = "考试用时(min)", align = 2, sort = 8)
    private Integer userTime;
    @ExcelField(title = "单选题得分", align = 2, sort = 9)
    private Integer radioScore;
    @ExcelField(title = "多选题得分", align = 2, sort = 10)
    private Integer MultiScore;
    @ExcelField(title = "判断题得分", align = 2, sort = 11)
    private Integer JudgeScore;
    @ExcelField(title = "填空题得分", align = 2, sort = 12)
    private Integer blankScore;
    @ExcelField(title = "操作题得分", align = 2, sort = 13)
    private Integer officeScore;
    @ExcelField(title = "试卷总分", align = 2, sort = 14)
    private Integer titleScore;
}

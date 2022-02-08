package com.yf.exam.modules.sys.user.dto.export;

import com.yf.exam.core.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * @author Xiaoxiao Hu
 */

@Data
public class SysUserExportDTO {

    private static final long serialVersionUID = 1L;

    @ExcelField(title="选课号", align=2, sort=1)
    private String deptName;

    @ExcelField(title="学号", align=2, sort=2)
    private String userName;

    @ExcelField(title="姓名", align=2, sort=3)
    private String realName;

    @ExcelField(title="密码", align=2, sort=4)
    private String password;

    private String departId;
}

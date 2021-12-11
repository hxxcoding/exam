package com.yf.exam.modules.sys.user.dto.export;

import com.yf.exam.core.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * @author Xiaoxiao Hu
 */

@Data
public class SysUserExportDTO {

    private static final long serialVersionUID = 1L;

    @ExcelField(title="序号", align=2, sort=1)
    private String no;

    @ExcelField(title="姓名", align=2, sort=2)
    private String realName;

    @ExcelField(title="班级", align=2, sort=3)
    private String deptName;

    private String departId;

    @ExcelField(title="学号", align=2, sort=4)
    private String userName;

    @ExcelField(title="密码", align=2, sort=5)
    private String password;
}

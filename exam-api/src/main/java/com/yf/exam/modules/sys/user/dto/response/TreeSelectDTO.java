package com.yf.exam.modules.sys.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yf.exam.modules.sys.user.entity.SysMenu;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单树结构响应类
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022-01-12
 */
@Data
@ApiModel(value="选择树结构响应类", description="选择树结构响应类")
public class TreeSelectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private String id;

    /** 节点名称 */
    private String label;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelectDTO> children;

    public TreeSelectDTO(SysMenu menu) {
        this.id = menu.getId();
        this.label = menu.getName();
        this.children = menu.getChildren().stream().map(TreeSelectDTO::new).collect(Collectors.toList());
    }
}

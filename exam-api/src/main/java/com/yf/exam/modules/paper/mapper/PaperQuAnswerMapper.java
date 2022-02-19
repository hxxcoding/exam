package com.yf.exam.modules.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yf.exam.modules.paper.dto.ext.PaperQuAnswerExtDTO;
import com.yf.exam.modules.paper.entity.PaperQuAnswer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* 试卷考题备选答案Mapper
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 16:33
*/
public interface PaperQuAnswerMapper extends BaseMapper<PaperQuAnswer> {

    /**
     * 查找试卷试题答案列表
     * @deprecated 已经使用应用层单表查询组合替代
     * @param paperId 试卷id
     * @param quId 试题id
     * @return 试题选项
     */
    List<PaperQuAnswerExtDTO> list(@Param("paperId") String paperId, @Param("quId") String quId);
}

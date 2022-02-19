package com.yf.exam.modules.paper.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.modules.paper.dto.PaperQuAnswerDTO;
import com.yf.exam.modules.paper.dto.ext.PaperQuAnswerExtDTO;
import com.yf.exam.modules.paper.entity.PaperQuAnswer;
import com.yf.exam.modules.paper.mapper.PaperQuAnswerMapper;
import com.yf.exam.modules.paper.service.PaperQuAnswerService;
import com.yf.exam.modules.qu.entity.QuAnswer;
import com.yf.exam.modules.qu.service.QuAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* <p>
* 语言设置 服务实现类
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 16:33
*/
@Service
public class PaperQuAnswerServiceImpl extends ServiceImpl<PaperQuAnswerMapper, PaperQuAnswer> implements PaperQuAnswerService {

    @Autowired
    private QuAnswerService quAnswerService;

    @Override
    public IPage<PaperQuAnswerDTO> paging(PagingReqDTO<PaperQuAnswerDTO> reqDTO) {

        //创建分页对象
        IPage<PaperQuAnswer> query = new Page<>(reqDTO.getCurrent(), reqDTO.getSize());

        //查询条件
        QueryWrapper<PaperQuAnswer> wrapper = new QueryWrapper<>();

        //获得数据
        IPage<PaperQuAnswer> page = this.page(query, wrapper);
        //转换结果
        IPage<PaperQuAnswerDTO> pageData = JSON.parseObject(JSON.toJSONString(page), new TypeReference<Page<PaperQuAnswerDTO>>(){});
        return pageData;
    }

    @Override
    public List<PaperQuAnswerExtDTO> listForExam(String paperId, String quId) {
        List<PaperQuAnswer> list = this.list(new LambdaQueryWrapper<PaperQuAnswer>()
                .select(PaperQuAnswer::getId, PaperQuAnswer::getPaperId,
                        PaperQuAnswer::getQuId, PaperQuAnswer::getAnswerId,
                        PaperQuAnswer::getChecked, PaperQuAnswer::getSort, PaperQuAnswer::getAbc)
                .eq(PaperQuAnswer::getPaperId, paperId)
                .eq(PaperQuAnswer::getQuId, quId)
                .orderByAsc(PaperQuAnswer::getSort));
        List<PaperQuAnswerExtDTO> res = BeanMapper.mapList(list, PaperQuAnswerExtDTO.class);
        for (int i = 0; i < list.size(); i++) {
            QuAnswer quAnswer = quAnswerService.getById(list.get(i).getAnswerId());
            PaperQuAnswerExtDTO dto = res.get(i);
            dto.setImage(quAnswer.getImage());
            dto.setContent(quAnswer.getContent());
        }
        return res;
    }

    @Override
    public List<PaperQuAnswer> listForFill(String paperId, String quId) {
        //查询条件
        QueryWrapper<PaperQuAnswer> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(PaperQuAnswer::getPaperId, paperId)
                .eq(PaperQuAnswer::getQuId, quId);

        return this.list(wrapper);
    }
}

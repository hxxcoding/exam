package com.yf.exam.modules.repo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.modules.qu.entity.QuRepo;
import com.yf.exam.modules.qu.service.QuRepoService;
import com.yf.exam.modules.repo.dto.RepoDTO;
import com.yf.exam.modules.repo.dto.response.RepoRespDTO;
import com.yf.exam.modules.repo.entity.Repo;
import com.yf.exam.modules.repo.mapper.RepoMapper;
import com.yf.exam.modules.repo.service.RepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* <p>
* 语言设置 服务实现类
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 13:23
*/
@Service
public class RepoServiceImpl extends ServiceImpl<RepoMapper, Repo> implements RepoService {

    @Autowired
    private QuRepoService quRepoService;

    @Override
    public IPage<RepoRespDTO> paging(PagingReqDTO<RepoDTO> reqDTO) {

        //创建分页对象
        Page page = new Page(reqDTO.getCurrent(), reqDTO.getSize());

        return baseMapper.paging(page, reqDTO.getParams());
     }

    @Override
    public void save(RepoDTO reqDTO) {

        //复制参数
        Repo entity = new Repo();
        BeanMapper.copy(reqDTO, entity);
        this.saveOrUpdate(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<String> ids) {
        this.removeByIds(ids);
        // 同步删除quRepo
        quRepoService.remove(new LambdaQueryWrapper<QuRepo>()
                .in(QuRepo::getRepoId, ids));
        return true;
    }

    @Override
    public void refreshStat(String repoId) {
        baseMapper.refreshStat(repoId);
    }


    @Override
    public String findByName(String name) {
        QueryWrapper<Repo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Repo::getTitle, name);
        Repo repo = this.getOne(wrapper);

        if(repo!=null){
            return repo.getId();
        }

        return null;
    }
}

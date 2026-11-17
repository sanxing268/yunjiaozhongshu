package com.subject.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.mapper.ArticleMapper;
import com.subject.domain.entity.Article;
import com.subject.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int batchDeleteArticle(List<Integer> ids) {
        if (ids != null && ids.size() > 0) {
            int deleteBatch = articleMapper.deleteBatchIds(ids);
            return deleteBatch;
        }
        return -1;
    }
}

package com.subject.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.entity.Article;

import java.util.List;


public interface ArticleService extends IService<Article> {
    int batchDeleteArticle(List<Integer> ids);
}

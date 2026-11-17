package com.subject.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.commons.result.Result;
import com.subject.domain.entity.Article;
import com.subject.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private com.subject.service.CosService cosService;

    //前台访问地址
    @PostMapping("/content/article/{size}/{page}")
    public Result listArticles(@PathVariable("size") int size, @PathVariable("page") int page,
                               @RequestBody(required = false) Article article) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // 支持按文章类型过滤
        if (article != null && article.getArticleType() != null && !"".equals(article.getArticleType())) {
            wrapper.eq(Article::getArticleType, article.getArticleType());
        } else {
            // 默认只查询通知公告
            wrapper.eq(Article::getArticleType, "notice");
        }
        wrapper.orderByDesc(Article::getCreateDate);
        Page<Article> pagBean = new Page<>(page, size);
        Page<Article> articlePage = articleService.page(pagBean, wrapper);
        return Result.ok().data("list", articlePage.getRecords()).data("total", articlePage.getTotal());
    }

    //后台访问地址
    @PostMapping("/admin/content/article/{size}/{page}")
    public Result AdminListArticles(@PathVariable("size") int size, @PathVariable("page") int page,
                                    @RequestBody Article article) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>().like(article.getTitle() != null && !"".equals(article.getTitle()),
                Article::getTitle, article.getTitle());
        // 支持按文章类型过滤
        if (article.getArticleType() != null && !"".equals(article.getArticleType())) {
            wrapper.eq(Article::getArticleType, article.getArticleType());
        }
        wrapper.orderByDesc(Article::getCreateDate);
        Page<Article> pagBean = new Page<>(page, size);
        Page<Article> articlePage = articleService.page(pagBean, wrapper);
        return Result.ok().data("list", articlePage.getRecords()).data("total", articlePage.getTotal());
    }

    @PostMapping("/admin/content/article")
    public Result saveArticle(@RequestBody Article article) {
        //判断是否为新增文章
        if (article.getCreateDate() == null) {
            article.setCover("http://localhost:8443/" + article.getCover());
            article.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        // 如果未指定文章类型，默认为通知公告
        if (article.getArticleType() == null || "".equals(article.getArticleType())) {
            article.setArticleType("notice");
        }
        articleService.saveOrUpdate(article);
        return Result.ok().message("保存成功");
    }

    @GetMapping("/article/{id}")
    public Result getOneArticle(@PathVariable("id") int id) {
        return Result.ok().data("list", articleService.getById(id));
    }

    @DeleteMapping("/admin/content/article/{id}")
    public Result deleteArticle(@PathVariable("id") int id) {
//        boolean delete = articleService.removeById(id);
//        if (delete) {
//            return Result.ok().message("删除成功");
//        } else {
//            return Result.ok().message("删除失败");
        try {
            // 先查询记录，获取cover字段（图片URL）
            Article article = articleService.getById(id);
            if (article != null && article.getCover() != null && !article.getCover().isEmpty()) {
                // 删除COS中的文件
                String coverUrl = article.getCover();
                // 如果cover是相对路径，需要转换为完整URL
                if (!coverUrl.startsWith("http")) {
                    // 旧数据可能是相对路径，跳过删除
                    System.out.println("文章封面是相对路径，跳过COS删除: " + coverUrl);
                } else {
                    System.out.println("准备删除文章封面图片: " + coverUrl);
                    boolean cosDeleted = cosService.deleteFile(coverUrl);
                    if (cosDeleted) {
                        System.out.println("COS文件删除成功");
                    } else {
                        System.out.println("COS文件删除失败，但继续删除数据库记录");
                    }
                }
            }
            
            // 删除数据库记录
            boolean delete = articleService.removeById(id);
            if (delete) {
                return Result.ok().message("删除成功");
            } else {
                return Result.error().message("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("删除时发生错误: " + e.getMessage());
        }
    }

    @PostMapping("/admin/content/article/batchDelete")
    public Result batchDeleteArticle(@RequestBody List<Integer> ids) {
        int batchDelete = articleService.batchDeleteArticle(ids);
        if (batchDelete > 0) {
            return Result.ok().message("批量删除成功");
        } else {
            return Result.ok().message("批量删除失败");
        }
    }
}

package com.subject.controller;

import com.subject.commons.result.Result;
import com.subject.domain.entity.Category;
import com.subject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/api/categoryList")
    public Result listCategory() {
        List<Category> list = categoryService.list();
        return Result.ok().data("list", list);
    }
}

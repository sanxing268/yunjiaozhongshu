package com.subject.controller;

import com.subject.commons.result.Result;
import com.subject.service.AdminMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class MenuController {
    @Autowired
    private AdminMenuService adminMenuService;


    @GetMapping("menu")
    public Result getMenusByUserId() {
        return Result.ok().data("list", adminMenuService.getMenusByUserId());
    }


    @GetMapping("role/menu/list")
    public Result getMenusList() {
        return Result.ok().data("list", adminMenuService.getMenusList());
    }


}

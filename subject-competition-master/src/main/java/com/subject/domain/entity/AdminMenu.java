package com.subject.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


import java.io.Serializable;


@Data
@TableName(value = "admin_menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private String path;

    private String name;

    private String nameZh;

    private String iconCls;

    private String component;

    private int parentId;


}

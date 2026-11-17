package com.subject.domain.vo;


import com.subject.domain.entity.AdminMenu;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AdminMenuVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String path;

    private String name;

    private String nameZh;

    private String iconCls;

    private String component;

    private int parentId;

    private List<AdminMenu> children;
}

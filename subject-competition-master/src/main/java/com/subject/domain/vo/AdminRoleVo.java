package com.subject.domain.vo;





import com.subject.domain.entity.AdminPermission;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AdminRoleVo  implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    private String nameZh;

    private boolean enabled;

    private List<AdminPermission> perms;

    private List<AdminMenuVo> menus;
}

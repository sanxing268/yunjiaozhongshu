package com.subject.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;


import java.io.Serializable;

/**
 * Relations between roles and permissions.
 *
 * @author Evan
 * @date 2019/11
 */

@Data
@TableName(value = "admin_role_permission")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminRolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private int rid;

    private int pid;
}

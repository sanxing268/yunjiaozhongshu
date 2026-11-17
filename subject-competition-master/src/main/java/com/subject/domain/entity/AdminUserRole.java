package com.subject.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;


@Data
@TableName(value = "admin_user_role")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

//    用户id
    private Long uid;

//    角色id
    private Long rid;
}

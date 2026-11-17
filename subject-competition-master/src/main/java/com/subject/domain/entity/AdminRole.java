package com.subject.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


import java.io.Serializable;



@Data
@TableName(value = "admin_role")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminRole implements Serializable {

    private static final long serialVersionUID = 1L;

//    角色id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField(value = "name_zh")
    private String nameZh;

    private boolean enabled;

}

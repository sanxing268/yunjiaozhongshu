package com.subject.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "sys_project")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

//   项目编号
    @TableId(value = "project_id", type = IdType.AUTO)
    private Integer projectId;

//    赛题名称
    private String name;

    private String organization;

    private String format;

    private Integer teacherId;

    private String phone;

    @Email(message = "邮箱格式不正确")
    private String mail;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    private String major;

    private String sponsor;

    private String contractor;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date projectDate;

    private String objective;

    private String opinion;

    private String status;

    @TableLogic
    private int deleteFlag;


}

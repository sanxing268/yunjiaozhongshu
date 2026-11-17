package com.subject.domain.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;


@Data
@TableName("team_member")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamMember implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "member_no", type = IdType.AUTO)
    private Integer memberNo;

    private String teamNo;

    private String studentNo;

    private String studentName;

    private String collegeName;

    private String className;

    private String grade;

    private String major;

    private String mail;

    private String mobileNumber;

    @TableLogic
    private Integer deleteFlag;

}


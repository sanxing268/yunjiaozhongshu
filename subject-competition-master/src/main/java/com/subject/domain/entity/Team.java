package com.subject.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@TableName("sys_team")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "team_no")
    private String teamNo;

    private String title;

    private String teamName;

    private Integer projectId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enrollTime;

    private String teacher;

    private String status;

    private String opinion;

    private String conStatus;

//    创建的用户id
    private Integer createUserId;

//    指导的老师id
    private Integer teacherId;
}

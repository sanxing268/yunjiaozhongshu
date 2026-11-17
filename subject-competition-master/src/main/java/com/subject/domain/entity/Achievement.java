package com.subject.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_achievement")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Achievement implements Serializable {

    private static final long serialVersionUID = 1L;

//    成果id
    @TableId(value = "achievement_id", type = IdType.AUTO)
    private Integer achievementId;

//    项目名称
    private String title;

//    奖励级别
    private String level;

//    获奖团队
    private String team;

//    获奖日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date awardDate;

//    成果描述
    private String description;

//    创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

//    更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

//    软删除标志
    private Integer delFlag;
}

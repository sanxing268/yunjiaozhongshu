package com.subject.domain.vo;


import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.subject.domain.entity.Team;
import com.subject.domain.entity.TeamMember;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TeamVo implements Serializable {

    private static final long serialVersionUID = 1L;

//团队编号
    private String teamNo;

//标题
    private String title;
//团队名称
    private String teamName;

    private Integer projectId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enrollTime;

//    指导老师
    private String teacher;

    private String status;

    private String opinion;

    private String conStatus;

    @TableLogic
    private Integer deleteFlag;

//    创建的用户id
    private Integer createUserId;

//    指导老师的id
    private Integer teacherId;

    private List<TeamMember> memberList;
}


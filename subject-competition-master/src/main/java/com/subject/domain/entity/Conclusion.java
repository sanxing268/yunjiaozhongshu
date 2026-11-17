package com.subject.domain.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;


import java.io.Serializable;

@Data
@TableName("sys_conclusion")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Conclusion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "conclusion_id", type = IdType.AUTO)
    private Integer conclusionId;

    private Integer applyId;

    private Integer prizeId;

    private String teamNo;

    private Integer teacherId;

    private String file;

    private String opinion;

}

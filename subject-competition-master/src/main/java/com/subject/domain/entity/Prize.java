package com.subject.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;



@Data
@TableName("sys_prize")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Prize implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "prize_id", type = IdType.AUTO)
    private Integer prizeId;

    private String level;


}

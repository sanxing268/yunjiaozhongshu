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
@TableName("sys_download")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Download implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "download_id", type = IdType.AUTO)
    private Integer downloadId;

    private String title;

    private String description;

    private String fileUrl;

    private String fileSize;

    private String category;

    private Integer downloadCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer delFlag;
}

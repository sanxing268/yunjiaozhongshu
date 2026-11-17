package com.subject.domain.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.subject.domain.entity.Category;
import com.subject.domain.entity.ProjectView;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProjectViewVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private String author;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;

    private String press;

    private String abs;

    private String cover;

    private Category category;
}

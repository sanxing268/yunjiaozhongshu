package com.subject.domain.vo;

import com.subject.domain.entity.Conclusion;
import com.subject.domain.entity.Prize;
import com.subject.domain.entity.Project;
import lombok.Data;


import java.io.Serializable;
@Data
public class ConclusionVo extends Conclusion implements Serializable {
    private static final long serialVersionUID = 1L;

    private Project project;

    private TeamVo teamVo;

    private Prize prize;
}

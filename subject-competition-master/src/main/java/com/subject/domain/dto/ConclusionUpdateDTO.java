package com.subject.domain.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConclusionUpdateDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer conclusionId;

    private Integer applyId;

    private Integer prizeId;

    private String teamNo;

    private Integer teacherId;

    private String file;

    private String status;

    private String opinion;

    private String conStatus;
}


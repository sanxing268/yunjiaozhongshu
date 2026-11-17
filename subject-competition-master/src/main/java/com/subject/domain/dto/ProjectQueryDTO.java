package com.subject.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cid;

    private String name;

    private String status;

}

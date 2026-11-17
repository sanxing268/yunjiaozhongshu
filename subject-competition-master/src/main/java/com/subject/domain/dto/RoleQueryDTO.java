package com.subject.domain.dto;



import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String nameZh;

}

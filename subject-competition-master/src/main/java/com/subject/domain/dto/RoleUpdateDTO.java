package com.subject.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String nameZh;

    private boolean enabled;

    private List<Integer> permsId;

    private List<Integer> menusIds;


}

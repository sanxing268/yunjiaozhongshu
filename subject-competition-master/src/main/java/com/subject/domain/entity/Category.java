package com.subject.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    private String name;
}

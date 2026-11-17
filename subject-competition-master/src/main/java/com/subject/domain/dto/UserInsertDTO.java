package com.subject.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;


@Data
public class UserInsertDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "用户名不能为空")
    private String username;

    private String password;

    private String name;

    private String phone;

    private String email;

    private String rname;

}


package com.subject.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String username;

    private String name;

    private String phone;

    private String email;

    private boolean enabled;

    private String password;

    private String newpwd;

    private List<Long> roleIds;

}

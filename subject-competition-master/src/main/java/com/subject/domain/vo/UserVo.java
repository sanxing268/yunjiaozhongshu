package com.subject.domain.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.subject.domain.entity.AdminRole;
import com.subject.domain.entity.User;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotEmpty(message = "用户名不能为空")
    private String username;

    private String password;

    private String salt;

    private String name;

    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Email(message = "请输入正确的邮箱")
    private String email;

    private boolean enabled;

    private List<AdminRole> roles;

}

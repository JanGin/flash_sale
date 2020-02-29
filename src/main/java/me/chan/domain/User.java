package me.chan.domain;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private Long id;

    private String mobile;

    private String password;

    private String salt;

    private String head;

    private Date registerDate;

    private Date lastLoginDate;

    private Integer loginCount;
}

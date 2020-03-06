package me.chan.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {

    private Long id;

    private String mobile;

    private String password;

    private String salt;

    private String head;

    private Date registerDate;

    private Date lastLoginDate;

    private Integer loginCount;
}

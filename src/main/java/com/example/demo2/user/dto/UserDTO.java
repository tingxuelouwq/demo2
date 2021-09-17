package com.example.demo2.user.dto;

import javax.validation.constraints.NotBlank;

/**
 * kevin<br/>
 * 2021/8/30 16:54<br/>
 */
public class UserDTO {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

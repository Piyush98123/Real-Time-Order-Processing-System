package com.order.process.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDto implements Serializable {

    private String userName;
    private String userEmail;
    private String pan;
    private String mobNumber;
    private Date dob;
}

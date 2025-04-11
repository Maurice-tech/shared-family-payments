package com.sharedpay.shared.payment.payload;

import lombok.Data;

@Data
public class AppUserRequestDto {
    private String username;
    private String password;
    private String roleName;
}

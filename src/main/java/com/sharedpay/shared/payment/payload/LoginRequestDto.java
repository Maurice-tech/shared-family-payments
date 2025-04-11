package com.sharedpay.shared.payment.payload;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}

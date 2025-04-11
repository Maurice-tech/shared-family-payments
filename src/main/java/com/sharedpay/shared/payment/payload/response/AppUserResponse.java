package com.sharedpay.shared.payment.payload.response;

import lombok.Data;

@Data
public class AppUserResponse {
    private Long id;
    private String username;
    private String roleName;
}

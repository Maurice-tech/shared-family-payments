package com.sharedpay.shared.payment.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse<T>  {
    private String token;
    private String roles;
    private String message;
    private T data;

    public AuthResponse(String token, String roles, String message, T data) {
        this.token = token;
        this.roles = roles;
        this.message = message;
        this.data = data;
    }

    public AuthResponse(String message, String roles,  T data) {
        this.message = message;
        this.roles = roles;
        this.data = data;
    }

}

package com.sharedpay.shared.payment.auth;
import com.sharedpay.shared.payment.payload.AppUserRequestDto;
import com.sharedpay.shared.payment.payload.LoginRequestDto;
import com.sharedpay.shared.payment.payload.response.AppUserResponse;
import com.sharedpay.shared.payment.payload.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthUserDetails {
    AuthResponse<AppUserResponse> registerUser(@RequestBody AppUserRequestDto userRequest) throws Exception;
    AuthResponse<AppUserResponse> signIn(@RequestBody LoginRequestDto loginRequest, HttpServletRequest request);

}

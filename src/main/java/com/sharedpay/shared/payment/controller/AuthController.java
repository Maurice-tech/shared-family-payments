package com.sharedpay.shared.payment.controller;
import com.sharedpay.shared.payment.auth.AuthUserDetails;
import com.sharedpay.shared.payment.payload.AppUserRequestDto;
import com.sharedpay.shared.payment.payload.LoginRequestDto;
import com.sharedpay.shared.payment.payload.response.AppUserResponse;
import com.sharedpay.shared.payment.payload.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUserDetails authUserDetails;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse<AppUserResponse>> register(@RequestBody  @Valid AppUserRequestDto userRequest) throws Exception {
        AuthResponse<AppUserResponse> response = authUserDetails.registerUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse<AppUserResponse>> login(@RequestBody LoginRequestDto loginRequest,
                                                               HttpServletRequest request) {
        AuthResponse<AppUserResponse> response = authUserDetails.signIn(loginRequest, request);
        return ResponseEntity.ok(response);
    }

}

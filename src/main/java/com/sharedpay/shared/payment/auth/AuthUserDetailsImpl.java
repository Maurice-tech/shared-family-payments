package com.sharedpay.shared.payment.auth;
import com.sharedpay.shared.payment.entity.AppUser;
import com.sharedpay.shared.payment.entity.Role;
import com.sharedpay.shared.payment.exception.ResourceNotFoundException;
import com.sharedpay.shared.payment.exception.UserAlreadyExistException;
import com.sharedpay.shared.payment.payload.AppUserRequestDto;
import com.sharedpay.shared.payment.payload.LoginRequestDto;
import com.sharedpay.shared.payment.payload.response.AppUserResponse;
import com.sharedpay.shared.payment.payload.response.AuthResponse;
import com.sharedpay.shared.payment.repository.AppUserRepository;
import com.sharedpay.shared.payment.repository.RoleRepository;
import com.sharedpay.shared.payment.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthUserDetailsImpl implements AuthUserDetails{
    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetails;

    @Override
    public AuthResponse<AppUserResponse> registerUser(AppUserRequestDto userRequest) throws Exception {
        if (userRequest.getRoleName() == null || userRequest.getRoleName().isEmpty()) {
            throw new ResourceNotFoundException("Role name is missing in the request.");
        }
        Role role = roleRepository.findByName(userRequest.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Optional< AppUser> existingUser = appUserRepository.findByUsername(userRequest.getUsername());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistException("Username already exists with another account");
        }

        AppUser user = new AppUser();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRoles(Set.of(role));

        AppUser savedUser = appUserRepository.save(user);

        AppUserResponse response = new AppUserResponse();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setRoleName(savedUser.getRoles().stream()
                .findFirst()
                .map(Role::getName)
                .orElse(""));

        return new AuthResponse<>("User registered successfully", response.getRoleName(), response);
    }

    @Override
    public AuthResponse<AppUserResponse> signIn(LoginRequestDto loginRequest, HttpServletRequest request) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || username.isEmpty()) {
            throw new ResourceNotFoundException("Username cannot be null or empty.");
        }
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser user = appUserRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found "+ username));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        String token = jwtProvider.generateToken(authentication);

        String roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));

        AppUserResponse response = new AppUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRoleName(roles);

        return new AuthResponse<>(token, roles, "Login successful", response);
    }


    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password ...");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }


}

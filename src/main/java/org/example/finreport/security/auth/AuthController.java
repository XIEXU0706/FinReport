package org.example.finreport.security.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.Result;
import org.example.finreport.security.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        LoginUser loginUser = (LoginUser) auth.getPrincipal();

        String token = jwtUtils.generateToken(loginUser.getUserId(), loginUser.getUsername(), loginUser.getRoles());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", loginUser.getUserId());
        data.put("username", loginUser.getUsername());
        data.put("roles", loginUser.getRoles());
        data.put("token", token);
        return Result.ok(data);
    }

    @GetMapping("/info")
    public Result<LoginUser> info() {
        LoginUser loginUser = LoginUserHolder.get();
        return Result.ok(loginUser);
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
}

package iuh.fit.se.userservice.controllers;

import iuh.fit.se.userservice.dtos.ApiResponse;
import iuh.fit.se.userservice.dtos.SignInRequest;
import iuh.fit.se.userservice.dtos.SignUpRequest;
import iuh.fit.se.userservice.dtos.TokenRequest;
import iuh.fit.se.userservice.exceptions.UserAlreadyExistsException;
import iuh.fit.se.userservice.services.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

@RestController
@RequestMapping("/users")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService, LogoutHandler logoutHandler, HandlerMapping resourceHandlerMapping) {
       this.authService = authService;

    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody @Valid SignUpRequest signUpRequest)
            throws UserAlreadyExistsException {
        return authService.signUp(signUpRequest);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<?>> signInUser(@RequestBody @Valid SignInRequest signInRequest){
        return authService.signIn(signInRequest);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<?>> refreshToken(@RequestBody TokenRequest tokenRequest){
        return authService.refreshToken(tokenRequest);
    }

    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<?>> introspectToken(@RequestBody TokenRequest tokenRequest) {
        return authService.introspectToken(tokenRequest);
    }



}

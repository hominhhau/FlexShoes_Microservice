package iuh.fit.se.userservice.services.impl;

import iuh.fit.se.userservice.auths.UserPrincipal;
import iuh.fit.se.userservice.dtos.*;
import iuh.fit.se.userservice.entities.Role;
import iuh.fit.se.userservice.entities.Token;
import iuh.fit.se.userservice.entities.User;
import iuh.fit.se.userservice.exceptions.UserAlreadyExistsException;
import iuh.fit.se.userservice.mappers.ProfileMapper;
import iuh.fit.se.userservice.repositories.httpClient.NotificationClient;
import iuh.fit.se.userservice.repositories.httpClient.ProfileClient;
import iuh.fit.se.userservice.services.AuthService;
import iuh.fit.se.userservice.services.RoleService;
import iuh.fit.se.userservice.services.TokenService;
import iuh.fit.se.userservice.services.UserService;
import iuh.fit.se.userservice.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private NotificationClient notificationClient;
    private UserService userService;
    private RoleService roleService;
    private TokenService tokenService;
    private PasswordEncoder passwordEncoder;
    private JwtTokenUtil jwtTokenUtil;
    private AuthenticationManager authenticationManager;
    private JwtEncoder jwtEncoder;
    private JwtDecoder jwtDecoder;
    private UserDetailsServiceImpl userDetailsService;
    private ProfileClient profileClient;
    private ProfileMapper profileMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Autowired
    public AuthServiceImpl(UserService userService,
                           RoleService roleService,
                           TokenService tokenService,
                           JwtTokenUtil jwtTokenUtil,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtEncoder jwtEncoder,
                           JwtDecoder jwtDecoder,
                           UserDetailsServiceImpl userDetailsService,
                           ProfileClient profileClient,
                           ProfileMapper profileMapper,
                           NotificationClient notificationClient) {
        this.userService = userService;
        this.roleService = roleService;
        this.tokenService = tokenService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userDetailsService = userDetailsService;
        this.profileClient = profileClient;
        this.profileMapper = profileMapper;
        this.notificationClient = notificationClient;
    }

    @Override
    public ResponseEntity<ApiResponse<?>> signUp(SignUpRequest signUpRequest)
            throws UserAlreadyExistsException {
        if (userService.existsByUserName(signUpRequest.getUserName())) {
            throw new UserAlreadyExistsException("Username already exist");
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exist");
        }

        System.out.println("Hi Tien");
        if (profileClient.checkProfile(signUpRequest.getPhoneNumber())) {
            System.out.println("Hi Tinh");
            throw new UserAlreadyExistsException("Phone number already exist");
        }


        User user = createUser(signUpRequest);
        User result = userService.saveUser(user);
        //Log result
        System.out.println("Result: 1" + result);


        //Create a profile for sending to profile-service
        ProfileCreationRequest profileCreationRequest = profileMapper.mapToProfile(signUpRequest);
        profileCreationRequest.setUserID(result.getId());

        ApiResponse<ProfileCreationResponse> obj = profileClient.createProfile(profileCreationRequest);
        //Log result
        log.info("Created profile: " + obj);
        user.setProfileKey(obj.getResponse().getProfileKey());
        userService.saveUser(user);


        log.debug("Sending recipient: {}", result);
        log.info("Successfully created user: " + result.getUserName());
        log.info("Successfully created profile for email: " + result.getEmail());

        // Gửi email xác nhận
        notificationClient.sendRegistrationEmail(
                new Recipient(result.getUserName(), result.getEmail())
        );

        log.info("Successfully sent email to " + result.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status(String.valueOf("SUCCESS"))
                        .message("User account has been successfully created!")
                        .build()
                );
    }

    private User createUser(SignUpRequest signUpRequest) {

        System.out.println("Create user in function: " + signUpRequest);

        return User.builder()
                .email(signUpRequest.getEmail())
                .userName(signUpRequest.getUserName())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .enabled(true)
                .roles(determineRoles(signUpRequest.getRoles()))
                .build();
    }

    private Set<Role> determineRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(roleService.getRoleByCode("ROLE_USER"));
        } else {
            for (String role : strRoles) {
                roles.add(roleService.getRoleByCode(role));
            }
        }
        return roles;
    }

    @Override
    public ResponseEntity<ApiResponse<?>> signIn(SignInRequest signInRequest) {
        Authentication authentication = null;
        try {
             authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInRequest.getUserName(),
                            signInRequest.getPassword()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .status("ERROR")
                            .message("Invalid username or password")
                            .build());
        }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenUtil.generateToken(authentication, jwtEncoder);
            String refreshToken = jwtTokenUtil.generateRefreshToken(authentication, jwtEncoder);
            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
            System.out.println("User principal: " + userDetails);
            User user = new User();
            user.setId(userDetails.getId());
            user.setUserName(userDetails.getUsername());
            System.out.println(authentication.getAuthorities());

            Token token = Token.builder()
                    .token(jwt)
                    .user(user)
                    .expiryDate(jwtTokenUtil.generateExpirationDate(true))
                    .revoked(false)
                    .isRefreshToken(false)
                    .build();
            tokenService.saveToken(token);
            Token refreshTokenEntity = Token.builder()
                    .token(refreshToken)
                    .user(user)
                    .expiryDate(jwtTokenUtil.generateExpirationDate(false))
                    .revoked(false)
                    .isRefreshToken(true)
                    .build();
            tokenService.saveToken(refreshTokenEntity);

            SignInResponse signInResponse = SignInResponse.builder()
                    .username(userDetails.getUsername())
                    .email(userDetails.getEmail())
                    .id(userDetails.getId())
                    .token(jwt)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .profileKey(userDetails.getProfileKey())
                    .roles(userDetails.getAuthorities())
                    .build();

            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .status("SUCCESS")
                            .message("Sign in successfull!")
                            .response(signInResponse)
                            .build()
            );


    }

    @Override
    public ResponseEntity<ApiResponse<?>> refreshToken(TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getToken();  // Lấy refreshToken từ request
        try {
            // Giải mã refreshToken
            Jwt jwt = this.jwtDecoder.decode(refreshToken);
            String userName = jwt.getSubject();  // Lấy tên người dùng từ token

            // Kiểm tra xem refreshToken có hợp lệ không
            Token tokenEntity = tokenService.findByToken(refreshToken);
            if (tokenEntity == null || tokenEntity.isRevoked() || Objects.requireNonNull(jwt.getExpiresAt()).isBefore(Instant.now())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.builder()
                                .status("ERROR")
                                .message("Refresh token has been revoked or expired or does not exist")
                                .build());
            }

            // Tạo lại Authentication từ thông tin trong refreshToken (không cần mật khẩu)
            UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(userName);
            if (userPrincipal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.builder()
                                .status("ERROR")
                                .message("User not found")
                                .build());
            }

            // Tạo đối tượng Authentication từ UserPrincipal
            Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo accessToken mới
            String newAccessToken = jwtTokenUtil.generateToken(authentication, jwtEncoder);

            User user = new User();
            user.setId(userPrincipal.getId());
            user.setUserName(userPrincipal.getUsername());

            Token newAccessTokenEntity = Token.builder()
                    .token(newAccessToken)
                    .user(user)
                    .expiryDate(jwtTokenUtil.generateExpirationDate(true))  // Thời gian hết hạn của accessToken
                    .revoked(false)
                    .isRefreshToken(false)
                    .build();
            tokenService.saveToken(newAccessTokenEntity);

            // Tạo phản hồi cho người dùng
            RefreshTokenResponse response = RefreshTokenResponse.builder()
                    .username(userPrincipal.getUsername())
                    .email(userPrincipal.getEmail())
                    .id(userPrincipal.getId())
                    .newAccessToken(newAccessToken)
                    .type("Bearer")
                    .roles(userPrincipal.getAuthorities())
                    .build();

            return ResponseEntity.ok(ApiResponse.builder()
                    .status("SUCCESS")
                    .message("Token refreshed successfully")
                    .response(response)
                    .build());

        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .status("ERROR")
                            .message("Invalid refresh token")
                            .build());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> introspectToken(TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();  // Lấy token từ request

        try {
            Jwt jwt = this.jwtDecoder.decode(token);

            // Kiểm tra xem token có hết hạn chưa
            Instant expiresAt = jwt.getExpiresAt();
            if (expiresAt == null || expiresAt.isBefore(Instant.now())) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .status("ERROR")
                        .message("Token has expired")
                        .response(IntrospectResponse.builder().valid(false).build())
                        .build());
            }

            // Kiểm tra xem token có bị thu hồi không
            Token tokenEntity = tokenService.findByToken(token);
            if (tokenEntity == null || tokenEntity.isRevoked()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .status("ERROR")
                        .message("Token has been revoked or does not exist")
                        .response(IntrospectResponse.builder().valid(false).build())
                        .build());
            }

            // Trích xuất thông tin từ token
            String username = jwt.getSubject();

            // Tạo IntrospectResponse
            IntrospectResponse introspectResponse = IntrospectResponse.builder()
                    .valid(true)
                    .username(username)
                    .expiresAt(expiresAt)
                    .build();

            // Trả về ApiResponse
            return ResponseEntity.ok(ApiResponse.builder()
                    .status("SUCCESS")
                    .message("Token is valid")
                    .response(introspectResponse)
                    .build());

        } catch (JwtException ex) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .status("ERROR")
                    .message("Invalid token")
                    .response(IntrospectResponse.builder().valid(false).build())
                    .build());
        }
    }

    public ResponseEntity<ApiResponse<Void>> updatePassword(Long userId, String oldPassword, String newPassword) {
        logger.info("Processing password update request for userId: {}", userId);

        // Kiểm tra null/rỗng cho mật khẩu
        if (oldPassword == null || oldPassword.isBlank()) {
            logger.warn("Old password is null or empty for userId: {}", userId);
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>("ERROR", "Old password cannot be empty", null));
        }
        if (newPassword == null || newPassword.isBlank()) {
            logger.warn("New password is null or empty for userId: {}", userId);
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>("ERROR", "New password cannot be empty", null));
        }

        // Lấy thông tin người dùng từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);

        if (user == null) {
            logger.error("User not found for username: {}", username);
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>("ERROR", "User not found", null));
        }

        if (!user.getId().equals(userId)) {
            logger.warn("Invalid userId: {} does not match authenticated user: {}", userId, user.getId());
            return ResponseEntity.status(403)
                    .body(new ApiResponse<>("ERROR", "Invalid user ID", null));
        }

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            logger.warn("Incorrect old password for userId: {}", userId);
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>("ERROR", "Old password is incorrect", null));
        }

        // Kiểm tra mật khẩu mới
        if (newPassword.length() < 8) {
            logger.warn("New password too short for userId: {}", userId);
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>("ERROR", "New password must be at least 8 characters", null));
        }

        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            logger.warn("New password does not meet complexity requirements for userId: {}", userId);
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>("ERROR", "New password must contain at least one uppercase letter, one lowercase letter, one number, and one special character", null));
        }

        // Mã hóa và cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user);
        logger.info("Password updated successfully for userId: {}", userId);

        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Password updated successfully", null));
    }
}

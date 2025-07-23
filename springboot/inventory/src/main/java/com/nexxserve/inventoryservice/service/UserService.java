package com.nexxserve.inventoryservice.service;

import com.nexxserve.inventoryservice.dto.user.*;
import com.nexxserve.inventoryservice.enums.Role;
import com.nexxserve.inventoryservice.entity.user.User;
import com.nexxserve.inventoryservice.enums.UserStatus;
import com.nexxserve.inventoryservice.exception.UserNotFoundException;
import com.nexxserve.inventoryservice.exception.UserNotActiveException;
import com.nexxserve.inventoryservice.exception.InvalidCredentialsException;
import com.nexxserve.inventoryservice.exception.UserAlreadyExistsException;
import com.nexxserve.inventoryservice.mapper.UserMapper;
import com.nexxserve.inventoryservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Lazy
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailOrPhoneNumber(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }


    public UserDto updateUserRoles(Long userId, UserRoleUpdateDto roleUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.setRoles(roleUpdateDto.getRoles());
        user = userRepository.save(user);
        log.info("User roles updated for user: {} by admin", user.getUsername());

        return userMapper.toDto(user);
    }


    public Page<UserDto> getAllUsers(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.ASC, sort != null ? sort : "id"));
        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(userMapper::toDto);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        try {
            String emailOrPhone = request.getEmailOrPhone();

            // First check if user exists
            User user = userRepository.findByEmailOrPhoneNumber(emailOrPhone, emailOrPhone)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email/phone or password"));

            // Check if user is active before authentication
            if (!user.isEnabled()) {
                throw new UserNotActiveException("User account is not active");
            }

            // Then authenticate
            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(emailOrPhone, request.getPassword())
                );
            } catch (BadCredentialsException e) {
                log.error("Login failed: Bad credentials for user: {}", emailOrPhone);
                throw new InvalidCredentialsException("Invalid email/phone or password");
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            user = (User) authentication.getPrincipal();

            // Update last login time
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user, user.getId());
            String refreshToken = jwtService.generateRefreshToken(user, user.getId());

            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .user(userMapper.toDto(user))
                    .build();

        } catch (InvalidCredentialsException | UserNotActiveException e) {
            // Just rethrow these specific exceptions
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for user: {}", request.getEmailOrPhone(), e);
            throw new RuntimeException("An unexpected error occurred during login", e);
        }
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        // Check if user already exists
        boolean emailExists = request.getEmail() != null && userRepository.existsByEmail(request.getEmail());
        boolean phoneExists = request.getPhoneNumber() != null &&
                userRepository.existsByPhoneNumberAndPhoneNumberIsNotNull(request.getPhoneNumber());

        if (emailExists || phoneExists) {
            throw new UserAlreadyExistsException("User with this email or phone number already exists");
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .roles(Set.of(Role.USER)) // Default role
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user, user.getId());
        String refreshToken = jwtService.generateRefreshToken(user, user.getId());

        UserDto userDto = userMapper.toDto(user);

        log.info("New user registered: {}", user.getUsername());

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(userDto)
                .build();
    }

    public AuthResponseDto refreshToken(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = loadUserByUsername(username);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        User user = (User) userDetails;
        String newAccessToken = jwtService.generateAccessToken(user, user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(user, user.getId());

        UserDto userDto = userMapper.toDto(user);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(userDto)
                .build();
    }

    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCredentialsException("No authenticated user found");
        }

        User user = (User) authentication.getPrincipal();
        return userMapper.toDto(user);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCredentialsException("No authenticated user found");
        }

        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public UserDto updateUser(Long id, UserUpdateDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Update fields if provided
        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getMiddleName() != null) {
            user.setMiddleName(updateDto.getMiddleName());
        }

        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void changePassword(Long userId, PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Verify password confirmation
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new InvalidCredentialsException("Password confirmation does not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getUsername());
    }

    // Admin operations
    public UserDto updateUserStatus(Long userId, UserStatusUpdateDto statusUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (statusUpdateDto.getStatus() != null) {
            user.setStatus(statusUpdateDto.getStatus());
        }

        if (statusUpdateDto.getRoles() != null) {
            user.setRoles(statusUpdateDto.getRoles());
        }

        user = userRepository.save(user);
        log.info("User status updated for user: {} by admin", user.getUsername());

        return userMapper.toDto(user);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        log.info("User deactivated: {}", user.getUsername());
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        log.info("User activated: {}", user.getUsername());
    }
}
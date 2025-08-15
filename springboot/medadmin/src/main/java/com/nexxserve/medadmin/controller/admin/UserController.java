package com.nexxserve.medadmin.controller.admin;

import com.nexxserve.medadmin.dto.request.UserReportDTO;
import com.nexxserve.medadmin.entity.clients.User;
import com.nexxserve.medadmin.security.HasRoleClient;
import com.nexxserve.medadmin.service.admin.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @HasRoleClient
    @PostMapping
    public List<User> saveUsers(@RequestBody List<UserReportDTO> users, HttpServletRequest httpServletRequest) {
        String remoteAddress = httpServletRequest.getRemoteAddr();
        String fullAddress = remoteAddress + ":5007";
        return userService.saveUsers(users, fullAddress);
    }
}
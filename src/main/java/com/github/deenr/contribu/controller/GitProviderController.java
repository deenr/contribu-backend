package com.github.deenr.contribu.controller;

import com.github.deenr.contribu.dto.GitProviderStatusDTO;
import com.github.deenr.contribu.service.GitProviderService;
import com.github.deenr.contribu.service.JwtService;
import com.github.deenr.contribu.util.RefreshTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/providers")
public class GitProviderController {
    private final GitProviderService gitProviderService;

    @Autowired
    public GitProviderController(GitProviderService gitProviderService) {
        this.gitProviderService = gitProviderService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAllProviderStatuses(HttpServletRequest request) {
        Optional<Cookie> cookie = RefreshTokenUtil.getRefreshCookie(request.getCookies());
        if (cookie.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        String email;
        try {
            email = JwtService.extractUsername(cookie.get().getValue());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }


        List<GitProviderStatusDTO> statuses = gitProviderService.getAllProviderStatuses(email);
        return ResponseEntity.ok(statuses);
    }
}

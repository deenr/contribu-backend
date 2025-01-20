package com.github.deenr.contribu.dto;

import com.github.deenr.contribu.enums.GitPlatform;

import java.time.LocalDateTime;

public class GitProviderStatusDTO {
    private GitPlatform provider;
    private boolean authorized;
    private boolean tokenValid;
    private LocalDateTime syncedAt;

    public GitProviderStatusDTO(GitPlatform provider, boolean authorized, boolean tokenValid, LocalDateTime syncedAt) {
        this.provider = provider;
        this.authorized = authorized;
        this.tokenValid = tokenValid;
        this.syncedAt = syncedAt;
    }

    public GitPlatform getProvider() {
        return provider;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public boolean isTokenValid() {
        return tokenValid;
    }

    public LocalDateTime getSyncedAt() {
        return syncedAt;
    }
}
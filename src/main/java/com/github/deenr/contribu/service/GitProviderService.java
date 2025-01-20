package com.github.deenr.contribu.service;

import com.github.deenr.contribu.dto.GitProviderStatusDTO;
import com.github.deenr.contribu.enums.GitPlatform;
import com.github.deenr.contribu.model.GitProvider;

import java.util.List;

public interface GitProviderService {
    List<GitProviderStatusDTO> getAllProviderStatuses(String email);
    GitProvider save(String email, GitPlatform provider, String token);
    void delete(String email, GitPlatform provider);
}

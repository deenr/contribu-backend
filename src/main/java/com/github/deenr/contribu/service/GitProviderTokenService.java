package com.github.deenr.contribu.service;

import com.github.deenr.contribu.dto.ProviderStatusDTO;
import com.github.deenr.contribu.enums.GitProvider;
import com.github.deenr.contribu.model.GitProviderToken;

import java.util.List;

public interface GitProviderTokenService {
    List<ProviderStatusDTO> getAllProviderStatuses(String email);
    GitProviderToken save(String email, GitProvider provider, String token);
    void delete(String email, GitProvider provider);
}

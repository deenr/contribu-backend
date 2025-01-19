package com.github.deenr.contribu.service;

import com.github.deenr.contribu.dto.GitProviderStatusDTO;
import com.github.deenr.contribu.model.GitProvider;

import java.util.List;

public interface GitProviderService {
    List<GitProviderStatusDTO> getAllProviderStatuses(String email);
    GitProvider save(String email, com.github.deenr.contribu.enums.GitProvider provider, String token);
    void delete(String email, com.github.deenr.contribu.enums.GitProvider provider);
}

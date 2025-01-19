package com.github.deenr.contribu.service.impl;

import com.github.deenr.contribu.dto.GitProviderStatusDTO;
import com.github.deenr.contribu.enums.TokenStatus;
import com.github.deenr.contribu.model.GitProvider;
import com.github.deenr.contribu.model.User;
import com.github.deenr.contribu.repository.GitProviderProvider;
import com.github.deenr.contribu.service.GitProviderService;
import com.github.deenr.contribu.service.factory.OAuthServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GitProviderServiceImpl implements GitProviderService {
    private final UserServiceImpl userService;
    private final GitProviderProvider gitProviderProvider;
    private final OAuthServiceFactory oAuthServiceFactory;

    @Autowired
    public GitProviderServiceImpl(
            UserServiceImpl userService,
            GitProviderProvider gitProviderProvider,
            @Lazy OAuthServiceFactory oAuthServiceFactory
    ) {
        this.userService = userService;
        this.gitProviderProvider = gitProviderProvider;
        this.oAuthServiceFactory = oAuthServiceFactory;
    }

    @Override
    public List<GitProviderStatusDTO> getAllProviderStatuses(String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<GitProviderStatusDTO> providerStatuses = new ArrayList<>();
        for (com.github.deenr.contribu.enums.GitProvider provider : com.github.deenr.contribu.enums.GitProvider.values()) {
            Optional<GitProvider> gitProviderToken = gitProviderProvider.getByUserIdAndProvider(user.getId(), provider);

            if (gitProviderToken.isPresent()) {
                System.err.println(provider.toString());
                System.err.println(gitProviderToken.get().getToken());
                TokenStatus status = oAuthServiceFactory.getOAuthService(provider.toString()).getTokenStatus(gitProviderToken.get().getToken());
                System.err.println(status);

                GitProviderStatusDTO providerStatus = new GitProviderStatusDTO(
                        provider,
                        true,
                        status == TokenStatus.VALID,
                        gitProviderToken.get().getSyncedAt()
                );
                providerStatuses.add(providerStatus);
            } else {
                GitProviderStatusDTO providerStatus = new GitProviderStatusDTO(
                        provider,
                        false,
                        false,
                        null
                );
                providerStatuses.add(providerStatus);
            }
        }

        return providerStatuses;
    }

    @Override
    public GitProvider save(String email, com.github.deenr.contribu.enums.GitProvider provider, String token) {
        User user = userService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));


        Optional<GitProvider> previousGitProviderToken = gitProviderProvider.getByUserIdAndProvider(user.getId(), provider);

        if (previousGitProviderToken.isPresent()) {
            GitProvider gitProvider = previousGitProviderToken.get();
            gitProvider.setToken(token);
            gitProvider.setSyncedAt(LocalDateTime.now());

            gitProviderProvider.updateTokenByUserIdAndProvider(
                    gitProvider.getUser().getId(),
                    gitProvider.getProvider(),
                    gitProvider.getToken(),
                    gitProvider.getSyncedAt()
            );

            return gitProvider;
        } else {
            GitProvider gitProvider = new GitProvider();
            gitProvider.setUser(user);
            gitProvider.setProvider(provider);
            gitProvider.setToken(token);
            gitProvider.setSyncedAt(LocalDateTime.now());

            return gitProviderProvider.save(gitProvider);
        }
    }

    @Override
    public void delete(String email, com.github.deenr.contribu.enums.GitProvider provider) {
        User user = userService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        gitProviderProvider.deleteByUserIdAndProvider(user.getId(), provider);
    }
}

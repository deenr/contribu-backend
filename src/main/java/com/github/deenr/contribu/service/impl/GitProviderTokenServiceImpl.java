package com.github.deenr.contribu.service.impl;

import com.github.deenr.contribu.dto.ProviderStatusDTO;
import com.github.deenr.contribu.enums.GitProvider;
import com.github.deenr.contribu.enums.TokenStatus;
import com.github.deenr.contribu.model.GitProviderToken;
import com.github.deenr.contribu.model.User;
import com.github.deenr.contribu.repository.GitProviderTokenProvider;
import com.github.deenr.contribu.service.GitProviderTokenService;
import com.github.deenr.contribu.service.factory.OAuthServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GitProviderTokenServiceImpl implements GitProviderTokenService {
    private final UserServiceImpl userService;
    private final GitProviderTokenProvider gitProviderTokenProvider;
    private final OAuthServiceFactory oAuthServiceFactory;

    @Autowired
    public GitProviderTokenServiceImpl(
            UserServiceImpl userService,
            GitProviderTokenProvider gitProviderTokenProvider,
            @Lazy OAuthServiceFactory oAuthServiceFactory
    ) {
        this.userService = userService;
        this.gitProviderTokenProvider = gitProviderTokenProvider;
        this.oAuthServiceFactory = oAuthServiceFactory;
    }

    @Override
    public List<ProviderStatusDTO> getAllProviderStatuses(String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<ProviderStatusDTO> providerStatuses = new ArrayList<>();
        for (GitProvider provider : GitProvider.values()) {
            Optional<GitProviderToken> gitProviderToken = gitProviderTokenProvider.getByUserIdAndProvider(user.getId(), provider);

            if (gitProviderToken.isPresent()) {
                System.err.println(provider.toString());
                System.err.println(gitProviderToken.get().getToken());
                TokenStatus status = oAuthServiceFactory.getOAuthService(provider.toString()).getTokenStatus(gitProviderToken.get().getToken());
                System.err.println(status);

                ProviderStatusDTO providerStatus = new ProviderStatusDTO(
                        provider,
                        true,
                        status == TokenStatus.VALID,
                        gitProviderToken.get().getSyncedAt()
                );
                providerStatuses.add(providerStatus);
            } else {
                ProviderStatusDTO providerStatus = new ProviderStatusDTO(
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
    public GitProviderToken save(String email, GitProvider provider, String token) {
        User user = userService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));


        Optional<GitProviderToken> previousGitProviderToken = gitProviderTokenProvider.getByUserIdAndProvider(user.getId(), provider);

        if (previousGitProviderToken.isPresent()) {
            GitProviderToken gitProviderToken = previousGitProviderToken.get();
            gitProviderToken.setToken(token);
            gitProviderToken.setSyncedAt(LocalDateTime.now());

            gitProviderTokenProvider.updateTokenByUserIdAndProvider(
                    gitProviderToken.getUser().getId(),
                    gitProviderToken.getProvider(),
                    gitProviderToken.getToken(),
                    gitProviderToken.getSyncedAt()
            );

            return gitProviderToken;
        } else {
            GitProviderToken gitProviderToken = new GitProviderToken();
            gitProviderToken.setUser(user);
            gitProviderToken.setProvider(provider);
            gitProviderToken.setToken(token);
            gitProviderToken.setSyncedAt(LocalDateTime.now());

            return gitProviderTokenProvider.save(gitProviderToken);
        }
    }

    @Override
    public void delete(String email, GitProvider provider) {
        User user = userService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        gitProviderTokenProvider.deleteByUserIdAndProvider(user.getId(), provider);
    }
}

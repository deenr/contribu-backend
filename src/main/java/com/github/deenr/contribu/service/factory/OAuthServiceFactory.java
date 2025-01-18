package com.github.deenr.contribu.service.factory;

import com.github.deenr.contribu.service.OAuthService;
import com.github.deenr.contribu.service.impl.GithubOAuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OAuthServiceFactory {
    private final GithubOAuthServiceImpl githubOAuthService;

    @Autowired
    public OAuthServiceFactory(GithubOAuthServiceImpl githubOAuthService) {
        this.githubOAuthService = githubOAuthService;
    }

    public OAuthService getOAuthService(String provider) {
        if (provider.equalsIgnoreCase("github")) {
            return githubOAuthService;
        }
        throw new IllegalArgumentException("Unknown provider: " + provider);
    }
}

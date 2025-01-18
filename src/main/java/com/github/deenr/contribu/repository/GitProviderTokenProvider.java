package com.github.deenr.contribu.repository;

import com.github.deenr.contribu.enums.GitProvider;
import com.github.deenr.contribu.model.GitProviderToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GitProviderTokenProvider extends JpaRepository<GitProviderToken, Long> {

    @Query("SELECT g FROM GitProviderToken g WHERE g.user.id = :userId AND g.provider = :provider")
    Optional<GitProviderToken> getByUserIdAndProvider(
            @Param("userId") Long userId,
            @Param("provider") GitProvider provider
    );

    @Modifying
    @Transactional
    @Query("UPDATE GitProviderToken g " +
            "SET g.token = :token, g.syncedAt = :syncedAt " +
            "WHERE g.user.id = :userId AND g.provider = :provider")
    void updateTokenByUserIdAndProvider(
            @Param("userId") Long userId,
            @Param("provider") GitProvider provider,
            @Param("token") String token,
            @Param("syncedAt") LocalDateTime syncedAt
    );

    @Modifying
    @Transactional
    @Query("DELETE GitProviderToken g WHERE g.user.id = :userId AND g.provider = :provider"
    )
    void deleteByUserIdAndProvider(
            @Param("userId") Long userId,
            @Param("provider") GitProvider provider
    );
}

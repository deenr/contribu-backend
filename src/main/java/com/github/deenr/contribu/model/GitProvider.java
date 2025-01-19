package com.github.deenr.contribu.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "git_provider")
public class GitProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_git_provider_token"))
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private com.github.deenr.contribu.enums.GitProvider provider;

    private LocalDateTime syncedAt;

    @Column
    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public com.github.deenr.contribu.enums.GitProvider getProvider() {
        return provider;
    }

    public void setProvider(com.github.deenr.contribu.enums.GitProvider provider) {
        this.provider = provider;
    }

    public LocalDateTime getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(LocalDateTime syncedAt) {
        this.syncedAt = syncedAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

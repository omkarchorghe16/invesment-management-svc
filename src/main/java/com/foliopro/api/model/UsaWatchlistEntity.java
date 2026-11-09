package com.foliopro.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "watchlists_usa")
public class UsaWatchlistEntity extends WatchlistEntity {
    @Column(name = "user_id", nullable = false)
    private Long userId = 1L;

    public UsaWatchlistEntity() {
        setMarket("US");
        setCurrency("USD");
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

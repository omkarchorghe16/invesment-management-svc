package com.foliopro.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "watchlist_items_usa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"watchlist_id", "symbol"}))
public class UsaWatchlistItemEntity extends WatchlistItemEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "watchlist_id", nullable = false)
    private UsaWatchlistEntity watchlist;

    public UsaWatchlistItemEntity() {}

    public UsaWatchlistEntity getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(UsaWatchlistEntity watchlist) {
        this.watchlist = watchlist;
    }
}

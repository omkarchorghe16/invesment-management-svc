package com.foliopro.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "watchlist_items_india",
        uniqueConstraints = @UniqueConstraint(columnNames = {"watchlist_id", "symbol"}))
public class IndiaWatchlistItemEntity extends WatchlistItemEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "watchlist_id", nullable = false)
    private IndiaWatchlistEntity watchlist;

    public IndiaWatchlistItemEntity() {}

    public IndiaWatchlistEntity getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(IndiaWatchlistEntity watchlist) {
        this.watchlist = watchlist;
    }
}

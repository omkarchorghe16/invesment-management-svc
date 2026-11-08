package com.foliopro.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "watchlists_india")
public class IndiaWatchlistEntity extends WatchlistEntity {
    public IndiaWatchlistEntity() {
        setMarket("India");
        setCurrency("INR");
    }
}

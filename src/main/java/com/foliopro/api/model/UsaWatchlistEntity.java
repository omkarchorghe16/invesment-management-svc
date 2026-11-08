package com.foliopro.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "watchlists_usa")
public class UsaWatchlistEntity extends WatchlistEntity {
    public UsaWatchlistEntity() {
        setMarket("US");
        setCurrency("USD");
    }
}

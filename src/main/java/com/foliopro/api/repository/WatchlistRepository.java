package com.foliopro.api.repository;

import com.foliopro.api.model.WatchlistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistRepository extends JpaRepository<WatchlistEntity, Long> {
    Page<WatchlistEntity> findByMarket(String market, Pageable pageable);
}

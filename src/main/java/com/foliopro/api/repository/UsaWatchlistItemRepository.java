package com.foliopro.api.repository;

import com.foliopro.api.model.UsaWatchlistItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsaWatchlistItemRepository extends JpaRepository<UsaWatchlistItemEntity, Long> {
    Page<UsaWatchlistItemEntity> findByWatchlistId(Long watchlistId, Pageable pageable);

    Optional<UsaWatchlistItemEntity> findByWatchlistIdAndSymbol(Long watchlistId, String symbol);

    List<UsaWatchlistItemEntity> findByWatchlistId(Long watchlistId);

    void deleteByWatchlistId(Long watchlistId);
}

package com.foliopro.api.repository;
import com.foliopro.api.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PortfolioRepository extends JpaRepository<PortfolioEntity,Long> {}
public interface HoldingRepository extends JpaRepository<HoldingEntity,Long> { List<HoldingEntity> findByPortfolioIdOrderById(Long id); void deleteByPortfolioId(Long id); }
public interface WatchlistRepository extends JpaRepository<WatchlistEntity,Long> { Page<WatchlistEntity> findByMarket(String market,Pageable pageable); }
public interface WatchlistItemRepository extends JpaRepository<WatchlistItemEntity,Long> { Page<WatchlistItemEntity> findByWatchlistId(Long id,Pageable pageable); Optional<WatchlistItemEntity> findByWatchlistIdAndSymbol(Long id,String symbol); List<WatchlistItemEntity> findByWatchlistId(Long id); }
public interface UserPreferencesRepository extends JpaRepository<UserPreferencesEntity,Long> { Optional<UserPreferencesEntity> findByUserKey(String userKey); }

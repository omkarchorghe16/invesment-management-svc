package com.foliopro.api.repository;

import com.foliopro.api.model.HoldingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoldingRepository extends JpaRepository<HoldingEntity, Long> {
    List<HoldingEntity> findByPortfolioIdOrderById(Long id);

    void deleteByPortfolioId(Long id);
}

package com.foliopro.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="holdings", uniqueConstraints=@UniqueConstraint(columnNames={"portfolio_id","symbol"}))
public class HoldingEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="portfolio_id",nullable=false) private PortfolioEntity portfolio;
 @Column(nullable=false,length=20) private String symbol;
 @Column(name="instrument_name",nullable=false,length=160) private String instrument;
 @Column(nullable=false,precision=24,scale=8) private BigDecimal quantity;
 @Column(name="average_cost",nullable=false,precision=20,scale=6) private BigDecimal averageCost;
 @Column(name="market_value",precision=20,scale=6) private BigDecimal marketValue;
 @Column(nullable=false,length=3) private String currency;
 public Long getId(){return id;} public PortfolioEntity getPortfolio(){return portfolio;} public void setPortfolio(PortfolioEntity v){portfolio=v;} public String getSymbol(){return symbol;} public void setSymbol(String v){symbol=v.toUpperCase();} public String getInstrument(){return instrument;} public void setInstrument(String v){instrument=v;} public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;} public BigDecimal getAverageCost(){return averageCost;} public void setAverageCost(BigDecimal v){averageCost=v;} public BigDecimal getMarketValue(){return marketValue;} public void setMarketValue(BigDecimal v){marketValue=v;} public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
}

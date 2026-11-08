package com.foliopro.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="watchlist_items", uniqueConstraints=@UniqueConstraint(columnNames={"watchlist_id","symbol"}))
public class WatchlistItemEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="watchlist_id",nullable=false) private WatchlistEntity watchlist;
 @Column(nullable=false,length=15) private String symbol;
 @Column(nullable=false,length=160) private String name;
 @Column(precision=20,scale=6) private BigDecimal price;
 @Column(name="change_percent",precision=12,scale=6) private BigDecimal change;
 @Column(length=12) private String trend;
 @Column(name="asset_type",length=50) private String assetType;
 @Column(length=100) private String sector;
 @Column(precision=8,scale=3) private BigDecimal score;
 @Column(length=60) private String volume;
 @Column(columnDefinition="text") private String metrics;
 public Long getId(){return id;} public WatchlistEntity getWatchlist(){return watchlist;} public void setWatchlist(WatchlistEntity v){watchlist=v;} public String getSymbol(){return symbol;} public void setSymbol(String v){symbol=v.toUpperCase();} public String getName(){return name;} public void setName(String v){name=v;} public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;} public BigDecimal getChange(){return change;} public void setChange(BigDecimal v){change=v;} public String getTrend(){return trend;} public void setTrend(String v){trend=v;} public String getAssetType(){return assetType;} public void setAssetType(String v){assetType=v;} public String getSector(){return sector;} public void setSector(String v){sector=v;} public BigDecimal getScore(){return score;} public void setScore(BigDecimal v){score=v;} public String getVolume(){return volume;} public void setVolume(String v){volume=v;} public String getMetrics(){return metrics;} public void setMetrics(String v){metrics=v;}
}

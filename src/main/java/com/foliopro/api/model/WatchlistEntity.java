package com.foliopro.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="watchlists")
public class WatchlistEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=40) private String name;
 @Column(nullable=false,length=10) private String market;
 @Column(nullable=false,length=3) private String currency;
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getMarket(){return market;} public void setMarket(String v){market=v;} public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
}

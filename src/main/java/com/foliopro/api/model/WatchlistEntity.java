package com.foliopro.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@SequenceGenerator(name="watchlists_sequence",sequenceName="watchlists_id_seq",allocationSize=1)
public abstract class WatchlistEntity {
 @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="watchlists_sequence") private Long id;
 @Column(nullable=false,length=40) private String name;
 @Column(nullable=false,length=10) private String market;
 @Column(nullable=false,length=3) private String currency;
 protected WatchlistEntity() {}
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getMarket(){return market;} public void setMarket(String v){market=v;} public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
}

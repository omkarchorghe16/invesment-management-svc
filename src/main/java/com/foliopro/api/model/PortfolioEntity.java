package com.foliopro.api.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="portfolios")
public class PortfolioEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=100) private String name;
 @Column(nullable=false,length=40) private String country;
 @Column(nullable=false,length=3) private String currency;
 @Column(name="asset_type") private String assetType;
 @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getCountry(){return country;} public void setCountry(String v){country=v;} public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;} public String getAssetType(){return assetType;} public void setAssetType(String v){assetType=v;} public Instant getCreatedAt(){return createdAt;} public void touch(){updatedAt=Instant.now();}
}

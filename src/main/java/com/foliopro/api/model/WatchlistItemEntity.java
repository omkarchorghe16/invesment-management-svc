package com.foliopro.api.model;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class WatchlistItemEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=15) private String symbol;
 @Column(nullable=false,length=160) private String name;
 @Column(name="is_favorite",nullable=false) private boolean favorite;
 public Long getId(){return id;} public String getSymbol(){return symbol;} public void setSymbol(String v){symbol=v.toUpperCase(java.util.Locale.ROOT);} public String getName(){return name;} public void setName(String v){name=v;} public boolean isFavorite(){return favorite;} public void setFavorite(boolean v){favorite=v;}
}

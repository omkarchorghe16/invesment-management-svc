package com.foliopro.api.model;

import jakarta.persistence.*;

@Entity @Table(name="user_preferences")
public class UserPreferencesEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="user_key",nullable=false,unique=true,length=120) private String userKey;
 @Column(name="favorite_watchlists",nullable=false,columnDefinition="text") private String favoriteWatchlists="[]";
 @Column(name="favorite_symbols",nullable=false,columnDefinition="text") private String favoriteSymbols="{\"US\":[],\"India\":[]}";
 @Column(name="watchlist_columns",nullable=false,columnDefinition="text") private String watchlistColumns="[\"symbol\",\"name\",\"assetType\",\"price\",\"change\",\"trend\"]";
 public Long getId(){return id;} public String getUserKey(){return userKey;} public void setUserKey(String v){userKey=v;} public String getFavoriteWatchlists(){return favoriteWatchlists;} public void setFavoriteWatchlists(String v){favoriteWatchlists=v;} public String getFavoriteSymbols(){return favoriteSymbols;} public void setFavoriteSymbols(String v){favoriteSymbols=v;} public String getWatchlistColumns(){return watchlistColumns;} public void setWatchlistColumns(String v){watchlistColumns=v;}
}

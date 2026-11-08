package com.foliopro.api.config;

import com.foliopro.api.model.*;
import com.foliopro.api.repository.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import java.math.BigDecimal;

@Configuration
public class DevelopmentSampleData {
 @Bean ApplicationRunner sampleData(WatchlistRepository lists,UsaWatchlistItemRepository usaItems,IndiaWatchlistItemRepository indiaItems,Environment env){return args->{if(!java.util.Arrays.asList(env.getActiveProfiles()).contains("dev")||lists.count()>0)return;
  UsaWatchlistEntity w=new UsaWatchlistEntity();w.setName("Growth picks");w=lists.save(w);
  add(usaItems,w,"NVDA","NVIDIA Corporation",true);
  add(usaItems,w,"MSFT","Microsoft Corporation",false);
  IndiaWatchlistEntity in=new IndiaWatchlistEntity();in.setName("India equities");in=lists.save(in);
  add(indiaItems,in,"RELIANCE","Reliance Industries",true);
 };}
 private void add(UsaWatchlistItemRepository repo,UsaWatchlistEntity w,String symbol,String name,boolean favorite){UsaWatchlistItemEntity i=new UsaWatchlistItemEntity();i.setWatchlist(w);i.setSymbol(symbol);i.setName(name);i.setFavorite(favorite);repo.save(i);}
 private void add(IndiaWatchlistItemRepository repo,IndiaWatchlistEntity w,String symbol,String name,boolean favorite){IndiaWatchlistItemEntity i=new IndiaWatchlistItemEntity();i.setWatchlist(w);i.setSymbol(symbol);i.setName(name);i.setFavorite(favorite);repo.save(i);}
}

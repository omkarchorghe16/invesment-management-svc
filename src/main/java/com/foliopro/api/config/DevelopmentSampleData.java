package com.foliopro.api.config;

import com.foliopro.api.model.*;
import com.foliopro.api.repository.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import java.math.BigDecimal;

@Configuration
public class DevelopmentSampleData {
 @Bean ApplicationRunner sampleData(WatchlistRepository lists,WatchlistItemRepository items,Environment env){return args->{if(!java.util.Arrays.asList(env.getActiveProfiles()).contains("dev")||lists.count()>0)return;
  WatchlistEntity w=new WatchlistEntity();w.setName("Growth picks");w.setMarket("US");w.setCurrency("USD");w=lists.save(w);
  add(items,w,"NVDA","NVIDIA Corporation","127.46","4.20","Bullish","Stock","Technology",91,"58.1M");
  add(items,w,"MSFT","Microsoft Corporation","426.18","1.87","Bullish","Stock","Technology",89,"21.8M");
  WatchlistEntity in=new WatchlistEntity();in.setName("India equities");in.setMarket("India");in.setCurrency("INR");in=lists.save(in);
  add(items,in,"RELIANCE","Reliance Industries","2940.50","1.18","Bullish","Stock","Energy",85,"4.2M");
 };}
 private void add(WatchlistItemRepository repo,WatchlistEntity w,String symbol,String name,String price,String change,String trend,String type,String sector,int score,String volume){WatchlistItemEntity i=new WatchlistItemEntity();i.setWatchlist(w);i.setSymbol(symbol);i.setName(name);i.setPrice(new BigDecimal(price));i.setChange(new BigDecimal(change));i.setTrend(trend);i.setAssetType(type);i.setSector(sector);i.setScore(BigDecimal.valueOf(score));i.setVolume(volume);repo.save(i);}
}

package com.foliopro.api.controller;

import com.foliopro.api.dto.ApiDtos.DashboardResponse;
import java.util.*;

final class DashboardSamples {
 private DashboardSamples(){}
 static DashboardResponse response(){
  Map<String,Object> summary=Map.of("totalValue",1284600,"totalProfit",216400,"dayChange",18450,"monthlyReturn",12.8,"riskScore",67,"currency","USD","note","Illustrative sample data; not live market values");
  List<Map<String,Object>> growth=List.of(Map.of("date","2026-01-01","value",1068200),Map.of("date","2026-02-01","value",1094100),Map.of("date","2026-03-01","value",1118200),Map.of("date","2026-04-01","value",1145500),Map.of("date","2026-05-01","value",1191600),Map.of("date","2026-06-01","value",1284600));
  List<Map<String,Object>> us=List.of(Map.of("name","Stocks","percent",24),Map.of("name","Mutual funds","percent",10),Map.of("name","ETFs","percent",14),Map.of("name","Property","percent",21),Map.of("name","Bonds / fixed income","percent",9),Map.of("name","Cash","percent",7),Map.of("name","Crypto","percent",5),Map.of("name","Other","percent",10));
  List<Map<String,Object>> india=List.of(Map.of("name","Stocks","percent",20),Map.of("name","Mutual funds","percent",18),Map.of("name","ETFs","percent",5),Map.of("name","Property","percent",32),Map.of("name","Bonds / fixed deposits","percent",13),Map.of("name","Crypto","percent",2),Map.of("name","Gold / commodities","percent",10));
  List<Map<String,Object>> portfolios=List.of(Map.of("name","Core investment account","country","United States","type","Stocks & ETFs","value","$642,800","profit","+$84,200","tone","up"),Map.of("name","Property portfolio","country","United States","type","Real estate & land","value","$418,000","profit","+$62,500","tone","up"),Map.of("name","Long-term funds","country","India","type","Mutual funds","value","₹32,40,000","profit","+₹4,82,000","tone","up"));
  List<Map<String,Object>> gainers=List.of(Map.of("symbol","NVDA","name","NVIDIA","change",4.2),Map.of("symbol","AAPL","name","Apple","change",2.91),Map.of("symbol","MSFT","name","Microsoft","change",1.87));
  List<Map<String,Object>> losers=List.of(Map.of("symbol","TSLA","name","Tesla","change",-2.35),Map.of("symbol","META","name","Meta Platforms","change",-1.20));
  return new DashboardResponse(summary,growth,us,india,portfolios,gainers,losers,true);
 }
}

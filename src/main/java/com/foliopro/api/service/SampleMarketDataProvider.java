package com.foliopro.api.service;

import com.foliopro.api.dto.ApiDtos.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SampleMarketDataProvider {
 public List<InstrumentResponse> search(String q,String market){List<InstrumentResponse> samples=List.of(
  new InstrumentResponse("AAPL","Apple Inc.","US","USD","Stock","Technology",new BigDecimal("214.22"),new BigDecimal("2.91"),Map.of(),true),
  new InstrumentResponse("MSFT","Microsoft Corporation","US","USD","Stock","Technology",new BigDecimal("426.18"),new BigDecimal("1.87"),Map.of(),true),
  new InstrumentResponse("NVDA","NVIDIA Corporation","US","USD","Stock","Semiconductors",new BigDecimal("127.46"),new BigDecimal("4.20"),Map.of(),true),
  new InstrumentResponse("RELIANCE","Reliance Industries","India","INR","Stock","Energy",new BigDecimal("2940.50"),new BigDecimal("1.18"),Map.of(),true),
  new InstrumentResponse("TCS","Tata Consultancy Services","India","INR","Stock","IT",new BigDecimal("3890.20"),new BigDecimal("0.72"),Map.of(),true),
  new InstrumentResponse("INFY","Infosys","India","INR","Stock","IT",new BigDecimal("1472.80"),new BigDecimal("1.36"),Map.of(),true));
  String term=q==null?"":q.toLowerCase(Locale.ROOT);return samples.stream().filter(x->market==null||x.market().equalsIgnoreCase(market)).filter(x->term.isBlank()||x.symbol().toLowerCase(Locale.ROOT).contains(term)||x.name().toLowerCase(Locale.ROOT).contains(term)).toList();
 }
}

package com.foliopro.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class ApiDtos {
    private ApiDtos() {}
    public record HoldingRequest(@NotBlank @Size(max=20) String symbol, @NotBlank @Size(max=160) String instrument,
      @NotNull @DecimalMin(value="0.00000001") BigDecimal quantity, @NotNull @DecimalMin("0") BigDecimal averageCost,
      @DecimalMin("0") BigDecimal marketValue, @NotBlank @Pattern(regexp="USD|INR") String currency) {}
    public record PortfolioRequest(@NotBlank @Size(max=100) String name, @NotBlank @Size(max=40) String country,
      @NotBlank @Pattern(regexp="USD|INR") String currency, @Size(max=160) String assetType,
      @Valid List<HoldingRequest> holdings) {}
    public record HoldingResponse(String symbol, String instrument, BigDecimal quantity, BigDecimal averageCost, BigDecimal marketValue, String currency) {}
    public record PortfolioResponse(Long id, String name, String country, String currency, String assetType, List<HoldingResponse> holdings, boolean sampleData) {}
    public record WatchlistRequest(@NotBlank @Size(max=40) String name, @NotBlank @Pattern(regexp="US|India") String market) {}
    public record WatchlistItemRequest(@NotBlank @Pattern(regexp="[A-Za-z0-9.-]{1,15}") String symbol, @Size(max=160) String name,
      BigDecimal price, BigDecimal change, String trend, @Size(max=50) String assetType, @Size(max=100) String sector,
      BigDecimal score, @Size(max=60) String volume, Map<String,Object> metrics) {}
    public record BulkItemsRequest(@NotEmpty @Size(max=100) @Valid List<WatchlistItemRequest> items) {}
    public record WatchlistItemResponse(Long id, String symbol, String name, BigDecimal price, BigDecimal change, String trend,
      String assetType, String sector, BigDecimal score, String volume, Map<String,Object> metrics, boolean sampleData) {}
    public record WatchlistResponse(Long id, String name, String market, String currency, List<WatchlistItemResponse> items) {}
    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {}
    public record PreferencesRequest(@NotNull List<@NotBlank String> favoriteWatchlists,
      @NotNull Map<String,List<@NotBlank String>> favoriteSymbols, @NotEmpty List<@NotBlank String> watchlistColumns) {}
    public record PreferencesResponse(List<String> favoriteWatchlists, Map<String,List<String>> favoriteSymbols, List<String> watchlistColumns) {}
    public record InstrumentResponse(String symbol, String name, String market, String currency, String assetType, String sector,
      BigDecimal price, BigDecimal change, Map<String,Object> metrics, boolean sampleData) {}
    public record DashboardResponse(Map<String,Object> summary, List<Map<String,Object>> growthHistory,
      List<Map<String,Object>> usAllocation, List<Map<String,Object>> indiaAllocation,
      List<Map<String,Object>> portfolios, List<Map<String,Object>> topGainers, List<Map<String,Object>> topLosers, boolean sampleData) {}
}

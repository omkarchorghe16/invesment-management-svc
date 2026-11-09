package com.foliopro.api.service;

import com.foliopro.api.dto.ApiDtos.WatchlistImportResponse;
import com.foliopro.api.model.IndiaWatchlistEntity;
import com.foliopro.api.model.IndiaWatchlistItemEntity;
import com.foliopro.api.model.UsaWatchlistEntity;
import com.foliopro.api.model.UsaWatchlistItemEntity;
import com.foliopro.api.model.WatchlistEntity;
import com.foliopro.api.repository.IndiaWatchlistItemRepository;
import com.foliopro.api.repository.UsaWatchlistItemRepository;
import com.foliopro.api.repository.WatchlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class WatchlistImportService {
    private static final int MAX_WATCHLIST_NAME_LENGTH = 40;
    private static final int MAX_SYMBOL_LENGTH = 15;
    private static final String SYMBOL_PATTERN = "[A-Za-z0-9.-]{1,15}";

    private final WatchlistRepository watchlists;
    private final UsaWatchlistItemRepository usaItems;
    private final IndiaWatchlistItemRepository indiaItems;

    public WatchlistImportService(WatchlistRepository watchlists, UsaWatchlistItemRepository usaItems,
                                  IndiaWatchlistItemRepository indiaItems) {
        this.watchlists = watchlists;
        this.usaItems = usaItems;
        this.indiaItems = indiaItems;
    }

    @Transactional
    public WatchlistImportResponse importWatchlists(MultipartFile file, String market, Long userId) {
        if (userId == null || userId < 1) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        String normalizedMarket = normalizeMarket(market);
        String csv;
        try {
            csv = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read CSV file", e);
        }

        List<WatchlistData> parsed = parse(csv);
        List<WatchlistEntity> entities = new ArrayList<>(parsed.size());
        List<String> names = new ArrayList<>(parsed.size());
        for (WatchlistData data : parsed) {
            WatchlistEntity watchlist = normalizedMarket.equals("US")
                    ? new UsaWatchlistEntity()
                    : new IndiaWatchlistEntity();
            if (watchlist instanceof UsaWatchlistEntity usa) {
                usa.setUserId(userId);
            }
            watchlist.setName(data.name());
            entities.add(watchlist);
            names.add(data.name());
        }

        List<WatchlistEntity> savedWatchlists = watchlists.saveAll(entities);
        int savedItems = 0;
        for (int i = 0; i < parsed.size(); i++) {
            WatchlistEntity watchlist = savedWatchlists.get(i);
            for (String symbol : parsed.get(i).symbols()) {
                if (watchlist instanceof UsaWatchlistEntity usa) {
                    UsaWatchlistItemEntity item = new UsaWatchlistItemEntity();
                    item.setWatchlist(usa);
                    item.setSymbol(symbol);
                    item.setName(symbol);
                    usaItems.save(item);
                } else {
                    IndiaWatchlistItemEntity item = new IndiaWatchlistItemEntity();
                    item.setWatchlist((IndiaWatchlistEntity) watchlist);
                    item.setSymbol(symbol);
                    item.setName(symbol);
                    indiaItems.save(item);
                }
                savedItems++;
            }
        }

        return new WatchlistImportResponse(savedWatchlists.size(), savedItems, names);
    }

    private String normalizeMarket(String market) {
        if ("USA".equalsIgnoreCase(market) || "US".equalsIgnoreCase(market)) {
            return "US";
        }
        if ("India".equalsIgnoreCase(market)) {
            return "India";
        }
        throw new IllegalArgumentException("Market must be USA or India");
    }

    private List<WatchlistData> parse(String csv) {
        List<List<String>> rows = parseRows(csv);
        List<List<String>> nonEmptyRows = rows.stream()
                .filter(row -> row.stream().anyMatch(cell -> !cell.isBlank()))
                .toList();
        if (nonEmptyRows.isEmpty()) {
            throw new IllegalArgumentException("CSV must contain watchlist names in its first row");
        }

        List<String> headers = nonEmptyRows.getFirst();
        List<WatchlistData> watchlistData = new ArrayList<>(headers.size());
        Set<String> names = new HashSet<>();
        for (String header : headers) {
            String name = header.trim();
            if (name.isEmpty() || name.length() > MAX_WATCHLIST_NAME_LENGTH) {
                throw new IllegalArgumentException("Watchlist names must contain 1 to "
                        + MAX_WATCHLIST_NAME_LENGTH + " characters");
            }
            if (!names.add(name.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Duplicate watchlist name: " + name);
            }
            watchlistData.add(new WatchlistData(name, new ArrayList<>(), new HashSet<>()));
        }

        for (int rowIndex = 1; rowIndex < nonEmptyRows.size(); rowIndex++) {
            List<String> row = nonEmptyRows.get(rowIndex);
            if (row.size() > headers.size()) {
                throw new IllegalArgumentException("CSV row " + (rowIndex + 1)
                        + " has more columns than the watchlist header row");
            }
            for (int column = 0; column < row.size(); column++) {
                String symbol = row.get(column).trim();
                if (symbol.isEmpty()) {
                    continue;
                }
                if (symbol.length() > MAX_SYMBOL_LENGTH || !symbol.matches(SYMBOL_PATTERN)) {
                    throw new IllegalArgumentException("Invalid stock symbol at row "
                            + (rowIndex + 1) + ", column " + (column + 1));
                }
                WatchlistData list = watchlistData.get(column);
                String normalized = symbol.toUpperCase(Locale.ROOT);
                if (!list.seenSymbols().add(normalized)) {
                    throw new IllegalArgumentException("Duplicate symbol in watchlist "
                            + list.name() + ": " + normalized);
                }
                list.symbols().add(normalized);
            }
        }
        return watchlistData;
    }

    private List<List<String>> parseRows(String csv) {
        if (csv.startsWith("\uFEFF")) {
            csv = csv.substring(1);
        }
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean afterQuote = false;
        boolean recordStarted = false;

        for (int i = 0; i < csv.length(); i++) {
            char current = csv.charAt(i);
            if (inQuotes) {
                if (current == '"') {
                    if (i + 1 < csv.length() && csv.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                        afterQuote = true;
                    }
                } else {
                    field.append(current);
                }
            } else if (afterQuote) {
                if (current == ',') {
                    addField(row, field);
                    afterQuote = false;
                    recordStarted = true;
                } else if (current == '\n' || current == '\r') {
                    addField(row, field);
                    rows.add(row);
                    row = new ArrayList<>();
                    afterQuote = false;
                    recordStarted = false;
                    if (current == '\r' && i + 1 < csv.length() && csv.charAt(i + 1) == '\n') {
                        i++;
                    }
                } else {
                    throw new IllegalArgumentException("Malformed CSV: unexpected character after a quoted field");
                }
            } else if (current == '"') {
                if (!field.isEmpty()) {
                    throw new IllegalArgumentException("Malformed CSV: quote inside an unquoted field");
                }
                inQuotes = true;
                recordStarted = true;
            } else if (current == ',') {
                addField(row, field);
                recordStarted = true;
            } else if (current == '\n' || current == '\r') {
                addField(row, field);
                rows.add(row);
                row = new ArrayList<>();
                recordStarted = false;
                if (current == '\r' && i + 1 < csv.length() && csv.charAt(i + 1) == '\n') {
                    i++;
                }
            } else {
                field.append(current);
                recordStarted = true;
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("Malformed CSV: unterminated quoted field");
        }
        if (recordStarted || afterQuote || !row.isEmpty() || !field.isEmpty()) {
            addField(row, field);
            rows.add(row);
        }
        return rows;
    }

    private void addField(List<String> row, StringBuilder field) {
        row.add(field.toString());
        field.setLength(0);
    }

    private record WatchlistData(String name, List<String> symbols, Set<String> seenSymbols) {}
}

package com.foliopro.api.controller;

import com.foliopro.api.dto.ApiDtos.*;
import com.foliopro.api.service.FolioService;
import com.foliopro.api.service.WatchlistImportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/watchlists")
@Validated
@Tag(name = "Watchlists", description = "Watchlist CRUD, items, pagination, and CSV import")
public class WatchlistController {
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;
    private final FolioService service;
    private final WatchlistImportService importService;

    public WatchlistController(FolioService service, WatchlistImportService importService) {
        this.service = service;
        this.importService = importService;
    }

    @Operation(summary = "List watchlists with tickers",
            description = "Returns a page of watchlists with every ticker and its name nested in each watchlist's items array. Pagination applies to watchlists; omit market to include both US and India.")
    @GetMapping
    public PageResponse<WatchlistResponse> watchlists(
            @RequestParam(required = false) @Pattern(regexp = "US|India") String market,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "15") @Min(1) @Max(100) int size) {
        return service.listWatchlists(market, page, size);
    }

    @Operation(summary = "Create a watchlist",
            description = "Creates an empty watchlist for the selected market. For USA, userId defaults to 1 if omitted.")
    @PostMapping
    public WatchlistResponse createWatchlist(@Valid @RequestBody WatchlistRequest request) {
        return service.createWatchlist(request);
    }

    @Operation(summary = "Get a watchlist", description = "Returns a watchlist with a page of its items.")
    @GetMapping("/{id}")
    public WatchlistResponse watchlist(
            @PathVariable @Positive long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "15") @Min(1) @Max(100) int size) {
        return service.getWatchlist(id, page, size);
    }

    @Operation(summary = "Rename a watchlist", description = "Updates the watchlist name. Its market cannot be changed.")
    @PatchMapping("/{id}")
    public WatchlistResponse renameWatchlist(
            @PathVariable @Positive long id, @Valid @RequestBody WatchlistRequest request) {
        return service.renameWatchlist(id, request);
    }

    @Operation(summary = "Delete a watchlist", description = "Deletes the watchlist and its items.")
    @DeleteMapping("/{id}")
    public void deleteWatchlist(@PathVariable @Positive long id) {
        service.deleteWatchlist(id);
    }

    @Operation(summary = "Add items to a watchlist",
            description = "Adds up to 100 items atomically. Duplicate symbols in a list are rejected.")
    @PostMapping("/{id}/items")
    public List<WatchlistItemResponse> addItems(
            @PathVariable @Positive long id, @Valid @RequestBody BulkItemsRequest request) {
        return service.addItems(id, request);
    }

    @Operation(summary = "Update a watchlist item", description = "Updates an item identified by its symbol.")
    @PutMapping("/{id}/items/{symbol}")
    public WatchlistItemResponse updateItem(
            @PathVariable @Positive long id,
            @PathVariable String symbol,
            @Valid @RequestBody WatchlistItemRequest request) {
        return service.updateItem(id, symbol, request);
    }

    @Operation(summary = "Remove an item from a watchlist")
    @DeleteMapping("/{id}/items/{symbol}")
    public void removeItem(@PathVariable @Positive long id, @PathVariable String symbol) {
        service.removeItem(id, symbol);
    }

    @Operation(
            summary = "Import watchlists from CSV",
            description = """
                    Upload a column-oriented CSV file. The first row contains watchlist names; each subsequent
                    column contains the stock symbols for that watchlist. Empty cells are ignored. Names and
                    symbols must be unique within the import. The complete file is validated before any records
                    are saved. Maximum file size is 5 MB.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "object", requiredProperties = {"file"})
                    )
            )
    )
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistImportResponse importWatchlists(
            @Parameter(description = "Column-oriented watchlist CSV file", required = true,
                    content = @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary")))
            @RequestPart("file") MultipartFile file,
             @Parameter(description = "Target watchlist table: USA or India", example = "USA",
                     schema = @Schema(allowableValues = {"USA", "India", "US"}, defaultValue = "USA"))
            @RequestParam(defaultValue = "USA") String market,
            @Parameter(description = "USA watchlist owner ID. Defaults to 1.", example = "1")
            @RequestParam(defaultValue = "1") Long userId) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file must not be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "CSV file must not exceed 5 MB");
        }
        return importService.importWatchlists(file, market, userId);
    }
}

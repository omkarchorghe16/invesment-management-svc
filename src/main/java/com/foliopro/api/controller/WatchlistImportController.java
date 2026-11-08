package com.foliopro.api.controller;

import com.foliopro.api.dto.ApiDtos.WatchlistImportResponse;
import com.foliopro.api.service.WatchlistImportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/watchlists")
@Tag(name = "Watchlist import", description = "Bulk import column-oriented CSV watchlists")
public class WatchlistImportController {
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;
    private final WatchlistImportService importService;

    public WatchlistImportController(WatchlistImportService importService) {
        this.importService = importService;
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
            @RequestParam(defaultValue = "USA") String market) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file must not be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "CSV file must not exceed 5 MB");
        }
        return importService.importWatchlists(file, market);
    }
}

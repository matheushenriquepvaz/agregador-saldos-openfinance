package ada.teste.dto;

import java.util.List;

public record SeedDataResponse(
        String message,
        List<String> seededInstitutions,
        long finbrasRecords,
        long externalRecords
) {
}


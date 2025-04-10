package info.mackiewicz.bankapp.presentation.dashboard.dto;

import org.hibernate.query.SortDirection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SortDirectionConverter implements Converter<String, SortDirection> {

    @Override
    public SortDirection convert(@NonNull String source) {
        if (source == null || source.isEmpty()) {
            return SortDirection.DESCENDING; // Default value if null or empty
        }
        
        return switch (source.toLowerCase()) {
            case "ascending", "asc" -> SortDirection.ASCENDING;
            case "descending", "desc" -> SortDirection.DESCENDING;
            default -> throw new IllegalArgumentException("Invalid sort direction: " + source);
        };
    }
}
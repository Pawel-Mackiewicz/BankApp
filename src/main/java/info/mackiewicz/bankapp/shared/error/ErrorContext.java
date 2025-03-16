package info.mackiewicz.bankapp.shared.error;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class ErrorContext {
    private final String path;     // Request path
    private final String domain;   // Module domain (security, transaction, etc.)
    
    @Builder.Default
    private final Map<String, Object> attributes = new HashMap<>();  // Additional context

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}
package info.mackiewicz.bankapp.utils;

import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtUtil {
    private static final String DEFAULT_SECRET = "defaultSecretKeyForTestingPurposesOnly12345678";
    private static final String SECRET_KEY = System.getenv("SECRET_KEY") != null ? 
            System.getenv("SECRET_KEY") : DEFAULT_SECRET;
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(String userEmail) {
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dzień
                .signWith(key)
                .compact();
    }
    
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

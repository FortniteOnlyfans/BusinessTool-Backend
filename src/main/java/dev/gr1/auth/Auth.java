package dev.gr1.auth;

import dev.gr1.Env;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Auth {
    private static final String FALLBACK_SECRET = "ashdjahr83urbfsefer<ye38434nj23hu2JHuI§Jfh3jfi39";
    private static final SecretKey KEY = makeKey();

    private static SecretKey makeKey() {
        String secret = System.getenv("FNOF_BPT_SECRET_KEY");
        if (secret == null) {
            System.err.println("WARNING: The env. var. FNOF_BPT_SECRET_KEY isn't set -> using an UNSECURE fallback secret.");
            secret = FALLBACK_SECRET;
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String rawPassword, String hashed) {
        return BCrypt.checkpw(rawPassword, hashed);
    }

    public static String createToken(String username) {
        //load var and convert to millis
        int lifespan = Env.getInt("ACCESS_TOKEN_LIFESPAN") * 3600000;
            return Jwts.builder()
                .subject(username)
                //expiration is in one hour from now
                .expiration(new Date(System.currentTimeMillis() + lifespan))
                .signWith(KEY)
                .compact();
    }

    public static boolean trustworthy(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

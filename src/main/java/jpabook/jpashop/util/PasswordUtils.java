package jpabook.jpashop.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;


@Component
public class PasswordUtils {


    private String SALT_CREATOR_ALGORITHM="SHA1PRNG";

    private String ENCODE_ALGORITHM="PBKDF2WithHmacSHA512";

    @Value("${spring.security.password.hashwidth}")
    private Integer HASH_WIDTH;

    @Value("${spring.security.password.iterations}")
    private Integer ITERATIONS;




    public byte[] createSalt() {
        try {
            SecureRandom random = SecureRandom.getInstance(SALT_CREATOR_ALGORITHM);
            byte[] bytes = new byte[HASH_WIDTH];

            random.nextBytes(bytes);
            return bytes;

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String encodePassword(String rawPassword, byte[] salt){
        try {


            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATIONS, HASH_WIDTH * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ENCODE_ALGORITHM);
            byte[] hashedPassword = skf.generateSecret(spec).getEncoded();


            return Base64.getEncoder().encodeToString(hashedPassword);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean matches(String givenPassword, byte[] givenSalt, String encodedPassword) {
        String createdPassword = encodePassword(givenPassword, givenSalt);

        return createdPassword.equals(encodedPassword);
    }
}


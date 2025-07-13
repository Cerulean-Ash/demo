package com.example.demo.configuration;

import com.nimbusds.jose.jwk.RSAKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class KeyConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(KeyConfiguration.class);

    @Bean
    public KeyPair rsaKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // 2048 bits is a common and secure key size
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            logger.info("Dynamically generated RSA KeyPair.");
            logger.debug("Public Key Algorithm: {}", keyPair.getPublic().getAlgorithm());
            logger.debug("Private Key Algorithm: {}", keyPair.getPrivate().getAlgorithm());
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to generate RSA KeyPair", e);
        }
    }

    @Bean
    public RSAKey rsaKey(KeyPair keyPair) {
        logger.info("Creating RSAKey from generated KeyPair.");
        return new RSAKey
                .Builder((RSAPublicKey) keyPair.getPublic()) // Set the public key
                .privateKey((RSAPrivateKey) keyPair.getPrivate()) // Set the private key
                .keyID(UUID.randomUUID().toString()) // Assign a unique Key ID
                .build();
    }
}

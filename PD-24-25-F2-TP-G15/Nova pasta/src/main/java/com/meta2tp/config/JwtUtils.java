package com.meta2tp.config;

public class JwtUtils {

    private static final String SECRET_KEY = "mysecretkey"; // Use uma chave secreta mais segura em produção
    private static final long EXPIRATION_TIME = 86400000; // 1 dia em milissegundos

    // Método para gerar um token JWT
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // Método para validar um token JWT
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Log or handle invalid token
            return false;
        }
    }

    // Método para extrair o nome do usuário do token JWT
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
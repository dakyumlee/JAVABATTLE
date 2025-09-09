
    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
            
            // userId 또는 nickname을 반환 (JWT에 저장된 필드에 따라)
            Object userId = claims.get("userId");
            if (userId != null) {
                return userId.toString();
            }
            
            // userId가 없으면 nickname 사용
            return claims.get("nickname", String.class);
            
        } catch (Exception e) {
            System.err.println("JWT 파싱 오류: " + e.getMessage());
            return null;
        }
    }

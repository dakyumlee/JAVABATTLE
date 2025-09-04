package com.javabattle.arena.web.auth;

import com.javabattle.arena.domain.player.Player;
import com.javabattle.arena.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final PlayerService playerService;
    
    @PostMapping("/signup")
    public SignupResponse signup(@RequestBody SignupRequest request) {
        Player player = playerService.createPlayer(request.nickname);
        return new SignupResponse(player.getId(), player.getNickname(), 
                                  player.getExp(), player.getRankTag());
    }
    
    @PostMapping("/login")  
    public LoginResponse login(@RequestBody LoginRequest request) {
        Player player = playerService.findByNickname(request.nickname);
        return new LoginResponse(player.getId(), player.getNickname(), 
                                 player.getExp(), player.getRankTag());
    }
    
    static class SignupRequest {
        public String nickname;
    }
    
    static class SignupResponse {
        public Long id;
        public String nickname;
        public int exp;
        public String rankTag;
        
        public SignupResponse(Long id, String nickname, int exp, String rankTag) {
            this.id = id;
            this.nickname = nickname;
            this.exp = exp;
            this.rankTag = rankTag;
        }
    }
    
    static class LoginRequest {
        public String nickname;
    }
    
    static class LoginResponse {
        public Long id;
        public String nickname;
        public int exp;
        public String rankTag;
        
        public LoginResponse(Long id, String nickname, int exp, String rankTag) {
            this.id = id;
            this.nickname = nickname;
            this.exp = exp;
            this.rankTag = rankTag;
        }
    }
}

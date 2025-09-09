package com.javabattle.arena.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class BattleController {
   
   @GetMapping("/battle")
   public String battlePage(@RequestParam(required = false) String mode,
                          @RequestParam(required = false) String room,
                          @RequestParam(required = false) String code,
                          Model model) {
       
       model.addAttribute("mode", mode != null ? mode : "random");
       model.addAttribute("roomName", room);
       model.addAttribute("inviteCode", code);
       
       return "battle";
   }
   
   @GetMapping("/battle-room")
   public String battleRoomPage(@RequestParam(required = false) String mode,
                               @RequestParam(required = false) String room,
                               @RequestParam(required = false) String roomId,
                               Model model) {
       
       model.addAttribute("mode", mode);
       model.addAttribute("roomName", room);
       model.addAttribute("roomId", roomId);
       
       return "battle-room";
   }
}

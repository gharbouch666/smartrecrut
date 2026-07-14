package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.service.MessageService;
import com.smartrecrute.smartrecrute.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService service;

    @GetMapping("/conversation/{userId1}/{userId2}")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long userId1, 
            @PathVariable Long userId2) {
        return ResponseEntity.ok(service.getConversation(userId1, userId2));
    }

    @PostMapping
    public ResponseEntity<Message> send(@RequestBody Map<String, Object> request) {
        Long senderId = ((Number) request.get("senderId")).longValue();
        Long receiverId = ((Number) request.get("receiverId")).longValue();
        String contenu = (String) request.get("contenu");
        return ResponseEntity.ok(service.sendMessage(senderId, receiverId, contenu));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam Long userId) {
        Long count = service.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestParam Long userId) {
        service.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread-per-user")
    public ResponseEntity<Map<Long, Long>> getUnreadCountPerUser(@RequestParam Long userId) {
        return ResponseEntity.ok(service.getUnreadCountPerUser(userId));
    }
    
    @PatchMapping("/read-from-user")
    public ResponseEntity<Void> markMessagesFromUserAsRead(@RequestParam Long userId, @RequestParam Long fromUserId) {
        service.markMessagesFromUserAsRead(userId, fromUserId);
        return ResponseEntity.ok().build();
    }
}
package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.Message;
import com.smartrecrute.smartrecrute.entity.Utilisateur;
import com.smartrecrute.smartrecrute.repository.MessageRepository;
import com.smartrecrute.smartrecrute.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    private MessageRepository repository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public List<Message> getConversation(Long userId1, Long userId2) {
        return repository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByDateEnvoiAsc(
            userId1, userId2, userId2, userId1);
    }

    public Message sendMessage(Long senderId, Long receiverId, String contenu) {
        Utilisateur sender = utilisateurRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        Utilisateur receiver = utilisateurRepository.findById(receiverId)
            .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContenu(contenu);
        message.setDateEnvoi(LocalDateTime.now());
        message.setLu(false);

        return repository.save(message);
    }

    public void markAsRead(Long messageId) {
        Message message = repository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setLu(true);
        repository.save(message);
    }
    
    public long getUnreadCount(Long userId) {
        return repository.countByReceiverIdAndLuFalse(userId);
    }
    
    public void markAllAsRead(Long userId) {
        repository.updateAllToReceiverAsRead(userId);
    }
    
    public void markMessagesFromUserAsRead(Long receiverId, Long senderId) {
        repository.updateMessagesFromSenderAsRead(receiverId, senderId);
    }
    
    public Map<Long, Long> getUnreadCountPerUser(Long userId) {
        Map<Long, Long> counts = new HashMap<>();
        List<Message> allMessages = repository.findAll();
        
        for (Message msg : allMessages) {
            if (msg.getReceiver() != null && msg.getReceiver().getId().equals(userId) && !msg.getLu()) {
                Long senderId = msg.getSender().getId();
                counts.put(senderId, counts.getOrDefault(senderId, 0L) + 1);
            }
        }
        
        return counts;
    }
}
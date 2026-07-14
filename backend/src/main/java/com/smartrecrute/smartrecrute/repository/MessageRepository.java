package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.Message;
import com.smartrecrute.smartrecrute.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrReceiverOrderByDateEnvoiDesc(Utilisateur sender, Utilisateur receiver);
    List<Message> findBySenderIdOrReceiverIdOrderByDateEnvoiDesc(Long senderId, Long receiverId);
    
    // Proper conversation query - messages between two specific users
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByDateEnvoiAsc(
        Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);
    
    // Count unread messages for a user
    long countByReceiverIdAndLuFalse(Long receiverId);
    
    // Mark all messages to a user as read
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.lu = true WHERE m.receiver.id = :receiverId")
    int updateAllToReceiverAsRead(Long receiverId);
    
    // Mark messages FROM a specific sender to a receiver as read
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.lu = true WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId")
    int updateMessagesFromSenderAsRead(Long receiverId, Long senderId);
}
package com.indra.chat.repository;

import com.indra.chat.entity.ConversationEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Integer> {

    @Query(value = "SELECT g.id FROM chat_group g WHERE g.url = :url", nativeQuery = true)
    int findGroupByUrl(@Param(value = "url") String url);

    @Query(value = "SELECT g.name FROM chat_group g WHERE g.url = :url", nativeQuery = true)
    String getGroupEntitiesBy(@Param(value = "url") String url);

    @Query(value = "SELECT g.url FROM chat_group g WHERE g.id = :id", nativeQuery = true)
    String getGroupUrlById(@Param(value = "id") Integer id);
    
    @Query("SELECT c.id FROM ConversationEntity c WHERE c.chatIdentifier = :chatIdentifier")
    int findConversationIdByChatIdentifier(@Param("chatIdentifier") String chatIdentifier);

    @Query("SELECT uc.userId FROM UserConversationEntity uc WHERE uc.conversationId = :conversationId")
    List<Integer> findUserIdsInConversation(@Param("conversationId") int conversationId);    
    

}

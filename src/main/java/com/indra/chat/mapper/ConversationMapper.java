package com.indra.chat.mapper;

import com.indra.chat.dto.ConversationDTO;
import com.indra.chat.entity.ConversationEntity;
import com.indra.chat.entity.UserEntity;
import com.indra.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConversationMapper {

    @Autowired
    private UserService userService;

    public ConversationDTO toConversationDTO(ConversationEntity conversationEntity) {
        ConversationDTO conversationDTO = new ConversationDTO();

        UserEntity user1 = conversationEntity.getUser1();
        UserEntity user2 = conversationEntity.getUser2();

        conversationDTO.setId(conversationEntity.getId());
        conversationDTO.setChatIdentifier(conversationEntity.getChatIdentifier());
        conversationDTO.setCreatedAt(conversationEntity.getCreatedAt());

        conversationDTO.setUser1Id(user1.getId());
        conversationDTO.setUser1FirstName(user1.getFirstName());
        conversationDTO.setUser1LastName(user1.getLastName());

        conversationDTO.setUser2Id(user2.getId());
        conversationDTO.setUser2FirstName(user2.getFirstName());
        conversationDTO.setUser2LastName(user2.getLastName());

        return conversationDTO;
    }
}

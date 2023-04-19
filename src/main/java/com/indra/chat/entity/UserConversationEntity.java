package com.indra.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_conversation")
@IdClass(UserConversationKey.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserConversationEntity implements Serializable {

    @Id
    private int userId;

    @Id
    private int conversationId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    UserEntity userMapping;

    @ManyToOne
    @MapsId("conversationId")
    @JoinColumn(name = "conversation_id")
    ConversationEntity conversationMapping;

    // Otros atributos relevantes para la relación entre usuario y conversación
    // ...

    @Override
    public int hashCode() {
        return Objects.hash(userId, conversationId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserConversationEntity userConversationKey = (UserConversationEntity) obj;
        return userId == userConversationKey.userId &&
                conversationId == userConversationKey.conversationId;
    }
}

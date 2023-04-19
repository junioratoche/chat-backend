package com.indra.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserConversationKey implements Serializable {

    @Column(name = "user_id")
    private int userId;

    @Column(name = "conversation_id")
    private int conversationId;

    @Override
    public int hashCode() {
        return Objects.hash(userId, conversationId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserConversationKey userConversationKey = (UserConversationKey) obj;
        return userId == userConversationKey.userId &&
                conversationId == userConversationKey.conversationId;
    }
}

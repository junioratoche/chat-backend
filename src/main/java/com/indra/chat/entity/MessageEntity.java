package com.indra.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    public MessageEntity(int userId, GroupEntity group, ConversationEntity conversation, String type, String message) {
        this.user_id = userId;
        this.group = group;
        this.conversation = conversation;
        this.type = type;
        this.message = message;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "msg_group_id", nullable = true)
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "msg_conversation_id", nullable = true)
    private ConversationEntity conversation;

    @Column(name = "msg_user_id")
    private int user_id;

    @Column(name = "type")
    private String type;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;
}

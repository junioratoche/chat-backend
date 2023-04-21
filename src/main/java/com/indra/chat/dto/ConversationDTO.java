package com.indra.chat.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO implements Serializable {

    private int id;
    private String chatIdentifier;
    private int user1Id;
    private String user1FirstName;
    private String user1LastName;
    private int user2Id;
    private String user2FirstName;
    private String user2LastName;
    private Timestamp createdAt;

    // Getters and Setters
}

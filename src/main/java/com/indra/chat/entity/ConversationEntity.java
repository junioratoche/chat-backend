package com.indra.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.indra.chat.utils.GroupTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "conversation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationEntity implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    @JsonIgnore
    private UserEntity user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    @JsonIgnore
    private UserEntity user2;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;
    
    @Column(name = "chat_identifier")
    private String chatIdentifier;

    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<MessageEntity> messages = new HashSet<>();
}

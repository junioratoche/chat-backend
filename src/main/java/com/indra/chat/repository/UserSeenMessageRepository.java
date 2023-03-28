package com.indra.chat.repository;

import com.indra.chat.entity.MessageUserEntity;
import com.indra.chat.entity.MessageUserKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSeenMessageRepository extends JpaRepository<MessageUserEntity, MessageUserKey> {

    MessageUserEntity findAllByMessageIdAndUserId(int messageId, int userId);

}

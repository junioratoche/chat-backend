package com.indra.chat.repository;

import com.indra.chat.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity,Integer> {

    FileEntity findByMessageId(int id);

    @Query(value = "SELECT storage.url FROM file_storage storage WHERE storage.fk_message_id = :id", nativeQuery = true)
    String findFileUrlByMessageId(@Param(value = "id") int id);
}

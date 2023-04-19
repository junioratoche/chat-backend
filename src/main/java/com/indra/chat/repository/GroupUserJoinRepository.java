package com.indra.chat.repository;

import com.indra.chat.entity.GroupEntity;
import com.indra.chat.entity.GroupRoleKey;
import com.indra.chat.entity.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupUserJoinRepository extends JpaRepository<GroupUser, GroupRoleKey> {

	@Query("SELECT gu FROM GroupUser gu WHERE gu.groupId = :groupId")
	List<GroupUser> getAllByGroupId(@Param("groupId") GroupEntity groupId);


    @Query(value = "SELECT g.user_id FROM group_user g WHERE g.group_id = :groupId", nativeQuery = true)
    List<Integer> getUsersIdInGroup(@Param("groupId") int groupId);
    
    List<GroupUser> getAllByGroupId(int groupId);
    
}

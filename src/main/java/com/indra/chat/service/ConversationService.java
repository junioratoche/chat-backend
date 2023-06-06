package com.indra.chat.service;

import com.indra.chat.dto.GroupMemberDTO;
import com.indra.chat.dto.user.UserDTO;
import com.indra.chat.entity.*;
import com.indra.chat.repository.ConversationRepository;
import com.indra.chat.repository.GroupRepository;
import com.indra.chat.repository.UserRepository;
import com.indra.chat.utils.GroupTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationService.class);

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupUserJoinService groupUserJoinService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    public List<UserDTO> fetchAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private UserDTO convertToDto(UserEntity user) {
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        // Establece otros campos según sea necesario
        return userDto;
    }   
    
    

    public GroupEntity findGroupByUrl(String url) {
        return groupRepository.findGroupByUrl(url);
    }

    public List<Integer> getAllUsersIdByGroupUrl(String groupUrl) {
        GroupEntity groupId = groupRepository.findGroupByUrl(groupUrl);
        List<GroupUser> users = groupUserJoinService.findAllByGroupId(groupId.getId());

        return users.stream().map(GroupUser::getUserId).collect(Collectors.toList());
    }

    public String getGroupName(String url) {
        return groupRepository.getGroupEntitiesBy(url);
    }

    public String getGroupUrlById(int id) {
        return groupRepository.getGroupUrlById(id);
    }

    public GroupMemberDTO addUserToConversation(int userId, int groupId) {
        Optional<GroupEntity> groupEntity = groupRepository.findById(groupId);
        if (groupEntity.isPresent() && groupEntity.orElse(null).getGroupTypeEnum().equals(GroupTypeEnum.SINGLE)) {
            log.info("Cannot add user in a single conversation");
            return new GroupMemberDTO();
        }
        UserEntity user = userService.findById(userId);
        GroupUser groupUser = new GroupUser();
        groupUser.setGroupMapping(groupEntity.orElse(null));
        groupUser.setUserMapping(user);
        groupUser.setGroupId(groupId);
        groupUser.setUserId(userId);
        groupUser.setRole(0);
        GroupUser saved = groupUserJoinService.save(groupUser);
        assert groupEntity.orElse(null) != null;
        groupEntity.orElse(null).getGroupUsers().add(saved);
        groupRepository.save(groupEntity.orElse(null));
        return new GroupMemberDTO(user.getId(), user.getFirstName(), user.getLastName(), false);
    }

    public GroupEntity createGroup(int userId, String name) {
        GroupUser groupUser = new GroupUser();
        GroupEntity group = new GroupEntity(name);
        group.setName(name);
        group.setUrl(UUID.randomUUID().toString());
        group.setGroupTypeEnum(GroupTypeEnum.GROUP);
        GroupEntity savedGroup = groupRepository.save(group);
        UserEntity user = userService.findById(userId);
        GroupRoleKey groupRoleKey = new GroupRoleKey();
        groupRoleKey.setUserId(userId);
        groupRoleKey.setGroupId(savedGroup.getId());
        groupUser.setGroupId(savedGroup.getId());
        groupUser.setUserId(userId);
        groupUser.setRole(1);
        groupUser.setUserMapping(user);
        groupUser.setGroupMapping(group);
        groupUserJoinService.save(groupUser);
        return savedGroup;
    }

    public Optional<GroupEntity> findById(int groupId) {
        return groupRepository.findById(groupId);
    }
    
    
    public ConversationEntity createConversation(int user1_id, int user2_id) {
        UserEntity user1 = userService.findById(user1_id);
        UserEntity user2 = userService.findById(user2_id);

        String chatIdentifier = UUID.randomUUID().toString();
        ConversationEntity conversationEntity = new ConversationEntity();
        conversationEntity.setUser1(user1);
        conversationEntity.setUser2(user2);
        conversationEntity.setChatIdentifier(chatIdentifier);

        return conversationRepository.save(conversationEntity);
    }
}

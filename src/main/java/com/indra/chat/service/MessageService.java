package com.indra.chat.service;

import com.indra.chat.dto.MessageDTO;
import com.indra.chat.dto.NotificationDTO;
import com.indra.chat.entity.ConversationEntity;
import com.indra.chat.entity.FileEntity;
import com.indra.chat.entity.GroupEntity;
import com.indra.chat.entity.MessageEntity;
import com.indra.chat.repository.ConversationRepository;
import com.indra.chat.repository.GroupRepository;
import com.indra.chat.repository.MessageRepository;
import com.indra.chat.utils.MessageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private FileService fileService;
    
    @Autowired
    private GroupRepository groupRepository; // Inyecta GroupRepository aquí
    
    @Autowired
    private ConversationRepository conversationRepository; // Inyecta ConversationRepository aquí

    private static final String[] colorsArray =
            {
                    "#FFC194", "#9CE03F", "#62C555", "#3AD079",
                    "#44CEC3", "#F772EE", "#FFAFD2", "#FFB4AF",
                    "#FF9207", "#E3D530", "#D2FFAF", "FF5733"
            };

    private static final Map<Integer, String> colors = new HashMap<>();

    public String getRandomColor() {
        return colorsArray[new Random().nextInt(colorsArray.length)];
    }

    public MessageEntity createAndSaveMessage(int userId, int chatId, boolean isGroupChat, String type, String data) {
        GroupEntity group = null;
        ConversationEntity conversation = null;

        if (isGroupChat) {
            group = groupRepository.findById(chatId).orElse(null);
        } else {
            conversation = conversationRepository.findById(chatId).orElse(null);
        }

        MessageEntity messageEntity = new MessageEntity(userId, group, conversation, type, data);
        return messageRepository.save(messageEntity);
    }



    public void flush() {
        messageRepository.flush();
    }

    public MessageEntity save(MessageEntity messageEntity) {
        return messageRepository.save(messageEntity);
    }

    public List<MessageEntity> findByGroupId(int id, int offset) {
        List<MessageEntity> list = messageRepository.findByGroupIdAndOffset(id, offset);
        if (list.size() == 0) {
            return new ArrayList<>();
        }
        return list;
    }

    public void deleteAllMessagesByGroupId(int groupId) {
        messageRepository.deleteMessagesDataByGroupId(groupId);
    }

    public MessageEntity findLastMessage(int groupId) {
        return messageRepository.findLastMessageByGroupId(groupId);
    }

    public int findLastMessageIdByGroupId(int groupId) {
        return messageRepository.findLastMessageIdByGroupId(groupId);
    }

    /**
     * Create a MessageDTO
     * Sent with user's initials
     *
     * @param id       of the message saved in DB
     * @param userId   int value for user ID
     * @param date     String of message sending date
     * @param group_id int value for group ID
     * @param message  string for the message content
     * @return a {@link MessageDTO}
     */
    public MessageDTO createMessageDTO(int id, String type, int userId, String date, int group_id, String message) {
        colors.putIfAbsent(userId, getRandomColor());
        String username = userService.findUsernameById(userId);
        String fileUrl = "";
        String[] arr = username.split(",");
        String initials = arr[0].substring(0, 1).toUpperCase() + arr[1].substring(0, 1).toUpperCase();
        String sender = StringUtils.capitalize(arr[0]) +
                " " +
                StringUtils.capitalize(arr[1]);
        if (type.equals(MessageTypeEnum.FILE.toString())) {
            FileEntity fileEntity = fileService.findByFkMessageId(id);
            fileUrl = fileEntity.getUrl();
        }
        return new MessageDTO(id, type, message, userId, group_id, null, sender, date, initials, colors.get(userId), fileUrl, userId == id);
    }

    public static String createUserInitials(String firstAndLastName) {
        String[] names = firstAndLastName.split(",");
        return names[0].substring(0, 1).toUpperCase() + names[1].substring(0, 1).toUpperCase();
    }

    @Transactional
    public List<Integer> createNotificationList(int userId, String groupUrl) {
        GroupEntity groupId = groupService.findGroupByUrl(groupUrl);
        List<Integer> toSend = new ArrayList<>();
        Optional<GroupEntity> optionalGroupEntity = groupService.findById(groupId.getId());

        if (optionalGroupEntity.isPresent()) {
            GroupEntity groupEntity = optionalGroupEntity.get();
            groupEntity.getUserEntities().forEach(userEntity -> toSend.add(userEntity.getId()));
        }
        return toSend;
    }

    public NotificationDTO createNotificationDTO(MessageEntity msg) {
    	int groupId = msg.getGroup().getId();
        String groupUrl = groupService.getGroupUrlById(groupId);
        NotificationDTO notificationDTO = new NotificationDTO();
//        notificationDTO.setGroupId(msg.getGroup_id());
        notificationDTO.setGroupId(groupId);
        notificationDTO.setGroupUrl(groupUrl);
        if (msg.getType().equals(MessageTypeEnum.TEXT.toString())) {
            notificationDTO.setType(MessageTypeEnum.TEXT);
            notificationDTO.setMessage(msg.getMessage());
        }
        if (msg.getType().equals(MessageTypeEnum.FILE.toString())) {
            FileEntity fileEntity = fileService.findByFkMessageId(msg.getId());
            notificationDTO.setType(MessageTypeEnum.FILE);
            notificationDTO.setMessage(msg.getMessage());
            notificationDTO.setFileUrl(fileEntity.getUrl());
            notificationDTO.setFileName(fileEntity.getFilename());
        }
        notificationDTO.setFromUserId(msg.getUser_id());
        notificationDTO.setLastMessageDate(msg.getCreatedAt().toString());
        notificationDTO.setSenderName(userService.findFirstNameById(msg.getUser_id()));
        notificationDTO.setMessageSeen(false);
        return notificationDTO;
    }

    public MessageDTO createNotificationMessageDTO(MessageEntity msg, int userId) {
    	int groupId = msg.getGroup().getId();
        String groupUrl = groupService.getGroupUrlById(groupId);
        String firstName = userService.findFirstNameById(msg.getUser_id());
        String initials = userService.findUsernameById(msg.getUser_id());
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(msg.getId());
        if (msg.getType().equals(MessageTypeEnum.FILE.toString())) {
            String url = fileService.findFileUrlByMessageId(msg.getId());
            messageDTO.setFileUrl(url);
        }
        messageDTO.setType(msg.getType());
        messageDTO.setMessage(msg.getMessage());
        messageDTO.setUserId(msg.getUser_id());
        messageDTO.setGroupUrl(groupUrl);
        messageDTO.setGroupId(groupId);
        messageDTO.setSender(firstName);
        messageDTO.setTime(msg.getCreatedAt().toString());
        messageDTO.setInitials(createUserInitials(initials));
        messageDTO.setColor(colors.get(msg.getUser_id()));
        messageDTO.setMessageSeen(msg.getUser_id() == userId);
        return messageDTO;
    }
    
    public int findIndividualConversationId(String chatIdentifier) {
        // Implementa la lógica para encontrar el ID de una conversación individual por su identificador
        // (por ejemplo, el ID del otro usuario en el chat)
        // Asume que existe una relación entre chatIdentifier y conversationId
        int conversationId = conversationRepository.findConversationIdByChatIdentifier(chatIdentifier);
        return conversationId;
    }

    public int getOtherUserIdInIndividualChat(int conversationId, int currentUserId) {
        // Implementa la lógica para obtener el ID del otro usuario en un chat individual
        // Asume que existe un método en el repositorio que devuelve los IDs de los usuarios en un chat individual
        List<Integer> userIds = conversationRepository.findUserIdsInConversation(conversationId);
        int otherUserId = -1;
        for (int userId : userIds) {
            if (userId != currentUserId) {
                otherUserId = userId;
                break;
            }
        }
        return otherUserId;
    }
}

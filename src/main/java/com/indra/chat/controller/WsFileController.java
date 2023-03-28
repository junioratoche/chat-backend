package com.indra.chat.controller;

import com.indra.chat.dto.MessageDTO;
import com.indra.chat.dto.OutputTransportDTO;
import com.indra.chat.entity.MessageEntity;
import com.indra.chat.service.GroupService;
import com.indra.chat.service.MessageService;
import com.indra.chat.service.StorageService;
import com.indra.chat.service.UserSeenMessageService;
import com.indra.chat.utils.MessageTypeEnum;
import com.indra.chat.utils.TransportActionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * API controller to handle file upload
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class WsFileController {

    private static Logger log = LoggerFactory.getLogger(WsFileController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserSeenMessageService seenMessageService;

    /**
     * Receive file to put in DB and send it back to the group conversation
     *
     * @param file     The file to be uploaded
     * @param userId   int value for user ID sender of the message
     * @param groupUrl string value for the group URL
     * @return a {@link ResponseEntity} with HTTP code
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam(name = "file") MultipartFile file, @RequestParam(name = "userId") int userId, @RequestParam(name = "groupUrl") String groupUrl) {
        int groupId = groupService.findGroupByUrl(groupUrl);
        try {
            MessageEntity messageEntity = messageService.createAndSaveMessage(userId, groupId, MessageTypeEnum.FILE.toString(), "have send a file");
            storageService.store(file, messageEntity.getId());
            OutputTransportDTO res = new OutputTransportDTO();
            MessageDTO messageDTO = messageService.createNotificationMessageDTO(messageEntity, userId);
            res.setAction(TransportActionEnum.NOTIFICATION_MESSAGE);
            res.setObject(messageDTO);
            seenMessageService.saveMessageNotSeen(messageEntity, groupId);
            List<Integer> toSend = messageService.createNotificationList(userId, groupUrl);
            toSend.forEach(toUserId -> messagingTemplate.convertAndSend("/topic/user/" + toUserId, res));
        } catch (Exception e) {
            log.error("Cannot save file, caused by {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }
}

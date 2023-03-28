package com.indra.chat.mapper;

import com.indra.chat.dto.user.GroupCallDTO;
import com.indra.chat.entity.GroupEntity;
import com.indra.chat.service.RoomCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupCallMapper {

    @Autowired
    private RoomCacheService roomCacheService;

    public GroupCallDTO toGroupCall(GroupEntity group) {
        List<String> keys = roomCacheService.getAllKeys();
        GroupCallDTO groupCallDTO = new GroupCallDTO();
        Optional<String> actualRoomKey =
                keys.stream().filter((key) -> {
                    String[] roomKey = key.split("_");
                    return group.getUrl().equals(roomKey[0]);
                }).findFirst();
        if (actualRoomKey.isPresent()) {
            groupCallDTO.setAnyCallActive(true);
            groupCallDTO.setActiveCallUrl(actualRoomKey.get().split("_")[1]);
        } else {
            groupCallDTO.setAnyCallActive(false);
        }
        return groupCallDTO;
    }
}

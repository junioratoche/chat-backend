package com.indra.chat.controller;

import com.google.gson.Gson;
import com.indra.chat.dto.*;
import com.indra.chat.dto.user.GroupDTO;
import com.indra.chat.dto.user.InitUserDTO;
import com.indra.chat.entity.ConversationEntity;
import com.indra.chat.entity.GroupEntity;
import com.indra.chat.entity.UserEntity;
import com.indra.chat.mapper.ConversationMapper;
import com.indra.chat.mapper.GroupMapper;
import com.indra.chat.mapper.UserMapper;
import com.indra.chat.service.ConversationService;
import com.indra.chat.service.CustomUserDetailsService;
import com.indra.chat.service.GroupService;
import com.indra.chat.service.UserService;
import com.indra.chat.utils.JwtUtil;
import com.indra.chat.utils.StaticVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMapper groupMapper;
    
    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private ConversationService conversationService;

    @PostMapping(value = "/auth")
    public AuthUserDTO createAuthenticationToken(@RequestBody JwtDTO authenticationRequest, HttpServletResponse response) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        UserEntity user = userService.findByNameOrEmail(authenticationRequest.getUsername(), authenticationRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        Cookie jwtAuthToken = new Cookie(StaticVariable.SECURE_COOKIE, token);
        jwtAuthToken.setHttpOnly(true);
        jwtAuthToken.setSecure(false);
        jwtAuthToken.setPath("/");
//        cookie.setDomain("http://localhost");
//         7 days
        jwtAuthToken.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(jwtAuthToken);
        return userMapper.toLightUserDTO(user);
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<?> fetchInformation(HttpServletResponse response) {
        Cookie cookie = new Cookie(StaticVariable.SECURE_COOKIE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/fetch")
    public InitUserDTO fetchInformation(HttpServletRequest request) {
        InitUserDTO initUserDTO = new InitUserDTO();
        System.out.println("escribiendo fetch="+ request);
        if (request != null) {
            return userMapper.toUserDTO(getUserEntity(request));
        }else{
            return initUserDTO;
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping(value = "/create")
    public GroupDTO createGroupChat(HttpServletRequest request, @RequestBody String payload) {
        UserEntity user = getUserEntity(request);
        Gson gson = new Gson();
        GroupDTO groupDTO = gson.fromJson(payload, GroupDTO.class);
        GroupEntity groupEntity = groupService.createGroup(user.getId(), groupDTO.getName());
        return groupMapper.toGroupDTO(groupEntity, user.getId());
    }
    
    @PostMapping(value = "/create/conversation/{user1_id}/{user2_id}")
    public ConversationDTO createConversationChat(@PathVariable int user1_id, @PathVariable int user2_id) {
        ConversationEntity conversationEntity = conversationService.createConversation(user1_id, user2_id);
        return conversationMapper.toConversationDTO(conversationEntity);
    }



    private UserEntity getUserEntity(HttpServletRequest request) {
        String username;
        String jwtToken;
        UserEntity user = new UserEntity();
        Cookie cookie = WebUtils.getCookie(request, StaticVariable.SECURE_COOKIE);
        if (cookie != null) {
            jwtToken = cookie.getValue();
            username = jwtTokenUtil.getUserNameFromJwtToken(jwtToken);
            user = userService.findByNameOrEmail(username, username);
        }
        return user;
    }
}
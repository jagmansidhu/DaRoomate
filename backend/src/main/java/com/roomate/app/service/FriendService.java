package com.roomate.app.service;

import com.auth0.jwt.JWT;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.entities.chatEntities.MessageEntity;
import com.roomate.app.entities.friendEntity.FriendEntity;

import java.util.List;
import java.util.Optional;

public interface FriendService {
    FriendEntity sendFriendRequest(String authId, String friendEmail);
    void acceptFriendRequest(Long requestId, String authId);
    void rejectFriendRequest(Long requestId, String authId);
    List<FriendEntity> getPendingFriendRequests(String authId);
    List<UserEntity> getFriends(String authId);
    void removeFriend(String authId, String friendEmail);
    boolean areFriends(String authId, String friendEmail);
}

package com.roomate.app.service.implementation;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.entities.friendEntity.FriendEntity;
import com.roomate.app.entities.friendEntity.FriendEnum;
import com.roomate.app.repository.FriendRepository;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.FriendService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendServiceImplt implements FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public FriendServiceImplt(UserRepository userRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    @Override
    public FriendEntity sendFriendRequest(String authId, String friendEmail) {
        UserEntity sender = getSender(authId);
        UserEntity receiver = userRepository.findByEmail(friendEmail);

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        Optional<FriendEntity> existingRequest = friendRepository
                .findByRequesterAndAddressee(sender.getId(), receiver.getId());

        if (existingRequest.isPresent()) {
            FriendEntity existing = existingRequest.get();
            if (existing.getStatus() == FriendEnum.PENDING) {
                throw new IllegalStateException("Friend request already pending");
            } else if (existing.getStatus() == FriendEnum.ACCEPTED) {
                throw new IllegalStateException("Already friends");
            } else if (existing.getStatus() == FriendEnum.REJECTED) {
                existing.setStatus(FriendEnum.PENDING);
                existing.setRequestDate(java.time.LocalDateTime.now());
                return friendRepository.save(existing);
            }
        }

        Optional<FriendEntity> reverseRequest = friendRepository
                .findByRequesterAndAddressee(receiver.getId(), sender.getId());

        System.out.println(reverseRequest.isPresent());
        if (reverseRequest.isPresent()) {
            FriendEntity reverse = reverseRequest.get();
            if (reverse.getStatus() == FriendEnum.PENDING) {
                reverse.accept();
                return friendRepository.save(reverse);
            } else if (reverse.getStatus() == FriendEnum.ACCEPTED) {
                throw new IllegalStateException("Already friends");
            }
        }

        FriendEntity friendRequest = new FriendEntity(sender, receiver, FriendEnum.PENDING);
        System.out.println(friendRequest.getAddressee() + "BESNA");
        return friendRepository.save(friendRequest);
    }

    @Override
    public void acceptFriendRequest(Long requestId, String authId) {
        UserEntity user = getSender(authId);

        FriendEntity request = getFriendRequestRequest(requestId);

        if (!request.getAddressee().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only the addressee can accept the request");
        }

        if (request.getStatus() != FriendEnum.PENDING) {
            throw new IllegalStateException("Request is not in pending status");
        }

        request.accept();
        friendRepository.save(request);
    }

    @Override
    public void rejectFriendRequest(Long requestId, String authId) {
        UserEntity user = getSender(authId);

        FriendEntity request = getFriendRequestRequest(requestId);

        if (!request.getAddressee().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only the addressee can accept the request");
        }

        if (request.getStatus() != FriendEnum.PENDING) {
            throw new IllegalStateException("Request is not in rejecting status");
        }

        request.decline();
        friendRepository.save(request);

    }

    @Override
    @Transactional
    public List<FriendEntity> getPendingFriendRequests(String authId) {
        UserEntity user = getSender(authId);

        return friendRepository.findByUserIdAndStatus(user.getId(), FriendEnum.PENDING)
                .stream()
                .filter(request -> request.getAddressee().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserEntity> getFriends(String authId) {
        UserEntity user = getSender(authId);

        List<FriendEntity> friendships = friendRepository.findByUserIdAndStatus(user.getId(), FriendEnum.ACCEPTED);

        return friendships.stream()
                .map(friendship -> {
                    if (friendship.getRequester().getId().equals(user.getId())) {
                        return friendship.getAddressee();
                    } else {
                        return friendship.getRequester();
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public void removeFriend(String authId, String friendEmail) {
        UserEntity user = getSender(authId);
        UserEntity friendRem = userRepository.findByEmail(friendEmail);

        Optional<FriendEntity> friendship = friendRepository.findByRequesterAndAddressee(user.getId(), friendRem.getId());

        if (friendship.isEmpty()) {
            FriendEntity friend = friendship.get();

            friendship = friendRepository
                    .findByRequesterAndAddressee(friend.getId(), user.getId());
        }

        if (friendship.get().getStatus() != FriendEnum.ACCEPTED) {
            throw new IllegalStateException("Not currently friends");
        }

        friendRepository.delete(friendship.get());
    }

    @Override
    @Transactional
    public boolean areFriends(String authId, String friendEmail) {
        try {
            UserEntity user = getSender(authId);

            UserEntity friend = userRepository.findByEmail(friendEmail);

            return friendRepository.areFriends(user.getId(), friend.getId());
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    private UserEntity getSender(String authId) {
        return userRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for Auth0 ID: " + authId));
    }

    private FriendEntity getFriendRequestRequest(Long requestId) {
        return friendRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found"));
    }
}

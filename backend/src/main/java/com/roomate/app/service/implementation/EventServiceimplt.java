package com.roomate.app.service.implementation;

import com.roomate.app.dto.EventDto;
import com.roomate.app.dto.RoomDto;
import com.roomate.app.dto.RoomMemberDto;
import com.roomate.app.dto.UserDto;
import com.roomate.app.entities.EventEntity;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.entities.room.RoomEntity;
import com.roomate.app.entities.room.RoomMemberEntity;
import com.roomate.app.repository.EventRepository;
import com.roomate.app.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequestMapping("/api/event")
public class EventServiceimplt implements EventService {
    EventRepository eventRepository;

    public EventServiceimplt(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    @GetMapping("/allEvents")
    public List<EventDto> getAllEventsForUser(String authId) {
        List<EventEntity> events = eventRepository.getAllEventsForUser(authId);

        return events.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private EventDto convertToDto(EventEntity eventEntity) {
        EventDto eventDto = new EventDto();
        eventDto.setId(eventEntity.getId());
        eventDto.setTitle(eventEntity.getTitle());
        eventDto.setDescription(eventEntity.getDescription());
        eventDto.setStartTime(eventEntity.getStartTime());
        eventDto.setEndTime(eventEntity.getEndTime());

        RoomEntity roomEntity = eventEntity.getRoom();
        RoomDto roomDto = new RoomDto(roomEntity.getRoomCode());
        eventDto.setRooms(roomDto);

        UserEntity userEntity = eventEntity.getUser();
        UserDto userDto = new UserDto(userEntity.getAuthId());
        eventDto.setUser(userDto);

        return eventDto;
    }

    @Override
    public List<EventDto> getEventsForUser(UUID roomID, String authId) {
        return List.of();
    }

    @Override
    public EventDto createEvent(EventDto eventDto, String authId) {
        return null;
    }

    @Override
    public EventDto updateEvent(UUID eventID, String authId) {
        return null;
    }

    @Override
    public void deleteEvent(UUID eventId, String authId) {

    }
}

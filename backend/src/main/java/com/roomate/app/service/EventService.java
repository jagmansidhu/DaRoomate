package com.roomate.app.service;

import com.roomate.app.dto.EventDto;

import java.util.List;
import java.util.UUID;

public interface EventService {
    List<EventDto> getAllEventsForUser(String authId);
    List<EventDto> getEventsForUserRoom(UUID roomID, String authId);
    void createEventForRoom(EventDto eventDto, UUID roomid, String authId);
    void updateEvent(EventDto eventDto, UUID eventID, String authId);
    void deleteEvent(UUID eventId, String authId);
}

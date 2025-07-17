package com.roomate.app.service;

import com.roomate.app.dto.EventDto;

import java.util.List;
import java.util.UUID;

public interface EventService {
    List<EventDto> getAllEventsForUser(String authId);
    List<EventDto> getEventsForUser(UUID roomID, String authId);
    EventDto createEvent(EventDto eventDto, String authId);
    EventDto updateEvent(UUID eventID, String authId);
    void deleteEvent(UUID eventId, String authId);
}

package com.roomate.app.controller;

import com.roomate.app.dto.EventDto;
import com.roomate.app.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @GetMapping("/user")
    public ResponseEntity<List<EventDto>> getAllEventsForUser(@AuthenticationPrincipal Jwt jwt) {
        String authId = jwt.getSubject();
        List<EventDto> events = eventService.getAllEventsForUser(authId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/user/own")
    public ResponseEntity<List<EventDto>> getUserOwnEvents(@AuthenticationPrincipal Jwt jwt) {
        String authId = jwt.getSubject();
        List<EventDto> events = eventService.getAllEventsForUser(authId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<EventDto>> getEventsForRoom(@PathVariable UUID roomId, @AuthenticationPrincipal Jwt jwt) {
        String authId = jwt.getSubject();
        List<EventDto> events = eventService.getEventsForUserRoom(roomId, authId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/room/{roomId}")
    public ResponseEntity<Void> createEvent(@PathVariable UUID roomId, @RequestBody @Valid EventDto eventDto, @AuthenticationPrincipal Jwt jwt) {
        String authId = jwt.getSubject();
        eventService.createEventForRoom(eventDto, roomId, authId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Void> updateEvent(@PathVariable UUID eventId, @RequestBody EventDto eventDto, @AuthenticationPrincipal Jwt jwt) {
        String authId = jwt.getSubject();
        eventService.updateEvent(eventDto, eventId, authId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId, @AuthenticationPrincipal Jwt jwt) {
        String authId = jwt.getSubject();
        eventService.deleteEvent(eventId, authId);
        return ResponseEntity.ok().build();
    }
}

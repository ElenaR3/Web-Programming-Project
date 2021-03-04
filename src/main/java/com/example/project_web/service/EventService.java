package com.example.project_web.service;

import com.example.project_web.model.CalendarEvent;
import com.example.project_web.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EventService implements EventServiceInterface{

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Iterable<CalendarEvent> findBetween(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findBetween(start, end);
    }

    @Override
    public CalendarEvent create(LocalDateTime start, LocalDateTime end, String text) {
        return this.eventRepository.save(new CalendarEvent(text, start, end));
    }

    @Override
    public CalendarEvent changeDate(Long id, LocalDateTime start, LocalDateTime end) {
        return null;
    }

    @Override
    public CalendarEvent putColor(Long id, String color) {
        Optional<CalendarEvent> calendarEvent = this.eventRepository.findById(id);
        CalendarEvent cal = calendarEvent.get();
        cal.setColor(color);
        return this.eventRepository.save(cal);
    }
}

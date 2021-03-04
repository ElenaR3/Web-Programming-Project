package com.example.project_web.service;

import com.example.project_web.model.CalendarEvent;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

public interface EventServiceInterface {

    Iterable<CalendarEvent> findBetween(LocalDateTime start, LocalDateTime end);
    CalendarEvent create(LocalDateTime start, LocalDateTime end, String text);
    CalendarEvent changeDate(Long id, LocalDateTime start, LocalDateTime end);
    CalendarEvent putColor(Long id, String color);


}

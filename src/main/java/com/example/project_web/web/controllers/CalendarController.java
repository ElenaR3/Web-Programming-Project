package com.example.project_web.web.controllers;


import com.example.project_web.model.CalendarEvent;
import com.example.project_web.repository.EventRepository;
import com.example.project_web.service.EventService;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RestController
public class CalendarController {

    private final EventRepository eventRepository;
    private final EventService eventService;


    public CalendarController(EventRepository er, EventService eventService) {
        this.eventRepository = er;
        this.eventService = eventService;
    }


    @RequestMapping("/events")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("bodyContent","calendar.html");
        modelAndView.setViewName("master-template");
        return modelAndView;
    }

    @GetMapping("/api/events")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    Iterable<CalendarEvent> events(@RequestParam("start")
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                   @RequestParam("end")
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return eventService.findBetween(start, end);
    }

    @PostMapping("/api/events/create")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Transactional
    CalendarEvent createEvent(@RequestBody EventCreateParams params) {
        return eventService.create(params.start,params.end, params.text);
    }

    @PostMapping("/api/events/move")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Transactional
    CalendarEvent moveEvent(@RequestBody EventMoveParams params) {
        return eventService.changeDate(params.id,params.start,params.end);
    }

    @PostMapping("/api/events/setColor")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Transactional
    CalendarEvent setColor(@RequestBody SetColorParams params) {
        return eventService.putColor(params.id,params.color);
    }

    public static class EventCreateParams {
        public LocalDateTime start;
        public LocalDateTime end;
        public String text;
        public Long resource;
    }

    public static class EventMoveParams {
        public Long id;
        public LocalDateTime start;
        public LocalDateTime end;
        public Long resource;
    }

    public static class SetColorParams {
        public Long id;
        public String color;
    }

}


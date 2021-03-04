package com.example.project_web.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="CalendarEvent")
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private LocalDateTime start;

    @Column(name = "finish")
    private LocalDateTime end;
    private String color;

    public CalendarEvent(Long id, String text, LocalDateTime start, LocalDateTime end, String color) {
        this.id = id;
        this.text = text;
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public CalendarEvent(String text, LocalDateTime start, LocalDateTime end) {
        this.text = text;
        this.start = start;
        this.end = end;
    }

    public CalendarEvent() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
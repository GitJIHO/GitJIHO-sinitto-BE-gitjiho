package com.example.sinitto.helloCall.entity;

import com.example.sinitto.member.entity.Sinitto;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class HelloCallTimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startDateAndTime;
    private LocalDateTime endDateAndTime;
    @ManyToOne
    @JoinColumn(name = "helloCall_id")
    private HelloCall helloCall;
    @OneToOne
    @JoinColumn(name = "sinitto_id")
    private Sinitto sinitto;

    public HelloCallTimeLog(HelloCall helloCall, Sinitto sinitto) {
        this.helloCall = helloCall;
        this.sinitto = sinitto;
    }

    protected HelloCallTimeLog() {
    }

    public void setStartDateAndTime(LocalDateTime startDateAndTime) {
        this.startDateAndTime = startDateAndTime;
    }

    public void setEndDateAndTime(LocalDateTime endDateAndTime) {
        this.endDateAndTime = endDateAndTime;
    }

    public LocalDateTime getStartDateAndTime() {
        return startDateAndTime;
    }

    public LocalDateTime getEndDateAndTime() {
        return endDateAndTime;
    }

    public String getSinittoName() {
        return this.sinitto.getMember().getName();
    }
}

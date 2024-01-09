package com.samj.shared;

import java.time.LocalDateTime;

public class CallForwardingDTO {

    private String calledNumber;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String destinationNumber;

    // Constructor, getters, and setters
    public CallForwardingDTO(String calledNumber, LocalDateTime beginTime, LocalDateTime endTime, String destinationNumber) {
        this.calledNumber = calledNumber;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.destinationNumber = destinationNumber;
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDestinationNumber() {
        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

}
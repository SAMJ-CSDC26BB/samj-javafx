package com.samj.shared;

import java.time.LocalDateTime;

/**
 * Class used as a Data Transfer Object for CallForwarding records from the database.
 * It contains only some private instance variables, getters and setters and constructors,
 * no additional logic should be added here.
 */
public class CallForwardingDTO {

    private int id;
    private String calledNumber;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String destinationNumber;
    private String destinationUsername;
    private String destinationUserFullName;

    public CallForwardingDTO(String calledNumber,
                             LocalDateTime beginTime,
                             LocalDateTime endTime,
                             String destinationNumber,
                             String destinationUsername,
                             String destinationUserFullName) {

        this.calledNumber = calledNumber;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.destinationNumber = destinationNumber;
        this.destinationUsername = destinationUsername;
        this.destinationUserFullName = destinationUserFullName;
    }

    public CallForwardingDTO(int id,
                             String calledNumber,
                             LocalDateTime beginTime,
                             LocalDateTime endTime,
                             String destinationNumber,
                             String destinationUsername,
                             String destinationUserFullName) {

        this(calledNumber, beginTime, endTime, destinationNumber, destinationUsername, destinationUserFullName);
        this.id = id;
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

    public int getId() {
        return id;
    }

    public String getDestinationUsername() {
        return destinationUsername;
    }

    public void setDestinationUsername(String destinationUsername) {
        this.destinationUsername = destinationUsername;
    }

    public String getDestinationUserFullName() {
        return destinationUserFullName;
    }

    public void setDestinationUserFullName(String destinationUserFullName) {
        this.destinationUserFullName = destinationUserFullName;
    }
}
package com.carrental.domain;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class Log {
    private @Id @GeneratedValue int id;
    private LocalDate date;
    private LocalTime time;
    private String ssn;
    private int carId;
    private String message;

    public Log(int id, Date date, Time time, String ssn, int carId, String message) {
        this.id = id;
        this.date = date.toLocalDate();
        this.time = time.toLocalTime();
        this.ssn = ssn;
        this.carId = carId;
        this.message = message;

    }

    public void info(String message){

    }
}

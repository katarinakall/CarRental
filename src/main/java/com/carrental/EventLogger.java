package com.carrental;

import com.carrental.domain.Car;
import com.carrental.domain.Customer;
import lombok.Data;
import org.joda.time.LocalTime;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
public class EventLogger {
    private @Id @GeneratedValue int id;
    private LocalDate date;
    private LocalTime time;
    private String message;
    private Customer customer;
    private Car car;


    public void info(String message){

    }
}

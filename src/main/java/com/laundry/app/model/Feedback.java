package com.laundry.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity @Table(name="feedback")
public class Feedback {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) private Account customer;
    @OneToOne(optional=false) private LaundryOrder laundryOrder;
    @Min(1) @Max(5) private int rating;
    @Size(max=1000) private String comment;
    private LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){return id;} public Account getCustomer(){return customer;} public void setCustomer(Account v){customer=v;}
    public LaundryOrder getLaundryOrder(){return laundryOrder;} public void setLaundryOrder(LaundryOrder v){laundryOrder=v;}
    public int getRating(){return rating;} public void setRating(int v){rating=v;}
    public String getComment(){return comment;} public void setComment(String v){comment=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}

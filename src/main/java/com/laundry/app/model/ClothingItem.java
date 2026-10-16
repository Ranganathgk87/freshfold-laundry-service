package com.laundry.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity @Table(name="clothing_items")
public class ClothingItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable=false, unique=true) private String name;
    @Column(nullable=false) private boolean active=true;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}

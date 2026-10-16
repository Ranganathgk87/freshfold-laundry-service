package com.laundry.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity @Table(name="laundry_services")
public class LaundryService {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable=false, unique=true) private String name;
    @Column(length=500) private String description;
    @NotNull @DecimalMin("0.01") @Column(nullable=false) private Double pricePerItem;
    @Column(nullable=false) private boolean active=true;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public Double getPricePerItem(){return pricePerItem;} public void setPricePerItem(Double v){pricePerItem=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}

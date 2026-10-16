package com.laundry.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity @Table(name="accounts")
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable=false) private String name;
    @Email @NotBlank @Column(nullable=false, unique=true) private String email;
    @NotBlank @Column(nullable=false) private String password;
    private String phone;
    @Column(length=500) private String address;
    @Column(nullable=false) private String role="CUSTOMER";
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v==null?null:v.trim().toLowerCase();}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getRole(){return role;} public void setRole(String v){role=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}

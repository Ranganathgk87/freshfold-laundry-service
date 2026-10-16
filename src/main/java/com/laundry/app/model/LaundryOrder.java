package com.laundry.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name="laundry_orders")
public class LaundryOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) private Account customer;
    @ManyToOne(optional=false) private LaundryService service;
    @ManyToOne(optional=false) private ClothingItem clothingItem;
    private int quantity;
    private double totalAmount;
    private LocalDate pickupDate;
    private String pickupSlot;
    @Column(length=500) private String pickupAddress;
    private LocalDate deliveryDate;
    @Column(nullable=false) private String status="PLACED";
    @Column(nullable=false) private String paymentStatus="UNPAID";
    @Column(length=1000) private String adminNote;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Account getCustomer(){return customer;} public void setCustomer(Account v){customer=v;}
    public LaundryService getService(){return service;} public void setService(LaundryService v){service=v;}
    public ClothingItem getClothingItem(){return clothingItem;} public void setClothingItem(ClothingItem v){clothingItem=v;}
    public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;}
    public double getTotalAmount(){return totalAmount;} public void setTotalAmount(double v){totalAmount=v;}
    public LocalDate getPickupDate(){return pickupDate;} public void setPickupDate(LocalDate v){pickupDate=v;}
    public String getPickupSlot(){return pickupSlot;} public void setPickupSlot(String v){pickupSlot=v;}
    public String getPickupAddress(){return pickupAddress;} public void setPickupAddress(String v){pickupAddress=v;}
    public LocalDate getDeliveryDate(){return deliveryDate;} public void setDeliveryDate(LocalDate v){deliveryDate=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(String v){paymentStatus=v;}
    public String getAdminNote(){return adminNote;} public void setAdminNote(String v){adminNote=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public static List<String> statuses(){return List.of("PLACED","PICKUP_SCHEDULED","PICKED_UP","WASHING","READY_FOR_DELIVERY","OUT_FOR_DELIVERY","COMPLETED","CANCELLED");}
}

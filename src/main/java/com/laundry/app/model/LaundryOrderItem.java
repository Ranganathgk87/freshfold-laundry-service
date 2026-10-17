package com.laundry.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="laundry_order_items")
public class LaundryOrderItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="order_id", nullable=false)
    private LaundryOrder laundryOrder;

    @ManyToOne(optional=false)
    private LaundryService service;

    @ManyToOne(optional=false)
    private ClothingItem clothingItem;

    @Column(nullable=false)
    private int quantity;

    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal unitPrice;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal lineTotal;

    public Long getId(){return id;}
    public LaundryOrder getLaundryOrder(){return laundryOrder;}
    public void setLaundryOrder(LaundryOrder value){laundryOrder=value;}
    public LaundryService getService(){return service;}
    public void setService(LaundryService value){service=value;}
    public ClothingItem getClothingItem(){return clothingItem;}
    public void setClothingItem(ClothingItem value){clothingItem=value;}
    public int getQuantity(){return quantity;}
    public void setQuantity(int value){quantity=value;}
    public BigDecimal getUnitPrice(){return unitPrice;}
    public void setUnitPrice(BigDecimal value){unitPrice=value;}
    public BigDecimal getLineTotal(){return lineTotal;}
    public void setLineTotal(BigDecimal value){lineTotal=value;}
}


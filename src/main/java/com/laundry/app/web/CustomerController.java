package com.laundry.app.web;

import com.laundry.app.model.*;
import com.laundry.app.repo.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Controller @RequestMapping("/customer")
public class CustomerController {
    private final AccountRepository accounts; private final LaundryServiceRepository services;
    private final ClothingItemRepository clothes; private final LaundryOrderRepository orders; private final FeedbackRepository feedback;
    public CustomerController(AccountRepository a,LaundryServiceRepository s,ClothingItemRepository c,LaundryOrderRepository o,FeedbackRepository f){accounts=a;services=s;clothes=c;orders=o;feedback=f;}
    @GetMapping String dashboard(Authentication auth,Model model){
        model.addAttribute("account",accounts.findByEmail(auth.getName()).orElseThrow());
        model.addAttribute("orders",orders.findByCustomerEmailOrderByCreatedAtDesc(auth.getName()));
        model.addAttribute("services",services.findByActiveTrueOrderByNameAsc());
        model.addAttribute("clothes",clothes.findByActiveTrueOrderByNameAsc());
        model.addAttribute("feedbackExists",feedback.findAll().stream().map(f->f.getLaundryOrder().getId()).collect(java.util.stream.Collectors.toSet()));
        return "customer";
    }
    @PostMapping("/profile") String updateProfile(Authentication auth,@RequestParam String name,@RequestParam(required=false) String phone,@RequestParam(required=false) String address,RedirectAttributes flash){
        Account account=accounts.findByEmail(auth.getName()).orElseThrow();
        if(name==null||name.isBlank()||name.trim().length()>100||phone!=null&&phone.length()>30||address!=null&&address.length()>500){flash.addFlashAttribute("error","Check your name, phone number and address.");return "redirect:/customer";}
        account.setName(name.trim());account.setPhone(phone==null?"":phone.trim());account.setAddress(address==null?"":address.trim());accounts.save(account);
        flash.addFlashAttribute("success","Your profile has been updated.");return "redirect:/customer";
    }
    @PostMapping("/orders")
    String place(Authentication auth,
            @RequestParam(name="serviceIds",required=false) List<Long> serviceIds,
            @RequestParam(name="clothingItemIds",required=false) List<Long> clothingItemIds,
            @RequestParam(name="quantities",required=false) List<Integer> quantities,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam String pickupSlot,@RequestParam String pickupAddress,RedirectAttributes flash){
        if(serviceIds==null||clothingItemIds==null||quantities==null||serviceIds.isEmpty()
                ||serviceIds.size()>20||serviceIds.size()!=clothingItemIds.size()||serviceIds.size()!=quantities.size()){
            flash.addFlashAttribute("error","Add between 1 and 20 complete clothing rows to your booking.");return "redirect:/customer#book";}
        if(pickupDate==null||pickupDate.isBefore(LocalDate.now(ZoneId.of("Asia/Kolkata")))) {
            flash.addFlashAttribute("error","Pickup date must be today or later.");return "redirect:/customer#book";}
        if(pickupAddress==null||pickupAddress.isBlank()||pickupAddress.length()>500){flash.addFlashAttribute("error","Enter a valid pickup address (up to 500 characters).");return "redirect:/customer#book";}
        if(!List.of("09:00-12:00","12:00-15:00","15:00-18:00").contains(pickupSlot)){
            flash.addFlashAttribute("error","Choose one of the available pickup windows.");return "redirect:/customer#book";}

        LaundryOrder order=new LaundryOrder();
        order.setCustomer(accounts.findByEmail(auth.getName()).orElseThrow());
        BigDecimal total=BigDecimal.ZERO;
        int totalQuantity=0;
        LaundryService firstService=null;
        ClothingItem firstClothingItem=null;
        for(int index=0;index<serviceIds.size();index++){
            Long serviceId=serviceIds.get(index);
            Long clothingItemId=clothingItemIds.get(index);
            Integer quantity=quantities.get(index);
            if(serviceId==null||clothingItemId==null||quantity==null||quantity<1||quantity>100){
                flash.addFlashAttribute("error","Choose a service and clothing type, and enter a quantity from 1 to 100 for every row.");return "redirect:/customer#book";}
            LaundryService service=services.findById(serviceId).filter(LaundryService::isActive).orElse(null);
            ClothingItem item=clothes.findById(clothingItemId).filter(ClothingItem::isActive).orElse(null);
            if(service==null||item==null||service.getPricePerItem()==null||service.getPricePerItem()<=0){
                flash.addFlashAttribute("error","One of the selected services or clothing types is no longer available. Please review your booking.");return "redirect:/customer#book";}
            BigDecimal unitPrice=BigDecimal.valueOf(service.getPricePerItem()).setScale(2,RoundingMode.HALF_UP);
            BigDecimal lineTotal=unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2,RoundingMode.HALF_UP);
            LaundryOrderItem line=new LaundryOrderItem();
            line.setService(service);line.setClothingItem(item);line.setQuantity(quantity);
            line.setUnitPrice(unitPrice);line.setLineTotal(lineTotal);order.addItem(line);
            total=total.add(lineTotal);totalQuantity+=quantity;
            if(firstService==null){firstService=service;firstClothingItem=item;}
        }
        // Keep the original order summary fields populated for existing reports and older orders.
        order.setService(firstService);order.setClothingItem(firstClothingItem);order.setQuantity(totalQuantity);
        order.setTotalAmount(total.doubleValue());
        order.setPickupDate(pickupDate);order.setPickupSlot(pickupSlot);order.setPickupAddress(pickupAddress.trim());
        order.setStatus("PICKUP_SCHEDULED");orders.save(order);flash.addFlashAttribute("success","Booking placed. Your order number is #"+order.getId()+".");
        return "redirect:/customer";
    }
    @PostMapping("/orders/{id}/cancel") String cancel(@PathVariable Long id,Authentication auth,RedirectAttributes flash){
        LaundryOrder o=orders.findById(id).filter(x->x.getCustomer().getEmail().equals(auth.getName())).orElse(null);
        if(o==null){flash.addFlashAttribute("error","Order not found.");return "redirect:/customer";}
        if(!java.util.List.of("PLACED","PICKUP_SCHEDULED").contains(o.getStatus())) flash.addFlashAttribute("error","This order can no longer be cancelled.");
        else {o.setStatus("CANCELLED");orders.save(o);flash.addFlashAttribute("success","Order cancelled.");}
        return "redirect:/customer";
    }
    @PostMapping("/orders/{id}/payment") String payment(@PathVariable Long id,Authentication auth,RedirectAttributes flash){
        LaundryOrder o=orders.findById(id).filter(x->x.getCustomer().getEmail().equals(auth.getName())).orElse(null);
        if(o==null){flash.addFlashAttribute("error","Order not found.");return "redirect:/customer";}
        if(!o.getStatus().equals("COMPLETED")) flash.addFlashAttribute("error","Payment can be recorded after the service is completed.");
        else {o.setPaymentStatus("PAID");orders.save(o);flash.addFlashAttribute("success","Payment recorded.");}
        return "redirect:/customer";
    }
    @PostMapping("/orders/{id}/feedback") String submitFeedback(@PathVariable Long id,Authentication auth,@RequestParam int rating,@RequestParam(required=false) String comment,RedirectAttributes flash){
        LaundryOrder o=orders.findById(id).filter(x->x.getCustomer().getEmail().equals(auth.getName())).orElse(null);
        if(o==null||!o.getStatus().equals("COMPLETED")||feedback.existsByLaundryOrderId(id)){flash.addFlashAttribute("error","Feedback is available once for completed orders.");return "redirect:/customer";}
        if(rating<1||rating>5||comment!=null&&comment.length()>1000){flash.addFlashAttribute("error","Choose a rating from 1 to 5 and keep feedback under 1000 characters.");return "redirect:/customer";}
        Feedback f=new Feedback();f.setCustomer(o.getCustomer());f.setLaundryOrder(o);f.setRating(rating);f.setComment(comment);feedback.save(f);flash.addFlashAttribute("success","Thank you for your feedback.");return "redirect:/customer";
    }
}


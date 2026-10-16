package com.laundry.app.web;

import com.laundry.app.model.*;
import com.laundry.app.repo.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

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
    String place(Authentication auth,@RequestParam Long serviceId,@RequestParam Long clothingItemId,
            @RequestParam @Min(1) @Max(100) Integer quantity,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam String pickupSlot,@RequestParam String pickupAddress,RedirectAttributes flash){
        if(pickupDate.isBefore(LocalDate.now())) {flash.addFlashAttribute("error","Pickup date must be today or later.");return "redirect:/customer#book";}
        if(pickupAddress.isBlank()||pickupAddress.length()>500){flash.addFlashAttribute("error","Enter a valid pickup address (up to 500 characters).");return "redirect:/customer#book";}
        LaundryService service=services.findById(serviceId).filter(LaundryService::isActive).orElse(null);
        ClothingItem item=clothes.findById(clothingItemId).filter(ClothingItem::isActive).orElse(null);
        if(service==null||item==null||quantity<1||quantity>100||!java.util.List.of("09:00-12:00","12:00-15:00","15:00-18:00").contains(pickupSlot)){
            flash.addFlashAttribute("error","Select a valid service, clothing item, quantity and pickup slot.");return "redirect:/customer#book";}
        LaundryOrder order=new LaundryOrder(); order.setCustomer(accounts.findByEmail(auth.getName()).orElseThrow());
        order.setService(service);order.setClothingItem(item);order.setQuantity(quantity);
        order.setTotalAmount(Math.round(service.getPricePerItem()*quantity*100.0)/100.0);
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

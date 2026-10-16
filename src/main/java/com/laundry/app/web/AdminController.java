package com.laundry.app.web;

import com.laundry.app.model.*;
import com.laundry.app.repo.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.charset.StandardCharsets;

@Controller @RequestMapping("/admin")
public class AdminController {
    private final AccountRepository accounts;private final LaundryServiceRepository services;private final ClothingItemRepository clothes;private final LaundryOrderRepository orders;private final FeedbackRepository feedback;
    public AdminController(AccountRepository a,LaundryServiceRepository s,ClothingItemRepository c,LaundryOrderRepository o,FeedbackRepository f){accounts=a;services=s;clothes=c;orders=o;feedback=f;}
    @GetMapping String dashboard(Model m){
        m.addAttribute("orders",orders.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,"createdAt")));
        m.addAttribute("services",services.findAll());m.addAttribute("clothes",clothes.findAll());m.addAttribute("customers",accounts.findAll().stream().filter(a->a.getRole().equals("CUSTOMER")).toList());
        m.addAttribute("feedback",feedback.findAllByOrderByCreatedAtDesc());m.addAttribute("statuses",LaundryOrder.statuses());m.addAttribute("serviceForm",new LaundryService());m.addAttribute("clothingForm",new ClothingItem());
        m.addAttribute("placed",orders.countByStatus("PLACED"));m.addAttribute("processing",orders.countByStatus("WASHING"));m.addAttribute("completed",orders.countByStatus("COMPLETED"));return "admin/dashboard";
    }
    @PostMapping("/services") String addService(@Valid @ModelAttribute("serviceForm") LaundryService s,BindingResult result,Model m,RedirectAttributes flash){
        if(result.hasErrors()){flash.addFlashAttribute("error","Service name and a price greater than zero are required.");return "redirect:/admin#services";}
        services.save(s);flash.addFlashAttribute("success","Service saved.");return "redirect:/admin#services";
    }
    @PostMapping("/services/{id}/toggle") String toggleService(@PathVariable Long id,RedirectAttributes f){services.findById(id).ifPresent(s->{s.setActive(!s.isActive());services.save(s);});f.addFlashAttribute("success","Service availability updated.");return "redirect:/admin#services";}
    @PostMapping("/services/{id}") String updateService(@PathVariable Long id,@RequestParam String name,@RequestParam(required=false) String description,@RequestParam double pricePerItem,RedirectAttributes f){
        LaundryService s=services.findById(id).orElse(null);
        if(s==null||name==null||name.isBlank()||name.trim().length()>100||pricePerItem<=0||!Double.isFinite(pricePerItem)||description!=null&&description.length()>500){f.addFlashAttribute("error","Enter a service name and valid price and description.");return "redirect:/admin#services";}
        s.setName(name.trim());s.setDescription(description==null?"":description.trim());s.setPricePerItem(Math.round(pricePerItem*100.0)/100.0);services.save(s);f.addFlashAttribute("success","Service and price updated.");return "redirect:/admin#services";
    }
    @PostMapping("/clothing") String addClothing(@RequestParam String name,RedirectAttributes f){
        String cleaned=name==null?"":name.trim();if(cleaned.isBlank()||cleaned.length()>80){f.addFlashAttribute("error","Enter a clothing name up to 80 characters.");return "redirect:/admin#services";}
        ClothingItem c=new ClothingItem();c.setName(cleaned);clothes.save(c);f.addFlashAttribute("success","Clothing item added.");return "redirect:/admin#services";
    }
    @PostMapping("/clothing/{id}/toggle") String toggleClothing(@PathVariable Long id,RedirectAttributes f){clothes.findById(id).ifPresent(c->{c.setActive(!c.isActive());clothes.save(c);});f.addFlashAttribute("success","Clothing availability updated.");return "redirect:/admin#services";}
    @PostMapping("/orders/{id}") String updateOrder(@PathVariable Long id,@RequestParam String status,@RequestParam(required=false) String paymentStatus,
            @RequestParam(required=false) String deliveryDate,@RequestParam(required=false) String adminNote,RedirectAttributes f){
        LaundryOrder o=orders.findById(id).orElse(null);
        if(o==null){f.addFlashAttribute("error","Order not found.");return "redirect:/admin#orders";}
        if(!LaundryOrder.statuses().contains(status)||paymentStatus!=null&&!java.util.List.of("UNPAID","PAID").contains(paymentStatus)){f.addFlashAttribute("error","Invalid order or payment status.");return "redirect:/admin#orders";}
        o.setStatus(status);if(paymentStatus!=null)o.setPaymentStatus(paymentStatus);
        try{if(deliveryDate!=null&&!deliveryDate.isBlank())o.setDeliveryDate(java.time.LocalDate.parse(deliveryDate));}catch(Exception e){f.addFlashAttribute("error","Delivery date is invalid.");return "redirect:/admin#orders";}
        o.setAdminNote(adminNote==null?"":adminNote.trim());orders.save(o);f.addFlashAttribute("success","Order updated.");return "redirect:/admin#orders";
    }
    @GetMapping(value="/report.csv",produces="text/csv")
    @ResponseBody ResponseEntity<byte[]> report(){
        StringBuilder b=new StringBuilder("Order,Customer,Email,Service,Clothing,Quantity,Amount,Status,Payment,Pickup date,Pickup slot,Delivery date,Created\n");
        for(LaundryOrder o:orders.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,"createdAt"))){
            b.append(o.getId()).append(',').append(csv(o.getCustomer().getName())).append(',').append(csv(o.getCustomer().getEmail())).append(',').append(csv(o.getService().getName())).append(',').append(csv(o.getClothingItem().getName())).append(',').append(o.getQuantity()).append(',').append(o.getTotalAmount()).append(',').append(o.getStatus()).append(',').append(o.getPaymentStatus()).append(',').append(o.getPickupDate()).append(',').append(csv(o.getPickupSlot())).append(',').append(o.getDeliveryDate()).append(',').append(o.getCreatedAt()).append('\n');
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=laundry-orders.csv").contentType(MediaType.parseMediaType("text/csv")).body(b.toString().getBytes(StandardCharsets.UTF_8));
    }
    private String csv(String v){return "\""+(v==null?"":v.replace("\"","\"\""))+"\"";}
}

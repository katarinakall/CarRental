package com.carrental;

import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import com.carrental.repository.CarRentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class CarRentalController {

    private CarRentalRepository repository;

    @Autowired
    public CarRentalController(CarRentalRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("startpage");
    }

    @GetMapping("/rentform")
    public String getRentForm(Model model) {
        model.addAttribute("rentalRequest", new RentalRequest());
        return "rentform";
    }

    @PostMapping("/rentform")
    public String rentFormSubmit(@ModelAttribute RentalRequest request) {
        System.out.println("***************" +request.getDateOfBirth() + " " + request.getLastFourDigits() + " ");
        return "cars";
    }

    @GetMapping("/returnform")
    public ModelAndView getReturnForm (){
        return new ModelAndView("returnform");
    }

    @GetMapping("/cars")
    public String getAvailableCars (){

       return "cars";
    }

}

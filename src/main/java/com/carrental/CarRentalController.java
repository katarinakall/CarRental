package com.carrental;

import com.carrental.domain.Car;
import com.carrental.repository.CarRentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.sql.SQLException;
import java.util.List;


@Controller
public class CarRentalController {

    @Autowired
    private CarRentalRepository repository;


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
    public ModelAndView rentFormSubmit(@ModelAttribute RentalRequest request) {
        List<Car> cars = repository.getAvailableCars(request.getCarType());
        repository.addBooking(request);
        repository.addCustomer(request);
        return new ModelAndView("cars")
                .addObject("cars", cars);
    }

    @RequestMapping(value="/selectcar", params = {"selectCar"})
    public String selectCar(HttpServletRequest request){
        String carId = request.getParameter("selectCar");
        System.out.println(carId);
        return "cars";
    }

    @GetMapping("/returnform")
    public ModelAndView getReturnForm (){
        return new ModelAndView("returnform");
    }


}

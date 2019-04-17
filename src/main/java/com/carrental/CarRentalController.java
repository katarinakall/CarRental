package com.carrental;

import com.carrental.domain.Car;
import com.carrental.repository.CarRentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    public ModelAndView rentFormSubmit(@ModelAttribute RentalRequest request, HttpSession session) {
        List<Car> cars = repository.getAvailableCars(request.getCarType());
        repository.addBooking(request);
        String ssn = repository.getCustomerSsn(request);
        session.setAttribute("ssn", ssn);
        return new ModelAndView("cars")
                .addObject("cars", cars);
    }


    @RequestMapping(value="/selectcar", method= RequestMethod.POST, params = {"selectCar"})
    public String selectCar(HttpServletRequest request, HttpSession session){
        String car = request.getParameter("selectCar");
        int carId = Integer.parseInt(car);
        String ssn = session.getAttribute("ssn").toString();
        repository.selectCar(carId, ssn);
        return "cars";
    }

    @GetMapping("/returnform")
    public ModelAndView getReturnForm (){
        return new ModelAndView("returnform");
    }


}

package com.carrental;

import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import com.carrental.repository.CarRentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

import static com.carrental.domain.CarType.SMALL;

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
    public ModelAndView rentFormSubmit(@ModelAttribute RentalRequest request) throws SQLException {
        List<Car> cars = repository.getAvailableCars(request.getCarType());
       // List<Car>cars = repository.getAllCars();
        System.out.println(cars.toString());
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

//    @GetMapping("/cars")
//    public ModelAndView listAvailableCars (Model model) throws SQLException {
//
//        return new ModelAndView("cars")
//        .addObject("cars", repository.getAvailableCars(SMALL));
//    }

}

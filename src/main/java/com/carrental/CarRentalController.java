package com.carrental;

import com.carrental.domain.Booking;
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
    public ModelAndView submitRentForm(@ModelAttribute RentalRequest request, HttpSession session) {
        List<Car> cars = repository.getAvailableCars(request.getCarType());
        repository.addBooking(request);
        String ssn = repository.getCustomerSsn(request);
        session.setAttribute("ssn", ssn);
        return new ModelAndView("cars")
                .addObject("cars", cars);
    }

    @RequestMapping(value="/selectcar", method= RequestMethod.POST, params = {"selectCar"})
    public ModelAndView selectCar(HttpServletRequest request, HttpSession session){
        String car = request.getParameter("selectCar");
        int carId = Integer.parseInt(car);
        String ssn = session.getAttribute("ssn").toString();
        repository.selectCar(carId, ssn);
        repository.toggleCarAvailability(carId, false);
        List<Booking> bookings = repository.getActiveBookings();
        return new ModelAndView("bookings")
                .addObject("bookings", bookings);
    }

    @RequestMapping(value="/returncar", method = RequestMethod.POST, params = {"returnCar"})
    public String returnCar(HttpServletRequest request, Model model, HttpSession session){
        String bookingNumber = request.getParameter("returnCar");
        session.setAttribute("bookingNumber", bookingNumber);
        model.addAttribute("returnRequest", new ReturnRequest());
        return "returnform";
    }

    @GetMapping("/bookings")
    public ModelAndView getAllBookings () {
        List<Booking> bookings = repository.getActiveBookings();
        return new ModelAndView("bookings")
                .addObject("bookings", bookings);
    }

    @PostMapping("/returnform")
    public String submitReturnForm(@ModelAttribute ReturnRequest request, Model model, HttpSession session){
        String bookingNumber = session.getAttribute("bookingNumber").toString();
        repository.returnCar(request, bookingNumber);

        Booking booking = repository.getBooking(bookingNumber);
        Car car = repository.getCar(booking.getCarId());

        double cost = repository.calculateCost(request, booking.getPickupDate(), car);

        System.out.println(cost);

        return "cost";
    }


}

package com.carrental;

import com.carrental.domain.Booking;
import com.carrental.domain.Car;
import com.carrental.domain.Customer;
import com.carrental.repository.CarRentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;


@Controller
public class CarRentalController {
    @Autowired
    private CarRentalRepository repository;

    @Autowired
    private CarRentalService service;

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
        return new ModelAndView("availablecars")
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

    @PostMapping("/managecar")
    public ModelAndView manageCars (HttpServletRequest request){
        String clean = request.getParameter("clean");
        String service = request.getParameter("service");
        String removeCar = request.getParameter("removeCar");

        if(clean != null) {
            int carId = Integer.parseInt(clean);
            repository.toggleCarCleaning(carId, true);
        }

        if(service != null){
            int carId = Integer.parseInt(service);
            repository.toggleService(carId, false);
        }

        if(removeCar != null) {
            int carId = Integer.parseInt(removeCar);
            repository.removeCar(carId);
        }

        List<Car> cars = repository.getAllCars();
        return new ModelAndView("cars")
                .addObject("cars", cars);
    }

    @GetMapping("/bookings")
    public ModelAndView getAllBookings () {
        List<Booking> bookings = repository.getActiveBookings();
        return new ModelAndView("bookings")
                .addObject("bookings", bookings);
    }
    @GetMapping("/cars")
    public ModelAndView getAllCars(){
        List<Car> cars = repository.getAllCars();
        return new ModelAndView("cars")
                .addObject("cars", cars);
    }

    @PostMapping("/returnform")
    public ModelAndView submitReturnForm(@ModelAttribute ReturnRequest request, HttpSession session){
        String bookingNumber = session.getAttribute("bookingNumber").toString();
        repository.returnCar(request, bookingNumber);

        Booking booking = repository.getBooking(bookingNumber);

        repository.toggleCarAvailability(booking.getCarId(), true);
        repository.updateCarMileage(booking.getCarId(), request.getMileageAtReturn());
        repository.updateTimesRented(booking.getCarId());
        repository.toggleCarCleaning(booking.getCarId(), true);

        Car car = repository.getCar(booking.getCarId());

        double cost = service.calculateCost(request, booking.getPickupDate(), car);

        return new ModelAndView("cost")
                .addObject("cost", cost);
    }

    @GetMapping("/customers")
    public ModelAndView getAllCustomers () {
        List<Customer> customers = repository.getAllCustomers();
        return new ModelAndView("customers")
                .addObject("customers", customers);
    }

    @RequestMapping(value="/selectcustomer", method= RequestMethod.POST, params = {"selectCustomer"})
    public ModelAndView getAllBookingsForCustomer(HttpServletRequest request){
        String ssn = request.getParameter("selectCustomer");
        List<Booking> bookings = repository.getAllBookingsForCustomer(ssn);
        System.out.println(bookings.toString());
        return new ModelAndView("bookings")
                .addObject("bookings", bookings);

    }



}

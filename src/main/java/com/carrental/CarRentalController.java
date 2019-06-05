package com.carrental;

import com.carrental.domain.*;
import com.carrental.repository.CarRentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
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
        session.setAttribute("carType", request.getCarType());
        if (repository.getCustomer(ssn) == null) {
            return new ModelAndView("newcustomer")
                    .addObject("customer", new Customer());
        }
        return new ModelAndView("availablecars")
                .addObject("cars", cars);
    }

    @PostMapping("/newcustomer")
    public ModelAndView addNewCustomer(Customer customer, HttpSession session){
        String ssn = session.getAttribute("ssn").toString();
        String displayName = session.getAttribute("carType").toString();
        CarType carType = CarType.fromString(displayName);
        repository.addNewCustomer(customer.getName(), customer.getSurname(), ssn);
        List<Car> cars = repository.getAvailableCars(carType);
        return new ModelAndView("availablecars")
                .addObject("cars", cars);
    }

    @PostMapping("/selectcar")
    public ModelAndView selectCar(HttpServletRequest request, HttpSession session) {
        String car = request.getParameter("selectCar");
        int carId = Integer.parseInt(car);
        String ssn = session.getAttribute("ssn").toString();
        service.selectCar(carId, ssn);
        List<Booking> bookings = repository.getActiveBookings();
        return new ModelAndView("bookings")
                .addObject("bookings", bookings);
    }

    @PostMapping("/returncar")
    public String returnCar(HttpServletRequest request, Model model, HttpSession session) {
        String bookingNumber = request.getParameter("returnCar");
        session.setAttribute("bookingNumber", bookingNumber);
        model.addAttribute("returnRequest", new ReturnRequest());
        return "returnform";
    }

    @PostMapping("/cars")
    public ModelAndView manageCars(HttpServletRequest request) {
        String clean = request.getParameter("clean");
        String service = request.getParameter("service");
        String removeCar = request.getParameter("removeCar");
        String carLog = request.getParameter("carLog");

        if (clean != null) {
            int carId = Integer.parseInt(clean);
            repository.toggleCarCleaning(carId, true);
        }

        if (service != null) {
            int carId = Integer.parseInt(service);
            repository.toggleService(carId, false);
        }

        if (removeCar != null) {
            int carId = Integer.parseInt(removeCar);
            repository.removeCar(carId);
        }

        if (carLog != null) {
            int carId = Integer.parseInt(carLog);
            List<Log> logs = repository.getCarLogs(carId);
            return new ModelAndView("log")
                    .addObject("logs", logs);
        }

        List<Car> cars = repository.getAllCars();
        return new ModelAndView("cars")
                .addObject("cars", cars)
                .addObject("car", new Car());
    }

    @GetMapping("/bookings")
    public ModelAndView getAllBookings() {
        List<Booking> bookings = repository.getActiveBookings();
        return new ModelAndView("bookings")
                .addObject("bookings", bookings);
    }

    @GetMapping("/cars")
    public ModelAndView getAllCars() {
        List<Car> cars = repository.getAllCars();
        return new ModelAndView("cars")
                .addObject("cars", cars)
                .addObject("car", new Car());
    }

    @PostMapping("/addcar")
    public ModelAndView addNewCar(@ModelAttribute Car car) {
        repository.addNewCar(car);
        List<Car> cars = repository.getAllCars();
        return new ModelAndView("cars")
                .addObject("cars", cars)
                .addObject("car", new Car());
    }

    @PostMapping("/returnform")
    public ModelAndView submitReturnForm(@ModelAttribute ReturnRequest request, HttpSession session) {
        String bookingNumber = session.getAttribute("bookingNumber").toString();
        repository.returnCar(request, bookingNumber);

        Booking booking = repository.getBooking(bookingNumber);

        Car car = repository.getCar(booking.getCarId());
        CostVariables costVariables = service.memberDiscount(request, booking.getPickupDate(), booking.getCarId(), booking.getCustomerSSN());
        BigDecimal cost = service.calculateCost(costVariables, car);

        service.updateReturnedCar(booking.getCarId(), request.getMileageAtReturn(), booking.getCustomerSSN());

        System.out.println(costVariables.toString());
        return new ModelAndView("cost")
                .addObject("cost", cost);
    }

    @GetMapping("/customers")
    public ModelAndView getAllCustomers() {
        List<Customer> customers = repository.getAllCustomers();
        return new ModelAndView("customers")
                .addObject("customers", customers);
    }

    @PostMapping("/selectcustomer")
    public ModelAndView getAllBookingsForCustomer(HttpServletRequest request) {
        String ssn = request.getParameter("selectCustomer");
        List<Booking> bookings = repository.getAllBookingsForCustomer(ssn);
        List<Log> logs = repository.getCustomersLogs(ssn);
        System.out.println(bookings.toString());
        return new ModelAndView("bookings")
                .addObject("bookings", bookings)
                .addObject("logs", logs);
    }

    @GetMapping("/log")
    public ModelAndView getAllLogs() {
        List<Log> logs = repository.getAllLogs();
        return new ModelAndView("log")
                .addObject("logs", logs);
    }
}

package com.Tekarch.BookingService.Controller;

import com.Tekarch.BookingService.DTO.BookingDto;
import com.Tekarch.BookingService.Exceptions.BookingNotFoundException;
import com.Tekarch.BookingService.Service.BookingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://tekair-frontendapp.s3-website-us-west-1.amazonaws.com/")
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    private static final Logger logger = LogManager.getLogger(BookingController.class);
    // Create a new booking
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingDto bookingDto) {
        try {
            BookingDto createdBooking = bookingService.createBooking(bookingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
            //return ResponseEntity.ok(createdBooking);

        } catch (BookingNotFoundException e) {
            logger.error("Booking not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null); // Adjust response as needed
        }
    }

    // Get booking details
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId) {
        try {
            BookingDto booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(booking);
        } catch (BookingNotFoundException e) {
            logger.error("Booking not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Retrieve all bookings for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDto>> getBookingsByUser(@PathVariable Long userId) {
        try {
            List<BookingDto> bookings = bookingService.getBookingsByUserId(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            logger.error("Error fetching bookings for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Cancel a booking
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking Id " + bookingId + " deleted successfully.");

        } catch (BookingNotFoundException e) {
            logger.error("Booking not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error canceling booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler
    public ResponseEntity<String> responseWithError(Exception e) {
        logger.error("Exception Occured.Details : {}", e.getMessage());
        return new ResponseEntity<>("Exception Occured.More Info :"
                + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}

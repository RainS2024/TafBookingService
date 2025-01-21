package com.Tekarch.BookingService.Service;

import com.Tekarch.BookingService.DTO.BookingDto;
import com.Tekarch.BookingService.DTO.FlightDto;
import com.Tekarch.BookingService.DTO.UserDto;
import com.Tekarch.BookingService.Service.Interface.BookingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class BookingService implements BookingInterface {
    @Autowired
    private static final Logger logger = LogManager.getLogger(BookingService.class);

    @Autowired
    private RestTemplate restTemplate;

    // Base URL for the Data microservice
    @Value("${db.service.url}")
    private String dataStoreServiceUrl;

    // Create a new booking
    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
//
        try {
            validateBookingDto(bookingDto);

            // Fetch and validate flight and user
            FlightDto flight = fetchFlightDetails(bookingDto.getFlightId());
            UserDto user = fetchUserDetails(bookingDto.getUserId());

            // Reduce available seats
            reduceFlightSeats(flight, 1);

            // Save booking
            String bookingUrl = dataStoreServiceUrl + "/bookings";
            return restTemplate.postForObject(bookingUrl, bookingDto, BookingDto.class);

        } catch (Exception e) {
            logger.error("Error creating booking", e);
            throw new RuntimeException("Booking creation failed: " + e.getMessage(), e);
        }
    }

    private void validateBookingDto(BookingDto bookingDto) {
        if (bookingDto.getFlightId() == null || bookingDto.getUserId() == null) {
            throw new IllegalArgumentException("Flight ID and User ID cannot be null");
        }
    }

    private FlightDto fetchFlightDetails(Long flightId) {
        String flightUrl = dataStoreServiceUrl + "/flights/" + flightId;
        FlightDto flight = restTemplate.getForObject(flightUrl, FlightDto.class);
        if (flight == null) throw new RuntimeException("Flight not found");
        if (flight.getAvailableSeats() <= 0) throw new RuntimeException("Flight fully booked");
        return flight;
    }

    private UserDto fetchUserDetails(Long userId) {
        String userUrl = dataStoreServiceUrl + "/users/" + userId;
        UserDto user = restTemplate.getForObject(userUrl, UserDto.class);
        if (user == null) throw new RuntimeException("User not found");
        return user;
    }

    private void reduceFlightSeats(FlightDto flight, int seatsToReduce) {
        if (flight.getAvailableSeats() < seatsToReduce) {
            throw new RuntimeException("Not enough available seats to reduce");
        }

        // Reduce the seats in the flight object
        flight.setAvailableSeats(flight.getAvailableSeats() - seatsToReduce);

        // Send a PUT request to update the flight in the Data Store
        String updateFlightUrl = dataStoreServiceUrl + "/flights/" + flight.getFlightId();
        restTemplate.put(updateFlightUrl, flight);
    }


    // Get booking by ID
    @Override
    public BookingDto getBookingById(Long bookingId) {
        try {
            String url = dataStoreServiceUrl + "/bookings/" + bookingId;
            return restTemplate.getForObject(url, BookingDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching booking: " + e.getMessage());
        }
    }

    // Get all bookings for a user
    @Override
    public List<BookingDto> getBookingsByUserId(Long userId) {
        try {
            String url = dataStoreServiceUrl + "/bookings/user/" + userId;
            ResponseEntity<List<BookingDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<BookingDto>>() {}
            );

            if (response.getBody() == null || response.getBody().isEmpty()) {
                throw new RuntimeException("No bookings found for user ID: " + userId);
            }

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching bookings for user: " + e.getMessage(), e);
        }
    }

    // Cancel booking
    @Override
    public void cancelBooking(Long bookingId) {
        try {
            // Get booking details
            BookingDto booking = getBookingById(bookingId);
            if (booking == null) {
                throw new RuntimeException("Booking not found for ID: " + bookingId);
            }

            if (booking.getStatus() == BookingDto.BookingStatus.CANCELLED) {
                throw new RuntimeException("Booking is already cancelled.");
            }

            // Mark status as "Cancelled"
            booking.setStatus(BookingDto.BookingStatus.CANCELLED);

            // Update booking in DataStore
            String url = dataStoreServiceUrl + "/bookings/" + bookingId;
            restTemplate.put(url, booking);
        } catch (Exception e) {
            throw new RuntimeException("Error cancelling booking: " + e.getMessage(), e);
        }
}
}

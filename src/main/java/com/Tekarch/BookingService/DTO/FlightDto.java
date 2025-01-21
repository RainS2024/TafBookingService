package com.Tekarch.BookingService.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDto {

    private Long flightId;

    @NotBlank(message = "Flight number cannot be blank")
    private String flightNumber;

    @NotBlank(message = "Departure location cannot be blank")
    private String departure;

    @NotBlank(message = "Arrival location cannot be blank")
    private String arrival;

    @NotNull(message = "Departure time cannot be null")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time cannot be null")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be a positive value")
    private Double price;

    @NotNull(message = "Available seats cannot be null")
    @PositiveOrZero(message = "Available seats cannot be negative")
    private Integer availableSeats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Custom validation method
    @AssertTrue(message = "Arrival time must be after departure time")
    private boolean isArrivalAfterDeparture() {
        return arrivalTime.isAfter(departureTime);
    }
}

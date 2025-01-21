package com.Tekarch.BookingService.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class BookingDto {
    private Long bookingId;

    @NotNull(message = "User ID cannot be null")
    private Long userId; // Reference to User instead of the full User object

    @NotNull(message = "Flight ID cannot be null")
    private Long flightId; // Reference to Flight instead of the full Flight object

    @NotNull(message = "Status cannot be null")
    private BookingStatus status;// Enum for "Booked" or "Cancelled"

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum BookingStatus {
        PENDING, BOOKED, CANCELLED
    }
}

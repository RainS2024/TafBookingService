package com.Tekarch.BookingService.Service.Interface;

import com.Tekarch.BookingService.DTO.BookingDto;

import java.util.List;

public interface BookingInterface {
    BookingDto createBooking(BookingDto bookingDto);
    BookingDto getBookingById(Long bookingId);
    List<BookingDto> getBookingsByUserId(Long userId);
    void cancelBooking(Long bookingId);

}

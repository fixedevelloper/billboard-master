package com.cscreativ.billboard.booking.api.mapper;

import com.cscreativ.billboard.booking.api.response.BookingResponse;
import com.cscreativ.billboard.booking.domain.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getBillboardId(),
                booking.getAdvertiserId(),
                booking.getPeriod().getStartDate(),
                booking.getPeriod().getEndDate(),
                booking.getTotalPrice(),
                booking.getCurrency(),
                booking.getStatus().name()
        );
    }
}

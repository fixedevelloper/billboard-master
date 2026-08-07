package com.cscreativ.billboard.booking.api;

import com.cscreativ.billboard.booking.api.mapper.BookingMapper;
import com.cscreativ.billboard.booking.api.request.CreateBookingRequest;
import com.cscreativ.billboard.booking.api.response.BookingResponse;
import com.cscreativ.billboard.booking.application.BookingService;
import com.cscreativ.billboard.booking.domain.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @GetMapping("/unpaid-expiration-days")
    public ResponseEntity<Integer> getUnpaidExpirationDays() {
        return ResponseEntity.ok(bookingService.getUnpaidExpirationDays());
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(
                request.billboardId(),
                request.advertiserId(),
                request.startDate(),
                request.endDate(),
                request.dailyRate(),
                request.currency()
        );
        return ResponseEntity.ok(bookingMapper.toResponse(booking));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable UUID id) {
        Booking booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(bookingMapper.toResponse(booking));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmBooking(@PathVariable UUID id) {
        bookingService.confirmBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/advertiser/{advertiserId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByAdvertiser(@PathVariable UUID advertiserId) {
        List<Booking> bookings = bookingService.getBookingsByAdvertiser(advertiserId);
        return ResponseEntity.ok(bookings.stream().map(bookingMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/billboard/{billboardId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByBillboard(@PathVariable UUID billboardId) {
        List<Booking> bookings = bookingService.getBookingsByBillboard(billboardId);
        return ResponseEntity.ok(bookings.stream().map(bookingMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings.stream().map(bookingMapper::toResponse).collect(Collectors.toList()));
    }
}

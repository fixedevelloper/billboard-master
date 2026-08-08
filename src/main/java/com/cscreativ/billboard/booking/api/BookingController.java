package com.cscreativ.billboard.booking.api;

import com.cscreativ.billboard.billboard.BillboardFacade;
import com.cscreativ.billboard.booking.api.mapper.BookingMapper;
import com.cscreativ.billboard.booking.api.request.CreateBookingRequest;
import com.cscreativ.billboard.booking.api.response.BookingResponse;
import com.cscreativ.billboard.booking.application.BookingService;
import com.cscreativ.billboard.booking.domain.Booking;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final BillboardFacade billboardFacade;

    public BookingController(BookingService bookingService, BookingMapper bookingMapper, BillboardFacade billboardFacade) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
        this.billboardFacade = billboardFacade;
    }

    @GetMapping("/unpaid-expiration-days")
    public ResponseEntity<Integer> getUnpaidExpirationDays() {
        return ResponseEntity.ok(bookingService.getUnpaidExpirationDays());
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody CreateBookingRequest request,
                                                          @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(request.advertiserId()));
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
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable UUID id,
                                                           @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Booking booking = bookingService.getBookingById(id);
        requirePartyToBooking(booking, currentUser);
        return ResponseEntity.ok(bookingMapper.toResponse(booking));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmBooking(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requirePartyToBooking(bookingService.getBookingById(id), currentUser);
        bookingService.confirmBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requirePartyToBooking(bookingService.getBookingById(id), currentUser);
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/advertiser/{advertiserId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByAdvertiser(@PathVariable UUID advertiserId,
                                                                          @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(advertiserId));
        List<Booking> bookings = bookingService.getBookingsByAdvertiser(advertiserId);
        return ResponseEntity.ok(bookings.stream().map(bookingMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/billboard/{billboardId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByBillboard(@PathVariable UUID billboardId,
                                                                         @AuthenticationPrincipal AuthenticatedUser currentUser) {
        UUID ownerId = billboardFacade.findOwnerIdByBillboard(billboardId).orElse(null);
        currentUser.require(currentUser.isAdmin() || currentUser.isOwner(ownerId));
        List<Booking> bookings = bookingService.getBookingsByBillboard(billboardId);
        return ResponseEntity.ok(bookings.stream().map(bookingMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings.stream().map(bookingMapper::toResponse).collect(Collectors.toList()));
    }

    private void requirePartyToBooking(Booking booking, AuthenticatedUser currentUser) {
        UUID ownerId = billboardFacade.findOwnerIdByBillboard(booking.getBillboardId()).orElse(null);
        currentUser.require(currentUser.isAdmin()
                || currentUser.isAdvertiser(booking.getAdvertiserId())
                || currentUser.isOwner(ownerId));
    }
}

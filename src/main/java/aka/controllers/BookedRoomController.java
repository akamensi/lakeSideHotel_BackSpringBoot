package aka.controllers;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import aka.exceptions.InvalidBookingRequestException;
import aka.exceptions.ResourceNotFoundException;
import aka.models.*;
import aka.response.*;
import aka.services.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookedRoomController {

	private final IBookedRoomService bookingService;
	private final IRoomService roomService;

@GetMapping("/all-bookings")
public ResponseEntity<List<BookingResponse>> getAllBookings() {
	List<BookedRoom> bookings = bookingService.getAllBookings();
	List<BookingResponse> bookingResponses = new ArrayList<>();
	for (BookedRoom booking : bookings) {
		BookingResponse bookingResponse = getBookingResponse(booking);
		bookingResponses.add(bookingResponse);
	}
	return ResponseEntity.ok(bookingResponses);
 }


@GetMapping("/confirmation/{confirmationCode}")
public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
    try{
        BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
        BookingResponse bookingResponse = getBookingResponse(booking);
        return ResponseEntity.ok(bookingResponse);
    }catch (ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}


@PostMapping("/room/{roomId}/booking")
public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                     @RequestBody BookedRoom bookingRequest){
    try{
        String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
        return ResponseEntity.ok(
                "Room booked successfully, Your booking confirmation code is :"+confirmationCode);

    }catch (InvalidBookingRequestException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


@DeleteMapping("/booking/{bookingId}/delete")
public void cancelBooking(@PathVariable Long bookingId){
    bookingService.cancelBooking(bookingId);
}


@GetMapping("/user/{email}/bookings")
public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email) {
    List<BookedRoom> bookings = bookingService.getBookingsByUserEmail(email);
    List<BookingResponse> bookingResponses = new ArrayList<>();
    for (BookedRoom booking : bookings) {
        BookingResponse bookingResponse = getBookingResponse(booking);
        bookingResponses.add(bookingResponse);
    }
    return ResponseEntity.ok(bookingResponses);
}






private BookingResponse getBookingResponse(BookedRoom booking) {
    Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
    RoomResponse room = new RoomResponse(
            theRoom.getId(),
            theRoom.getRoomType(),
            theRoom.getRoomPrice());
    return new BookingResponse(
            booking.getBookingId(),
            booking.getCheckInDate(),
            booking.getCheckOutDate(),
            booking.getGuestFullName(),
            booking.getGuestEmail(),
            booking.getNumOfAdults(),
            booking.getNumOfChildern(),
            booking.getTotalNumOfGuest(),
            booking.getBookingConfirmationCode(),
            room);
}

}

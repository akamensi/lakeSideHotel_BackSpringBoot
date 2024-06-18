package aka.services;

import java.util.List;

import org.springframework.stereotype.Service;

import aka.exceptions.InvalidBookingRequestException;
import aka.exceptions.ResourceNotFoundException;
import aka.models.BookedRoom;
import aka.models.Room;
import aka.repositories.BookedRoomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookedRoomServiceImpl implements IBookedRoomService  {

	private final BookedRoomRepository bookedroomrepository;
	private final IRoomService roomservice;
	
	   @Override
	    public List<BookedRoom> getAllBookings() {
	        return bookedroomrepository.findAll();
	    }


	    @Override
	    public List<BookedRoom> getBookingsByUserEmail(String email) {
	        return bookedroomrepository.findByGuestEmail(email);
	    }

	    @Override
	    public void cancelBooking(Long bookingId) {
	    	bookedroomrepository.deleteById(bookingId);
	    }

	    @Override
	    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
	        return bookedroomrepository.findByRoomId(roomId);
	    }

	    @Override
	    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
	        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
	            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
	        }
	        Room room = roomservice.getRoomById(roomId).get();
	        List<BookedRoom> existingBookings = room.getBookings();
	        boolean roomIsAvailable = roomIsAvailable(bookingRequest,existingBookings);
	        if (roomIsAvailable){
	            room.addBooking(bookingRequest);
	            bookedroomrepository.save(bookingRequest);
	        }else{
	            throw  new InvalidBookingRequestException("Sorry, This room is not available for the selected dates;");
	        }
	        return bookingRequest.getBookingConfirmationCode();
	    }

	    @Override
	    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
	        return bookedroomrepository.findByBookingConfirmationCode(confirmationCode)
	                .orElseThrow(() -> new ResourceNotFoundException("No booking found with booking code :"+confirmationCode));

	    }


	    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
	        return existingBookings.stream()
	                .noneMatch(existingBooking ->
	                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
	                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
	                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
	                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
	                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

	                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
	                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

	                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

	                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
	                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

	                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
	                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
	                );
	    }

}

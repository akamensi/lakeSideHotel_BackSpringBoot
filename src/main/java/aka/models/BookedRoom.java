package aka.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookedRoom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookingId;
	
	@Column(name = "check_In")
	private LocalDate checkInDate;
	
	@Column(name = "check_Out")
	private LocalDate checkOutDate;
	
	@Column(name = "guest_FullName")
	private String guestFullName;
	
	@Column(name = "guest_Email")
	private String guestEmail;
	
	@Column(name = "adults")
	private int numOfAdults;
	
	@Column(name = "children")
	private int numOfChildern;
	
	@Column(name = "total_Guest")
	private int totalNumOfGuest;
	
	@Column(name = "confirmation_Code")
	private String bookingConfirmationCode;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_Id")
	private Room room;
	
	public void calculTotalNumOfGuest() {
		this.totalNumOfGuest = this.numOfAdults + this.numOfChildern;
	}

	public void setNumOfAdults(int numOfAdults) {
		this.numOfAdults = numOfAdults;
		calculTotalNumOfGuest();
	}

	public void setNumOfChildern(int numOfChildern) {
		this.numOfChildern = numOfChildern;
		calculTotalNumOfGuest();
	}

	public void setBookingConfirmationCode(String bookingConfirmationCode) {
		this.bookingConfirmationCode = bookingConfirmationCode;
	}
	
	
	
	
	
	

	

	

}

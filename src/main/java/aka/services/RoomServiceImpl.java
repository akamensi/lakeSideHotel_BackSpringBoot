package aka.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import aka.exceptions.InternalServerException;
import aka.exceptions.ResourceNotFoundException;
import aka.models.Room;
import aka.repositories.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements IRoomService {

	@Autowired
	private final RoomRepository roomRepository;

	@Override
	public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws SQLException, IOException {
		Room room = new Room();
		room.setRoomType(roomType);
		room.setRoomPrice(roomPrice);
		if (!file.isEmpty()) {
			byte[] photoBytes = file.getBytes();
			Blob photoBlob = new SerialBlob(photoBytes);
			room.setPhoto(photoBlob);
		}
		return roomRepository.save(room);
	}

	@Override
	public List<String> getAllRoomTypes() {
		return roomRepository.findDistinctRoomTypes();
	}

	@Override
	public List<Room> getAllRooms() {
		return roomRepository.findAll();
	}

	@Override
	public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
		Optional<Room> theRoom = roomRepository.findById(roomId);
		if (theRoom.isEmpty()) {
			throw new ResourceNotFoundException("Sorry, Room not found!");
		}
		Blob photoBlob = theRoom.get().getPhoto();
		if (photoBlob != null) {
			return photoBlob.getBytes(1, (int) photoBlob.length());
		}
		return null;
	}

	@Override
	public void deleteRoom(Long roomId) {
		Optional<Room> theRoom = roomRepository.findById(roomId);
		if (theRoom.isPresent()) {
			roomRepository.deleteById(roomId);
		}
	}

	// with this method it capable of updating each of the fields separately
	@Override
	public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) {
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room not found"));

		if (roomType != null) {
			room.setRoomType(roomType);
		}

		if (roomPrice != null) {
			room.setRoomPrice(roomPrice);
		}

		if (photoBytes != null && photoBytes.length > 0) {
			try {
				room.setPhoto(new SerialBlob(photoBytes));
			} catch (SQLException ex) {
				throw new InternalServerException("Failed to update room photo");
			}
		}

		return roomRepository.save(room);
	}

	/*
	 * // we can be capable of updating each of the fields separately
	 * 
	 * @Override public Room updateRoom(Long roomId, String roomType, BigDecimal
	 * roomPrice, byte[] photoBytes) { Room room =
	 * roomRepository.findById(roomId).get(); if (roomType != null)
	 * room.setRoomType(roomType); if (roomPrice != null)
	 * room.setRoomPrice(roomPrice); if (photoBytes != null && photoBytes.length >
	 * 0) { try { room.setPhoto(new SerialBlob(photoBytes)); } catch (SQLException
	 * ex) { throw new InternalServerException("Fail updating room"); } } return
	 * roomRepository.save(room); }
	 */
	@Override
	public Optional<Room> getRoomById(Long roomId) {
		return Optional.of(roomRepository.findById(roomId).get());
	}

	@Override
	public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
		return roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
	}

}

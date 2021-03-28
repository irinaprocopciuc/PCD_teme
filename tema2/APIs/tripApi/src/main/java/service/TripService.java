package service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dao.TripInterface;
import model.TripDetails;

@Service
public class TripService {

	private final TripInterface conn;

	@Autowired
	public TripService(@Qualifier("Trip") TripInterface connection) {
		this.conn = connection;
	}

	public List<Map<String, String>> getAllTrips() {
		return conn.getAllTrips();
	}

	public Map<String, String> getTripDetails(String tripID) {
		return conn.getTripDetails(tripID);
	}

	public Map<String, String> getTripDetailsByName(String tripName) {
		return conn.getTripDetailsByName(tripName);
	}

	public boolean addTrip(TripDetails credentials) {
		if (conn.findTrip(credentials.getTripname()) == -1) {
			return conn.addTrip(credentials);
		} else {
			return false;
		}
	}

	public String addTripWithParticipants(TripDetails credentials) {
		return conn.addTripWithParticipants(credentials);
	}

	public boolean updateTripList(TripDetails[] tripList) {
		return conn.updateTripList(tripList);
	}

	public String updateTripDetails(TripDetails tripDetails) {
		return conn.updateTripDetails(tripDetails);
	}

	public boolean deleteTrip(String tripId) {
		return conn.deleteTrip(tripId);
	}

	public boolean deleteAllTrips() {
		return conn.deleteTrips();
	}
}

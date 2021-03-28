package dao;

import java.util.List;
import java.util.Map;

import model.TripDetails;

public interface TripInterface {

	public List<Map<String, String>> getAllTrips();

	public Map<String, String> getTripDetails(String tripID);

	public Map<String, String> getTripDetailsByName(String tripName);

	public int findTrip(String tripName);

	public boolean addTrip(TripDetails details);

	public String addTripWithParticipants(TripDetails details);

	public boolean updateTripList(TripDetails[] tripList);

	public String updateTripDetails(TripDetails tripDetails);

	public boolean deleteTrip(String tripId);

	public boolean deleteTrips();
}

package api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.TripDetails;
import service.TripService;

@RequestMapping("")
@RestController
public class TripController {

	private final TripService tripService;

	@Autowired
	public TripController(TripService tripService) {
		this.tripService = tripService;
	}

	@GetMapping(path = "/trips")
	public ResponseEntity<String> getTrips() throws JsonProcessingException {
		Map<String, Object> map = new HashMap<>();
		List<Map<String, String>> tripsList = tripService.getAllTrips();
		if (!tripsList.isEmpty()) {
			map.put("status", HttpStatus.OK);
			map.put("code", "200");
			map.put("message", "Trips retrieved!");
			map.put("response", tripsList);
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.OK);
		} else {
			map.put("status", HttpStatus.NOT_FOUND);
			map.put("code", "404");
			map.put("message", "Trip details not found!");
			map.put("response", "");
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(path = "/trip/tripID={tripID}")
	public ResponseEntity<String> getTripDetails(@PathVariable("tripID") String tripID) throws JsonProcessingException {
		Map<String, Object> map = new HashMap<>();
		if (tripID.matches("\\d+")) {
			Map<String, String> tripDetails = tripService.getTripDetails(tripID);
			if (tripDetails.containsKey("idtrip")) {
				map.put("status", HttpStatus.OK);
				map.put("code", "200");
				map.put("message", "Trip details retrieved!");
				map.put("response", tripDetails);
				return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
						HttpStatus.OK);
			} else {
				map.put("status", HttpStatus.NOT_FOUND);
				map.put("code", "404");
				map.put("message", "Trip details not found!");
				map.put("response", "");
				return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
						HttpStatus.NOT_FOUND);
			}
		} else {
			map.put("status", HttpStatus.BAD_REQUEST);
			map.put("code", "400");
			map.put("message", "Failed to retrive trip details, trip id does not match the criteria!");
			map.put("response", "null");
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(path = "/trips")
	public ResponseEntity<String> addTrip(@Valid @RequestBody TripDetails credentials, HttpServletResponse response)
			throws JsonProcessingException {

		Map<String, Object> map = new HashMap<>();

		if (!tripService.addTrip(credentials)) {
			map.put("status", HttpStatus.CONFLICT);
			map.put("code", "409");
			map.put("message", "Trip already exists!");
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.CONFLICT);
		} else {
			List<Map<String, String>> tripsList = tripService.getAllTrips();
			map.put("status", HttpStatus.CREATED);
			map.put("code", "201");
			map.put("message", "Trip added successfully!");
			map.put("response", tripsList);
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.CREATED);
		}

	}

	@PostMapping(path = "/trip")
	public ResponseEntity<String> addTripWithParticipants(@Valid @RequestBody TripDetails credentials,
			HttpServletResponse response) throws JsonProcessingException {

		Map<String, Object> map = new HashMap<>();
		String result = tripService.addTripWithParticipants(credentials);

		if (result.equals("trip created") | result.equals("updated")) {
			Map<String, String> createdTrip = tripService.getTripDetailsByName(credentials.getTripname());
			map.put("status", HttpStatus.CREATED);
			map.put("code", "201");
			map.put("message", "Trip or Participants added successfully!");
			map.put("response", createdTrip);
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.CREATED);
		} else if (result.equals("no participants")) {
			map.put("status", HttpStatus.BAD_REQUEST);
			map.put("code", "400");
			map.put("message", "Participants are empty!");
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.BAD_REQUEST);
		} else {
			map.put("status", HttpStatus.CONFLICT);
			map.put("code", "409");
			map.put("message", "Participants already exists!");
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.CONFLICT);
		}

	}

	@PutMapping(path = "/trips")
	public ResponseEntity<String> updateTripList(@Valid @RequestBody TripDetails[] tripList,
			HttpServletResponse response) throws JsonProcessingException {

		Map<String, Object> map = new HashMap<>();

		if (!tripService.updateTripList(tripList)) {
			map.put("status", HttpStatus.NOT_MODIFIED);
			map.put("code", "304");
			map.put("message", "The trips list wasn't updated!");
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.NOT_MODIFIED);
		} else {
			List<Map<String, String>> newTripList = tripService.getAllTrips();
			map.put("status", HttpStatus.OK);
			map.put("code", "200");
			map.put("message", "Trip list updated successfully!");
			map.put("response", newTripList);
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.OK);
		}
	}

	@PutMapping(path = "/trip")
	public ResponseEntity<String> updateTripDetails(@Valid @RequestBody TripDetails tripDetails,
			HttpServletResponse response) throws JsonProcessingException {

		Map<String, Object> map = new HashMap<>();
		String result = tripService.updateTripDetails(tripDetails);

		if (result.equals("modified")) {
			Map<String, String> newTrip = tripService.getTripDetailsByName(tripDetails.getTripname());
			map.put("status", HttpStatus.OK);
			map.put("code", "200");
			map.put("message", "Trip updated successfully!");
			map.put("response", newTrip);
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.OK);
		} else if (result.equals("no participants")) {
			map.put("status", HttpStatus.BAD_REQUEST);
			map.put("code", "400");
			map.put("message", "Participants are empty!");
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.BAD_REQUEST);
		} else {
			map.put("status", HttpStatus.NOT_MODIFIED);
			map.put("code", "304");
			map.put("message", "The details were not modified!");
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.NOT_MODIFIED);
		}
	}

	@DeleteMapping(value = "/trips")
	public ResponseEntity<String> deteleTrips() throws JsonProcessingException {
		Map<String, Object> map = new HashMap<>();
		Boolean tripDeleteResponse = tripService.deleteAllTrips();
		if (tripDeleteResponse == true) {
			List<Map<String, String>> tripsList = tripService.getAllTrips();
			map.put("status", HttpStatus.OK);
			map.put("code", "200");
			map.put("message", "Trips deleted successfully!");
			map.put("response", tripsList);
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.OK);
		} else {
			map.put("status", HttpStatus.NOT_FOUND);
			map.put("code", "404");
			map.put("message", "Can not delete the trips because the list is empty");
			map.put("response", tripDeleteResponse);
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping(value = "/trip/tripID={tripId}")
	public ResponseEntity<String> deteleTrip(@PathVariable("tripId") String tripId) throws JsonProcessingException {
		Map<String, Object> map = new HashMap<>();
		if (tripId.matches("\\d+")) {
			Map<String, String> tripsList = tripService.getTripDetails(tripId);
			Boolean tripDeleteResponse = tripService.deleteTrip(tripId);
			if (tripDeleteResponse == true) {
				map.put("status", HttpStatus.OK);
				map.put("code", "200");
				map.put("message", "Trip deleted!");
				map.put("response", tripsList);
				return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
						HttpStatus.OK);
			} else {
				map.put("status", HttpStatus.NOT_FOUND);
				map.put("code", "404");
				map.put("message", "Trip not deleted, trip id not found!");
				map.put("response", tripDeleteResponse);
				return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
						HttpStatus.NOT_FOUND);
			}

		} else {
			map.put("status", HttpStatus.BAD_REQUEST);
			map.put("code", "400");
			map.put("message", "Trip id does not match the type!");
			map.put("response", "false");
			return new ResponseEntity<>(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map),
					HttpStatus.BAD_REQUEST);
		}
	}

}

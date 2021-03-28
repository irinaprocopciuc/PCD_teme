package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import model.TripDetails;

@Repository("Trip")
public class Trip implements TripInterface {

	public static Connection conn;

	public Trip() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/baca", "root", "root");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Map<String, String>> getAllTrips() {
		List<Map<String, String>> allTripsList = new ArrayList<>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("select idtrip, tripName, startingPoint, destination, startDate, endDate from trip;");
			while (rs.next()) {
				Map<String, String> trip = new HashMap<>();
				trip.put("idtrip", rs.getString(1));
				trip.put("tripName", rs.getString(2));
				trip.put("startingPoint", rs.getString(3));
				trip.put("destination", rs.getString(4));
				trip.put("startDate", rs.getString(5));
				trip.put("endDate", rs.getString(6));
				allTripsList.add(trip);
			}
			return allTripsList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return allTripsList;
	}

	@Override
	public Map<String, String> getTripDetails(String tripID) {
		Map<String, String> trip = new HashMap<>();

		if (tripID.equals("-1")) {
			return trip;
		} else {
			try {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"select tripName, startingPoint, destination, startDate, endDate from trip where idtrip = "
								+ tripID + ";");
				while (rs.next()) {
					trip = new HashMap<>();
					trip.put("idtrip", tripID);
					trip.put("tripName", rs.getString(1));
					trip.put("startingPoint", rs.getString(2));
					trip.put("destination", rs.getString(3));
					trip.put("startDate", rs.getString(4));
					trip.put("endDate", rs.getString(5));
				}
				rs = stmt.executeQuery(
						"select f.userId, g.user_name from tripparticipants f join user g on f.userid = g.iduser"
								+ " where tripid = " + tripID + ";");

				List<String> users = new ArrayList<String>();
				String user;

				while (rs.next()) {
					user = "{ userId: " + rs.getString(1) + ", userName: " + rs.getString(2) + "}";
					users.add(user);
				}
				if (!users.isEmpty()) {
					trip.put("participants", users.toString());
				}

				return trip;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return trip;
	}

	@Override
	public Map<String, String> getTripDetailsByName(String tripName) {
		Map<String, String> trip = new HashMap<>();

		if (tripName.isEmpty()) {
			return trip;
		} else {
			try {
				String tripId = "";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"select idTrip, tripName, startingPoint, destination, startDate, endDate from trip where tripName = '"
								+ tripName + "';");
				while (rs.next()) {
					trip = new HashMap<>();
					trip.put("idtrip", rs.getString(1));
					trip.put("tripName", rs.getString(2));
					trip.put("startingPoint", rs.getString(3));
					trip.put("destination", rs.getString(4));
					trip.put("startDate", rs.getString(5));
					trip.put("endDate", rs.getString(6));
					tripId = rs.getString(1);
				}
				rs = stmt.executeQuery(
						"select f.userId, g.user_name from tripparticipants f join user g on f.userid = g.iduser"
								+ " where tripid = " + tripId + ";");

				List<String> users = new ArrayList<String>();
				String user;

				while (rs.next()) {
					user = "{ userId: " + rs.getString(1) + ", userName: " + rs.getString(2) + "}";
					users.add(user);
				}
				if (!users.isEmpty()) {
					trip.put("participants", users.toString());
				}

				return trip;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return trip;
	}

	@Override
	public boolean addTrip(TripDetails credentials) {
		int tripID = this.generateTripID();
		if (tripID != -1) {

			try {
				Statement stmt = conn.createStatement();

				ResultSet rs = stmt.executeQuery("select tripName from trip where idtrip = '" + tripID + "'");
				boolean alreadyExists = rs.next();

				if (alreadyExists == false) {

					stmt.executeUpdate(
							"insert into trip (idtrip, tripName, startingPoint, destination, startDate, endDate) values ('"
									+ tripID + "', '" + credentials.getTripname() + "', '"
									+ credentials.getStartingPoint() + "', '" + credentials.getDestination() + "', '"
									+ credentials.getStartDate() + "', '" + credentials.getEndDate() + "');");
					return true;

				}
				return false;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public String addTripWithParticipants(TripDetails credentials) {
		int tripID = this.generateTripID();
		if (tripID != -1) {

			try {
				Statement stmt = conn.createStatement();

				ResultSet rs = stmt
						.executeQuery("select idTrip from trip where tripName = '" + credentials.getTripname() + "'");
				boolean alreadyExists = rs.next();
				if (alreadyExists == false) {

					stmt.executeUpdate(
							"insert into trip (idtrip, tripName, startingPoint, destination, startDate, endDate) values ('"
									+ tripID + "', '" + credentials.getTripname() + "', '"
									+ credentials.getStartingPoint() + "', '" + credentials.getDestination() + "', '"
									+ credentials.getStartDate() + "', '" + credentials.getEndDate() + "');");

					String cred = credentials.getParticipants().toString();
					String[] participants = cred.split(",");

					String userId = "";
					for (int index = 0; index < participants.length; index++) {

						userId = participants[index].substring(participants[index].indexOf(":") + 2,
								participants[index].indexOf("}"));

						stmt.executeUpdate("insert into tripparticipants(tripId, userId) values ('" + tripID + "', '"
								+ userId + "');");
					}

					return "trip created";
				} else {
					tripID = Integer.parseInt(rs.getString(1));

					String cred = credentials.getParticipants();
					List<String> participantsList = new ArrayList<String>();

					if (cred.contains("{")) {
						String[] participants = cred.split(",");

						for (int index = 0; index < participants.length; index++) {

							participantsList.add(participants[index].substring(participants[index].indexOf(":") + 2,
									participants[index].indexOf("}")));
						}

						rs = stmt.executeQuery("select userId from tripparticipants where tripid = '" + tripID + "'");
						String p = "";
						while (rs.next()) {
							p = rs.getString(1);
							if (participantsList.contains(p)) {
								participantsList.remove(p);
							}
						}

						if (participantsList.size() == 0) {
							return "not updated";
						}

						for (String i : participantsList) {
							stmt.executeUpdate("insert into tripparticipants(tripId, userId) values ('" + tripID
									+ "', '" + i + "');");
						}
						return "updated";
					} else {
						return "no participants";
					}

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "not updated";
	}

	@Override
	public boolean updateTripList(TripDetails[] tripList) {
		try {
			Statement stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("select idTrip from trip;");
			boolean alreadyExists = rs.next();
			if (alreadyExists == true) {
				stmt.executeUpdate("delete from tripparticipants;");
				stmt.executeUpdate("delete from trip;");
			}
			for (TripDetails item : tripList) {
				int tripID = this.generateTripID();
				stmt.executeUpdate(
						"insert into trip (idtrip, tripName, startingPoint, destination, startDate, endDate) values ('"
								+ tripID + "', '" + item.getTripname() + "', '" + item.getStartingPoint() + "', '"
								+ item.getDestination() + "', '" + item.getStartDate() + "', '" + item.getEndDate()
								+ "');");

				String cred = item.getParticipants();
				if (cred != null) {
					String[] participants = cred.split(",");

					String userId = "";
					for (int index = 0; index < participants.length; index++) {

						userId = participants[index].substring(participants[index].indexOf(":") + 2,
								participants[index].indexOf("}"));

						stmt.executeUpdate("insert into tripparticipants(tripId, userId) values ('" + tripID + "', '"
								+ userId + "');");
					}
				}

			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String updateTripDetails(TripDetails tripDetails) {
		try {
			Statement stmt = conn.createStatement();
			int tripId;

			ResultSet rs = stmt
					.executeQuery("select idTrip from trip where tripName = '" + tripDetails.getTripname() + "';");
			boolean alreadyExists = rs.next();
			if (alreadyExists == true) {
				tripId = Integer.parseInt(rs.getString(1));
				stmt.executeUpdate("delete from tripparticipants where tripId = '" + tripId + "';");
				stmt.executeUpdate("delete from trip where idTrip = '" + tripId + "';");
			}
			tripId = generateTripID();

			stmt.executeUpdate(
					"insert into trip (idtrip, tripName, startingPoint, destination, startDate, endDate) values ('"
							+ tripId + "', '" + tripDetails.getTripname() + "', '" + tripDetails.getStartingPoint()
							+ "', '" + tripDetails.getDestination() + "', '" + tripDetails.getStartDate() + "', '"
							+ tripDetails.getEndDate() + "');");

			String cred = tripDetails.getParticipants();
			if (cred != null) {
				if (cred.contains("{")) {
					String[] participants = cred.split(",");

					String userId = "";
					for (int index = 0; index < participants.length; index++) {

						userId = participants[index].substring(participants[index].indexOf(":") + 2,
								participants[index].indexOf("}"));

						stmt.executeUpdate("insert into tripparticipants(tripId, userId) values ('" + tripId + "', '"
								+ userId + "');");
					}
				} else {
					return "no participants";
				}
			}

			return "modified";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "not modified";
	}

	public boolean deleteTrip(String tripId) {

		try {
			Statement stmt = conn.createStatement();
			int rs = stmt.executeUpdate("delete from tripparticipants where tripId ='" + tripId + "'");
			rs = stmt.executeUpdate("delete from trip where idtrip ='" + tripId + "'");
			if (rs == 1) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean deleteTrips() {

		try {
			Statement stmt = conn.createStatement();
			int rs = stmt.executeUpdate("delete from tripparticipants;");
			rs = stmt.executeUpdate("delete from trip;");
			if (rs == 1) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public int findTrip(String tripName) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from trip where tripName ='" + tripName + "';");
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private int generateTripID() {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select max(idtrip) from trip;");
			rs.next();
			String max = rs.getString(1);
			if (max != null) {
				return Integer.parseInt(max) + 1;
			} else {
				return 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

}

package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;


@DisplayName("Fare calculate test")
public class FareCalculatorServiceTest {


	private static FareCalculatorService fareCalculatorService;

	@Mock
	private Ticket ticket;
	private Boolean reduction = false;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();

	}
	
	@Nested
	@Tag("FareCareTests")
	@DisplayName("Calculate Fare car Test")
	class FareCare {
		
		@Test
		@DisplayName("Calculate the parking price of a car")
		public void calculateFareCar() throws ClassNotFoundException, SQLException {
			// ARRANGE
			Date inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			// ACT
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			fareCalculatorService.calculateFare(ticket, reduction);
			// ASSERT
			assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
		}
		
		@Test
		@DisplayName("calculate Fare Car With Less Than One Hour Parking  Time")
		public void calculateFareCarWithLessThanOneHourParkingTime() throws ClassNotFoundException, SQLException {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																			// parking fare
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			fareCalculatorService.calculateFare(ticket, reduction);
			assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice(), 0.01);
		}
		
		@Test
		@DisplayName("calculate Fare Car With More Than A Day Parking Time")
		public void calculateFareCarWithMoreThanADayParkingTime() throws ClassNotFoundException, SQLException {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (25 * 60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			fareCalculatorService.calculateFare(ticket, reduction);
			assertEquals((25 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice(), 0.01);
		}
		
	}
	
	
	@Nested
	@Tag("FareBikeTests")
	@DisplayName("Calculate Fare bike Test")
	class FareBike {
		
		@Test
		@DisplayName("Calculate the parking price of a bike")
		public void calculateFareBike() throws ClassNotFoundException, SQLException {
			// ARRANGE
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			// ACT
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			fareCalculatorService.calculateFare(ticket, reduction);
			// ASSERT
			assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
		}
		
		@Test
		@DisplayName("calculate Fare Bike With future in time")
		public void calculateFareBikeWithFutureInTime() {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, reduction));
		}
		
		@Test
		@DisplayName("calculate Fare Bike WithLess Than One Hour Parking Time ")
		public void calculateFareBikeWithLessThanOneHourParkingTime() throws ClassNotFoundException, SQLException {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																			// parking fare
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			fareCalculatorService.calculateFare(ticket, reduction);
			assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice(), 0.01);
		}
		
	}	

	
	@Nested
	@Tag("Othercalculations")
	@DisplayName("Other calculations ")
	class Othercalculations {
		
		@Test
		@DisplayName("calculate Fare Unkown Type")
		public void calculateFareUnkownType() {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, reduction));
		}

		@Test
		@DisplayName("Verify that less than 30 minutes parkingtime is free")
		public void verifyLessThan30MinutesParkingTimeFree() throws ClassNotFoundException, SQLException {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			fareCalculatorService.calculateFare(ticket, reduction);

			assertEquals(0, ticket.getPrice());
		}

		@Test
		@DisplayName("applied 5% discount for recurring users")
		public void applied5recurringusers() throws ClassNotFoundException, SQLException {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			reduction=true;
			fareCalculatorService.calculateFare(ticket, reduction);
			assertEquals(1.42, ticket.getPrice());
		}
		
	}

	

}

package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {


	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static String regNumberString;
	private static Ticket ticket;


	@Mock
	private static InputReaderUtil inputReaderUtil;


	@BeforeAll
	private static void setUp() {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumberString = "ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterEach
	private void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}


	@Test
	public void testParkingACar() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		// int parkingPlace = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		parkingService.processIncomingVehicle();
		ticket = ticketDAO.getTicket(regNumberString);
		System.out.println(ticketDAO.getTicket(regNumberString));
		assertEquals(ticket.getVehicleRegNumber(), regNumberString);
		assertEquals(ticket.getParkingSpot().isItAvailable(), false);
	}


	@Test
	public void testParkingLotExit() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumberString);
		parkingService.processIncomingVehicle();
		ticket = ticketDAO.getTicket(regNumberString);
		Thread.sleep(1000);
		parkingService.processExitingVehicle();
		Ticket ticket2 = ticketDAO.getTicket(regNumberString);

		assertNotNull(ticket2.getPrice());
		assertNotNull(ticket2.getOutTime());
		assertEquals(ticket2.getInTime(), ticket.getInTime());
		assertEquals(ticket2.getVehicleRegNumber(), ticket.getVehicleRegNumber());
	}

	@Test
	public void testRecurrentVehicleReductionAvailable() throws Exception {
		//ARRANGE
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumberString);

		//ACT
		parkingService.processIncomingVehicle();
		Thread.sleep(1000);
		parkingService.processExitingVehicle();
		parkingService.processIncomingVehicle();
		Ticket ticket2 = ticketDAO.getTicket(regNumberString);
		boolean test = ticketDAO.applyReduction5p(ticket2);

		//ASSERT
		assertTrue(test);
	}

}

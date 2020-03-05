package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

	private Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();
	
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;

	public boolean saveTicket(Ticket ticket) {
		
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);			
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
			System.out.println("Ticket saved");
			return ps.execute();
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
			// throw new Exception("Error fetching next available slot", ex);
		} finally {
					
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				logger.error("Error close resource PreparedStatement", e);
			}
			
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				logger.error("Error close resource connection", e);
			}
			
		}
		return false;
	}

	public Ticket getTicket(String vehicleRegNumber) {
		
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_TICKET);
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				ticket.setInTime(rs.getTimestamp(4));
				ticket.setOutTime(rs.getTimestamp(5));
				System.out.println("Ticket get");
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Error close resource resultser", e);
			}
			
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				logger.error("Error close resource PreparedStatement", e);
			}
			
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				logger.error("Error close resource connection", e);
			}
			
		}
		return ticket;
	}

	public boolean updateTicket(Ticket ticket) {
		
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			System.out.println("Ticket updated");
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {					
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				logger.error("Error close resource PreparedStatement", e);
			}
			
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				logger.error("Error close resource connection", e);
			}
		}
		return false;
	}

	public boolean verifyVehicleRegnumber(String vehicleRegNumber) {
		
		boolean result = true;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_VEHICULE_);
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = false;
			}
		} catch (Exception ex) {
			logger.error("Error fetching doublon registration numbers", ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Error close resource resultser", e);
			}
			
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				logger.error("Error close resource PreparedStatement", e);
			}
			
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				logger.error("Error close resource connection", e);
			}
			
		}
		return result;
	}

	public boolean applyReduction5p(Ticket ticket) throws ClassNotFoundException, SQLException {
		
		int cpt = 0;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_VEHICULE_REGNUMBER);
			ps.setString(1, ticket.getVehicleRegNumber());
			rs = ps.executeQuery();
			while (rs.next()) {
				cpt ++;
			}
		} catch (Exception ex) {
			logger.error("Error fetching reduction5percent", ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Error close resource resultser", e);
			}
			
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				logger.error("Error close resource PreparedStatement", e);
			}
			
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				logger.error("Error close resource connection", e);
			}
			
		}
		if (cpt > 1)
			return true;
		else
			return false;
	}

	public void setLogger(Logger testlogger) {
		this.logger = testlogger;
	}
}

package edu.ucam.commons;

import edu.ucam.dao.EspacioDAO;
import edu.ucam.dao.MySQLEspacioDAO;
import edu.ucam.dao.MySQLReservas;
import edu.ucam.dao.ReservaDAO;

public class Singleton {
	
	public static EspacioDAO espacioDAO;
	public static ReservaDAO reservaDAO;
	
	public static EspacioDAO getEspacioDAO() {
		if(Singleton.espacioDAO == null) {
			Singleton.espacioDAO = new MySQLEspacioDAO();
		}
		
		return Singleton.espacioDAO;
	}
	
	public static ReservaDAO getReservaDAO() {
		if(Singleton.reservaDAO == null) {
			Singleton.reservaDAO = new MySQLReservas();
		}
		return Singleton.reservaDAO;
	}
}

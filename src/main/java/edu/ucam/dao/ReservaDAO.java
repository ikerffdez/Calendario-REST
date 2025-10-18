package edu.ucam.dao;

import java.sql.SQLException;
import java.util.HashMap;

import edu.ucam.entity.Reserva;

public interface ReservaDAO {
	public void insertarReserva(Reserva reserva) throws SQLException;
    public void eliminarReserva(Reserva reserva) throws SQLException;
    public void editarReserva(Reserva reserva) throws SQLException;
    public HashMap<Integer, Reserva> obtenerListaReserva() throws SQLException;
}

package edu.ucam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import edu.ucam.entity.Reserva;

public class MySQLReservas implements ReservaDAO{
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	@Override
	public void insertarReserva(Reserva reserva) throws SQLException {
		// TODO Auto-generated method stub
		try {
	        // Obtener conexi�n desde el contexto JNDI
	        Context initContext = new InitialContext();
	        Context envContext  = (Context) initContext.lookup("java:/comp/env");
	        DataSource ds = (DataSource) envContext.lookup("jdbc/dad2_77168527P_49305680M");
	        conn = ds.getConnection();

	        // Preparar la consulta SQL con placeholders
	        String sql = "INSERT INTO reservas (id_espacio, fecha_inicio, fecha_fin) VALUES (?, ?, ?)";
	        ps = conn.prepareStatement(sql);
	        ps.setInt(1, reserva.getIdEspacio());
	        ps.setTimestamp(2, Timestamp.valueOf(reserva.getFechaInicio()));
	        ps.setTimestamp(3, Timestamp.valueOf(reserva.getFechaFin()));
	        
	        ps.executeUpdate();

	    } catch (NamingException e) {
	        e.printStackTrace();
	        throw new SQLException("Error al obtener la conexi�n desde JNDI.");
	    } finally {
	        if (ps != null) ps.close();
	        if (conn != null) conn.close();
	    }
	}

	@Override
	public void eliminarReserva(Reserva reserva) throws SQLException {
		// TODO Auto-generated method stub
		try {
	        // Obtener conexion desde el contexto JNDI
	        Context initContext = new InitialContext();
	        Context envContext  = (Context) initContext.lookup("java:/comp/env");
	        DataSource ds = (DataSource) envContext.lookup("jdbc/dad2_77168527P_49305680M");
	        conn = ds.getConnection();

	        // Preparar la consulta SQL con placeholders
	        String sql = "DELETE FROM reservas WHERE id_reserva = ?";
	        ps = conn.prepareStatement(sql);
	        ps.setInt(1, reserva.getIdReserva());
	        
	        
	        ps.executeUpdate();

	    } catch (NamingException e) {
	        e.printStackTrace();
	        throw new SQLException("Error al obtener la conexi�n desde JNDI.");
	    } finally {
	        if (ps != null) ps.close();
	        if (conn != null) conn.close();
	    }
	}

	@Override
	public void editarReserva(Reserva reserva) throws SQLException {
		// TODO Auto-generated method stub
		try {
	        // Obtener conexi�n desde el contexto JNDI
	        Context initContext = new InitialContext();
	        Context envContext  = (Context) initContext.lookup("java:/comp/env");
	        DataSource ds = (DataSource) envContext.lookup("jdbc/dad2_77168527P_49305680M");
	        conn = ds.getConnection();

	        // Preparar la consulta SQL con placeholders
	        String sql = "UPDATE reservas SET fecha_inicio = ?, fecha_fin = ? WHERE id_reserva = ?";
	        ps = conn.prepareStatement(sql);
	        ps.setTimestamp(1, Timestamp.valueOf(reserva.getFechaInicio()));
	        ps.setTimestamp(2, Timestamp.valueOf(reserva.getFechaFin()));
	        ps.setInt(3, reserva.getIdReserva());
	        
	        ps.executeUpdate();

	    } catch (NamingException e) {
	        e.printStackTrace();
	        throw new SQLException("Error al obtener la conexión desde JNDI.");
	    } finally {
	        if (ps != null) ps.close();
	        if (conn != null) conn.close();
	    }
	}

	@Override
	public HashMap<Integer, Reserva> obtenerListaReserva() throws SQLException {
		// TODO Auto-generated method stub
				HashMap<Integer,Reserva> reservas = new HashMap<Integer, Reserva>();

			    try {
			        // Obtener conexi�n desde el contexto JNDI
			        Context initContext = new InitialContext();
			        Context envContext  = (Context) initContext.lookup("java:/comp/env"); //busca los recurso del datasource
			        DataSource ds = (DataSource) envContext.lookup("jdbc/dad2_77168527P_49305680M");//busca la bbdd entre los recursos
			        conn = ds.getConnection();

			        // Sentencia SQL para obtener todos las reservas
			        String sql = "SELECT id_reserva, id_espacio, fecha_inicio, fecha_fin FROM reservas";
			        ps = conn.prepareStatement(sql);
			        rs = ps.executeQuery();

			        // Iterar sobre los resultados y crear la lista de reservas
			        while (rs.next()) {
			        	Reserva reserva = new Reserva();
			        	reserva.setIdReserva(rs.getInt("id_reserva"));
			        	reserva.setIdEspacio(rs.getInt("id_espacio"));
			        	reserva.setFechaInicio(rs.getTimestamp("fecha_inicio").toLocalDateTime());
			        	reserva.setFechaFin(rs.getTimestamp("fecha_fin").toLocalDateTime());
			        	reservas.put(reserva.getIdReserva(), reserva);
			        }

			    } catch (NamingException e) {
			        e.printStackTrace();
			        throw new SQLException("Error al obtener la conexión desde JNDI.");
			    } finally {
			        if (rs != null) rs.close();
			        if (ps != null) ps.close();
			        if (conn != null) conn.close();
			    }

			    return reservas;  // Devuelve la lista de reservas
	}
}

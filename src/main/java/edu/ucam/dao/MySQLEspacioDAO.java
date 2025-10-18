package edu.ucam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import edu.ucam.entity.Espacio;

public class MySQLEspacioDAO implements EspacioDAO{
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	 
	@Override
	public void insertarEspacio(Espacio espacio) throws SQLException {
		// TODO Auto-generated method stub
		try {
	        // Obtener conexión desde el contexto JNDI
	        Context initContext = new InitialContext();
	        Context envContext  = (Context) initContext.lookup("java:/comp/env");
	        DataSource ds = (DataSource) envContext.lookup("jdbc/dad2_77168527P_49305680M");
	        conn = ds.getConnection();

	        // Preparar la consulta SQL con placeholders
	        String sql = "INSERT INTO espacios (ciudad, edificio, planta, numeroPuerta, descripcion) VALUES (?, ?, ?, ?, ?)";
	        ps = conn.prepareStatement(sql);
	        ps.setString(1, espacio.getCiudad());
	        ps.setString(2, espacio.getEdificio());
	        ps.setInt(3, espacio.getPlanta());
	        ps.setInt(4,  espacio.getNumeroPuerta());
	        ps.setString(5, espacio.getDescripcion());
	        
	        
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
	public void eliminarEspacio(Espacio espacio) throws SQLException {
		// TODO Auto-generated method stub
		try {
	        // Obtener conexión desde el contexto JNDI
	        Context initContext = new InitialContext();
	        Context envContext  = (Context) initContext.lookup("java:/comp/env");
	        DataSource ds = (DataSource) envContext.lookup("jdbc/dad2_77168527P_49305680M");
	        conn = ds.getConnection();

	        // Preparar la consulta SQL con placeholders
	        String sql = "DELETE FROM espacios WHERE id = ?";
	        ps = conn.prepareStatement(sql);
	        ps.setInt(1, espacio.getId());
	        
	        
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
	public void editarEspacio(Espacio espacio) throws SQLException {
		// TODO Auto-generated method stub
		try {
	        // Obtener conexión desde el contexto JNDI
	        Context initContext = new InitialContext();
	        Context envContext  = (Context) initContext.lookup("java:/comp/env");
	        DataSource ds = (DataSource) envContext.lookup("jdbc/dad2_77168527P_49305680M");
	        conn = ds.getConnection();

	        // Preparar la consulta SQL con placeholders
	        String sql = "UPDATE espacios SET ciudad = ?, edificio = ?, planta = ?, numeroPuerta = ?, descripcion = ? WHERE id = ?";
	        ps = conn.prepareStatement(sql);
	        ps.setString(1, espacio.getCiudad());
	        ps.setString(2, espacio.getEdificio());
	        ps.setInt(3, espacio.getPlanta());
	        ps.setInt(4, espacio.getNumeroPuerta());
	        ps.setString(5, espacio.getDescripcion());
	        ps.setInt(6, espacio.getId());
	        
	        
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
	public HashMap<Integer, Espacio> obtenerListaEspacios() throws SQLException {
		// TODO Auto-generated method stub
		HashMap<Integer, Espacio> espacios = new HashMap<Integer, Espacio>();

	    try {
	        // Obtener conexión desde el contexto JNDI
	        Context initContext = new InitialContext();
	        Context envContext  = (Context) initContext.lookup("java:/comp/env"); //busca los recurso del datasource
	        DataSource ds = (DataSource) envContext.lookup("jdbc/dad2_77168527P_49305680M");//busca la bbdd entre los recursos
	        conn = ds.getConnection();

	        // Sentencia SQL para obtener todos los usuarios
	        String sql = "SELECT id, ciudad, edificio, planta, numeroPuerta, descripcion FROM espacios";
	        ps = conn.prepareStatement(sql);
	        rs = ps.executeQuery();

	        // Iterar sobre los resultados y crear la lista de usuarios
	        while (rs.next()) {
	        	Espacio espacio = new Espacio();
	        	espacio.setId(rs.getInt("id"));
	        	espacio.setCiudad(rs.getString("ciudad"));
	        	espacio.setEdificio(rs.getString("edificio"));
	        	espacio.setPlanta(rs.getInt("planta"));
	        	espacio.setNumeroPuerta(rs.getInt("numeroPuerta"));
	        	espacio.setDescripcion(rs.getString("descripcion"));
	        	espacios.put(espacio.getId(), espacio);
	        }

	    } catch (NamingException e) {
	        e.printStackTrace();
	        throw new SQLException("Error al obtener la conexión desde JNDI.");
	    } finally {
	        if (rs != null) rs.close();
	        if (ps != null) ps.close();
	        if (conn != null) conn.close();
	    }

	    return espacios;  // Devuelve la lista de usuarios
	}

}

package edu.ucam.dao;

import java.sql.SQLException;
import java.util.HashMap;

import edu.ucam.entity.Espacio;

public interface EspacioDAO {
	public void insertarEspacio(Espacio espacio) throws SQLException;
    public void eliminarEspacio(Espacio espacio) throws SQLException;
    public void editarEspacio(Espacio espacio) throws SQLException;
    public HashMap<Integer, Espacio> obtenerListaEspacios() throws SQLException;
}

package edu.ucam.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import edu.ucam.commons.Singleton;
import edu.ucam.entity.Espacio;
import edu.ucam.entity.Reserva;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Servicio REST para la entidad Espacio.
 * Proporciona endpoints para crear, editar, listar y borrar espacios.
 */
@Path("/espacio")
public class EspacioService {

    /**
     * Endpoint para dar de alta un nuevo espacio.
     * @param incomingData JSON con los datos del nuevo espacio.
     * @return Respuesta con el espacio creado o error si ya existe o si hay problemas en los datos.
     */
    @POST
    @Path("/alta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response altaEspacio(InputStream incomingData) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(incomingData))) {
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
        } catch (Exception e) {
            return Response.status(400).entity("Error leyendo los datos").build();
        }

        try {
            JSONObject json = new JSONObject(sb.toString());
            String ciudad = json.getString("ciudad");
            String edificio = json.getString("edificio");
            int planta = json.getInt("planta");
            int numeroPuerta = json.getInt("numeroPuerta");
            String descripcion = json.getString("descripcion");

            Espacio espacio = new Espacio(ciudad, edificio, planta, numeroPuerta, descripcion);

            // Verificar si ya existe un espacio igual
            if (existeEspacio(espacio)) {
                return Response.status(404).entity("Ya existe un espacio con los mismos datos").build();
            } else {
                // Insertar espacio y generar respuesta
                Singleton.getEspacioDAO().insertarEspacio(espacio);
                int id = devuelveId(espacio);
                if (id == -1) {
                    return Response.status(404).entity("Error en la insercción").build();
                }

                JSONObject jsonRespuesta = new JSONObject();
                JSONObject jsonEspacio = new JSONObject();
                jsonEspacio.put("ciudad", espacio.getCiudad());
                jsonEspacio.put("edificio", espacio.getEdificio());
                jsonEspacio.put("planta", espacio.getPlanta());
                jsonEspacio.put("numeroPuerta", espacio.getNumeroPuerta());
                jsonEspacio.put("descripcion", espacio.getDescripcion());
                jsonEspacio.put("id", id);
                jsonRespuesta.put("espacio", jsonEspacio);

                return Response.status(201).entity(jsonRespuesta.toString()).build();
            }
        } catch (Exception e) {
            return Response.status(400).entity("Error en los datos enviados: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint para editar un espacio existente.
     * @param incomingData JSON con los nuevos datos del espacio.
     * @return Respuesta con el espacio actualizado o error si no se encuentra o ya existe otro igual.
     */
    @PUT
    @Path("/editar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editarEspacio(InputStream incomingData) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(incomingData))) {
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
        } catch (Exception e) {
            return Response.status(400).entity("Error leyendo los datos").build();
        }

        try {
            HashMap<Integer, Espacio> espacios = (HashMap<Integer, Espacio>) Singleton.getEspacioDAO().obtenerListaEspacios();
            JSONObject json = new JSONObject(sb.toString());
            int id = json.getInt("id");

            if (!espacios.containsKey(id))
                return Response.status(404).entity("Espacio no encontrado").build();

            Espacio espacio = espacios.get(id);
            espacio.setCiudad(json.getString("ciudad"));
            espacio.setEdificio(json.getString("edificio"));
            espacio.setPlanta(json.getInt("planta"));
            espacio.setNumeroPuerta(json.getInt("numeroPuerta"));
            espacio.setDescripcion(json.getString("descripcion"));

            // Verificar si ya existe otro espacio igual
            if (existeEspacio(espacio)) {
                return Response.status(404).entity("Ya existe un espacio con los mismos datos").build();
            } else {
                Singleton.getEspacioDAO().editarEspacio(espacio);

                JSONObject jsonRespuesta = new JSONObject();
                JSONObject jsonEspacio = new JSONObject();
                jsonEspacio.put("id", espacio.getId());
                jsonEspacio.put("ciudad", espacio.getCiudad());
                jsonEspacio.put("edificio", espacio.getEdificio());
                jsonEspacio.put("planta", espacio.getPlanta());
                jsonEspacio.put("numeroPuerta", espacio.getNumeroPuerta());
                jsonEspacio.put("descripcion", espacio.getDescripcion());
                jsonRespuesta.put("espacio", jsonEspacio);

                return Response.status(200).entity(jsonRespuesta.toString()).build();
            }
        } catch (Exception e) {
            return Response.status(400).entity("Error en los datos enviados: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint para obtener una lista de todos los espacios.
     * @return Respuesta con un JSON que contiene todos los espacios ordenados por ciudad.
     */
    @GET
    @Path("/todos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarEspacios() {
        JSONObject respuesta = new JSONObject();
        try {
            HashMap<Integer, Espacio> espacios = (HashMap<Integer, Espacio>) Singleton.getEspacioDAO().obtenerListaEspacios();
            List<Espacio> lista = new ArrayList<>(espacios.values());
            lista.sort(Comparator.comparing(Espacio::getCiudad)); // Ordenar por ciudad

            for (Espacio e : lista) {
                JSONObject jsonEspacio = new JSONObject();
                jsonEspacio.put("id", e.getId());
                jsonEspacio.put("ciudad", e.getCiudad());
                jsonEspacio.put("edificio", e.getEdificio());
                jsonEspacio.put("planta", e.getPlanta());
                jsonEspacio.put("numeroPuerta", e.getNumeroPuerta());
                jsonEspacio.put("descripcion", e.getDescripcion());
                respuesta.append("espacios", jsonEspacio);
            }

            return Response.status(200).entity(respuesta.toString()).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(404).entity("Error al recuperar los espacios").build();
        }
    }

    /**
     * Endpoint para eliminar un espacio.
     * Si tiene reservas asociadas, también las elimina.
     * @param id ID del espacio a eliminar.
     * @return Respuesta indicando si se ha borrado correctamente o si no se encuentra.
     */
    @DELETE
    @Path("/borra/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response borrarEspacio(@PathParam("id") int id) {
        try {
            HashMap<Integer, Espacio> espacios = (HashMap<Integer, Espacio>) Singleton.getEspacioDAO().obtenerListaEspacios();
            if (espacios.containsKey(id)) {
                // Eliminar reservas asociadas
                HashMap<Integer, Reserva> reservas = (HashMap<Integer, Reserva>) Singleton.getReservaDAO().obtenerListaReserva();
                for (Reserva r : reservas.values()) {
                    if (r.getIdEspacio() == id) {
                        Singleton.getReservaDAO().eliminarReserva(r);
                    }
                }
                // Eliminar espacio
                Singleton.getEspacioDAO().eliminarEspacio(espacios.get(id));
                return Response.status(200).entity("Espacio borrado").build();
            } else {
                return Response.status(404).entity("Espacio no encontrado").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(404).entity("Error al recuperar los espacios").build();
        }
    }

    /**
     * Método auxiliar para comprobar si ya existe un espacio con los mismos datos.
     * Compara ciudad, edificio, planta y número de puerta.
     * @param espacio Espacio a comprobar.
     * @return true si ya existe un espacio igual, false en caso contrario.
     */
    private boolean existeEspacio(Espacio espacio) {
        try {
            HashMap<Integer, Espacio> espacios = (HashMap<Integer, Espacio>) Singleton.getEspacioDAO().obtenerListaEspacios();
            for (Espacio e : espacios.values()) {
                if (e.getId() != espacio.getId()) {
                    if (e.getCiudad().equals(espacio.getCiudad()) &&
                        e.getEdificio().equals(espacio.getEdificio()) &&
                        e.getNumeroPuerta() == espacio.getNumeroPuerta() &&
                        e.getPlanta() == espacio.getPlanta()) {
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Si hay error, asumimos que ya existe para prevenir duplicados
        }
    }

    /**
     * Método auxiliar para recuperar el ID de un espacio recién insertado.
     * @param espacio Espacio a buscar.
     * @return ID del espacio si se encuentra, -1 si no.
     */
    private int devuelveId(Espacio espacio) {
        try {
            HashMap<Integer, Espacio> espacios = (HashMap<Integer, Espacio>) Singleton.getEspacioDAO().obtenerListaEspacios();
            for (Espacio e : espacios.values()) {
                if (e.getId() != espacio.getId()) {
                    if (e.getCiudad().equals(espacio.getCiudad()) &&
                        e.getEdificio().equals(espacio.getEdificio()) &&
                        e.getNumeroPuerta() == espacio.getNumeroPuerta() &&
                        e.getPlanta() == espacio.getPlanta() &&
                        e.getDescripcion().equals(espacio.getDescripcion())) {
                        return e.getId();
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}

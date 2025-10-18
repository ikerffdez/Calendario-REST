package edu.ucam.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import edu.ucam.commons.Singleton;
import edu.ucam.entity.Reserva;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Servicio REST para gestionar reservas de espacios.
 * Permite operaciones CRUD sobre la entidad Reserva.
 */
@Path("/reserva")
public class ReservaService {

    // Formatter para parsear fechas entrantes en formato ISO 8601, ej: 2025-06-15T17:11:00
    private static final DateTimeFormatter parseFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    // Formatter para formatear fechas de salida en formato más amigable, ej: 15/06/2025 17:11
    private static final DateTimeFormatter printFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @POST
    @Path("/alta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response altaReserva(InputStream incomingData) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(incomingData))) {
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            return Response.status(400).entity("Error al leer los datos").build();
        }

        try {
            JSONObject jsonRecibido = new JSONObject(sb.toString());

            int idEspacio = jsonRecibido.getInt("idEspacio");
            LocalDateTime fechaInicio = LocalDateTime.parse(jsonRecibido.getString("fechaInicio"), parseFormatter);
            LocalDateTime fechaFin = LocalDateTime.parse(jsonRecibido.getString("fechaFin"), parseFormatter);

            if (fechaInicio.isAfter(fechaFin) || fechaInicio.equals(fechaFin)) {
                return Response.status(400).entity("La fecha de inicio no puede ser posterior o igual a la fecha de fin").build();
            }

            Reserva reserva = new Reserva(idEspacio, fechaInicio, fechaFin);
            HashMap<Integer, Reserva> reservas = (HashMap<Integer, Reserva>) Singleton.getReservaDAO().obtenerListaReserva();

            for (Reserva r : reservas.values()) {
                if (r.getIdEspacio() == idEspacio &&
                   ((fechaInicio.isAfter(r.getFechaInicio()) && fechaInicio.isBefore(r.getFechaFin())) ||
                    (fechaFin.isAfter(r.getFechaInicio()) && fechaFin.isBefore(r.getFechaFin())) ||
                    (fechaInicio.isBefore(r.getFechaInicio()) && fechaFin.isAfter(r.getFechaFin())) ||
                    (fechaInicio.isEqual(r.getFechaInicio()) || fechaFin.isEqual(r.getFechaFin())))) {
                    return Response.status(409).entity("Conflicto de horarios con otra reserva").build();
                }
            }

            Singleton.getReservaDAO().insertarReserva(reserva);
            int id = devuelveId(reserva);
            if(id == -1) {
                return Response.status(404).entity("Error en la inserción").build();
            }

            JSONObject jsonReserva = new JSONObject();
            jsonReserva.put("idReserva", id);
            jsonReserva.put("idEspacio", reserva.getIdEspacio());
            jsonReserva.put("fechaInicio", reserva.getFechaInicio().format(printFormatter));
            jsonReserva.put("fechaFin", reserva.getFechaFin().format(printFormatter));

            JSONObject jsonRespuesta = new JSONObject();
            jsonRespuesta.put("reserva", jsonReserva);

            return Response.status(201).entity(jsonRespuesta.toString()).build();

        } catch (Exception e) {
            return Response.status(400).entity("Error en los datos enviados: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/editar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editarReserva(InputStream incomingData) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(incomingData))) {
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            return Response.status(400).entity("Error al leer los datos").build();
        }

        try {
            JSONObject jsonRecibido = new JSONObject(sb.toString());
            int idReserva = jsonRecibido.getInt("idReserva");

            HashMap<Integer, Reserva> reservas = (HashMap<Integer, Reserva>) Singleton.getReservaDAO().obtenerListaReserva();

            if (!reservas.containsKey(idReserva)) {
                return Response.status(404).entity("Reserva no encontrada").build();
            }

            Reserva reserva = reservas.get(idReserva);
            LocalDateTime fechaInicio = LocalDateTime.parse(jsonRecibido.getString("fechaInicio"), parseFormatter);
            LocalDateTime fechaFin = LocalDateTime.parse(jsonRecibido.getString("fechaFin"), parseFormatter);

            if (fechaInicio.isAfter(fechaFin) || fechaInicio.equals(fechaFin)) {
                return Response.status(400).entity("La fecha de inicio no puede ser posterior o igual a la fecha de fin").build();
            }

            for (Reserva otra : reservas.values()) {
                if (otra.getIdReserva() != idReserva && otra.getIdEspacio() == reserva.getIdEspacio() &&
                   ((fechaInicio.isAfter(otra.getFechaInicio()) && fechaInicio.isBefore(otra.getFechaFin())) ||
                    (fechaFin.isAfter(otra.getFechaInicio()) && fechaFin.isBefore(otra.getFechaFin())) ||
                    (fechaInicio.isBefore(otra.getFechaInicio()) && fechaFin.isAfter(otra.getFechaFin())) ||
                    (fechaInicio.isEqual(otra.getFechaInicio()) || fechaFin.isEqual(otra.getFechaFin())))) {
                    return Response.status(409).entity("Conflicto de horarios con otra reserva").build();
                }
            }

            reserva.setFechaInicio(fechaInicio);
            reserva.setFechaFin(fechaFin);
            Singleton.getReservaDAO().editarReserva(reserva);

            JSONObject jsonReserva = new JSONObject();
            jsonReserva.put("idReserva", reserva.getIdReserva());
            jsonReserva.put("idEspacio", reserva.getIdEspacio());
            jsonReserva.put("fechaInicio", reserva.getFechaInicio().format(printFormatter));
            jsonReserva.put("fechaFin", reserva.getFechaFin().format(printFormatter));

            JSONObject jsonRespuesta = new JSONObject();
            jsonRespuesta.put("reserva", jsonReserva);

            return Response.status(200).entity(jsonRespuesta.toString()).build();

        } catch (Exception e) {
            return Response.status(400).entity("Error en los datos enviados: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/todos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarReservas() {
        try {
            HashMap<Integer, Reserva> reservas = (HashMap<Integer, Reserva>) Singleton.getReservaDAO().obtenerListaReserva();

            List<Reserva> lista = new ArrayList<>(reservas.values());
            lista.sort(Comparator.comparing(Reserva::getFechaInicio));

            JSONObject jsonRespuesta = new JSONObject();
            for (Reserva r : lista) {
                JSONObject jsonReserva = new JSONObject();
                jsonReserva.put("idReserva", r.getIdReserva());
                jsonReserva.put("idEspacio", r.getIdEspacio());
                jsonReserva.put("fechaInicio", r.getFechaInicio().format(printFormatter));
                jsonReserva.put("fechaFin", r.getFechaFin().format(printFormatter));
                jsonRespuesta.append("reservas", jsonReserva);
            }

            return Response.status(200).entity(jsonRespuesta.toString()).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(404).entity("Error al recuperar la lista de reservas").build();
        }
    }

    @DELETE
    @Path("/borra/{idReserva}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response borrarReserva(@PathParam("idReserva") int idReserva) {
        try {
            HashMap<Integer, Reserva> reservas = (HashMap<Integer, Reserva>) Singleton.getReservaDAO().obtenerListaReserva();
            if (reservas.containsKey(idReserva)) {
                Singleton.getReservaDAO().eliminarReserva(reservas.get(idReserva));
                return Response.status(200).entity("Reserva borrada").build();
            } else {
                return Response.status(404).entity("Reserva no encontrada").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(404).entity("Error al recuperar la lista de reservas").build();
        }
    }

    private int devuelveId(Reserva reserva) {
        try {
            HashMap<Integer, Reserva> reservas = (HashMap<Integer, Reserva>) Singleton.getReservaDAO().obtenerListaReserva();
            for (Reserva r : reservas.values()) {
                if (r.getIdReserva() != reserva.getIdReserva()) {
                    if (r.getIdEspacio() == reserva.getIdEspacio() &&
                        r.getFechaInicio().equals(reserva.getFechaInicio()) &&
                        r.getFechaFin().equals(reserva.getFechaFin())) {
                        return r.getIdReserva();
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

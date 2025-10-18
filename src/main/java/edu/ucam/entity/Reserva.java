package edu.ucam.entity;

import java.time.LocalDateTime;

public class Reserva {
	private int idReserva;
	private int idEspacio;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	
	public Reserva() {
		
	}

	public Reserva(int idReserva, int idEspacio, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
		super();
		this.idReserva = idReserva;
		this.idEspacio = idEspacio;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public Reserva(int idEspacio, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
		super();
		this.idEspacio = idEspacio;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public int getIdReserva() {
		return idReserva;
	}

	public void setIdReserva(int idReserva) {
		this.idReserva = idReserva;
	}

	public int getIdEspacio() {
		return idEspacio;
	}

	public void setIdEspacio(int idEspacio) {
		this.idEspacio = idEspacio;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}
	
	
}

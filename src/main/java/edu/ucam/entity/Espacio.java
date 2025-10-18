package edu.ucam.entity;

public class Espacio {
	private int id;
    private String ciudad;
    private String edificio;
    private int planta;
    private int numeroPuerta;
    private String descripcion;
    
	public Espacio(int id, String ciudad, String edificio, int planta, int numeroPuerta, String descripcion) {
		super();
		this.id = id;
		this.ciudad = ciudad;
		this.edificio = edificio;
		this.planta = planta;
		this.numeroPuerta = numeroPuerta;
		this.descripcion = descripcion;
	}

	public Espacio(String ciudad, String edificio, int planta, int numeroPuerta, String descripcion) {
		super();
		this.ciudad = ciudad;
		this.edificio = edificio;
		this.planta = planta;
		this.numeroPuerta = numeroPuerta;
		this.descripcion = descripcion;
	}

	public Espacio() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getEdificio() {
		return edificio;
	}

	public void setEdificio(String edificio) {
		this.edificio = edificio;
	}

	public int getPlanta() {
		return planta;
	}

	public void setPlanta(int planta) {
		this.planta = planta;
	}

	public int getNumeroPuerta() {
		return numeroPuerta;
	}

	public void setNumeroPuerta(int numeroPuerta) {
		this.numeroPuerta = numeroPuerta;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
    
	
}
<!DOCTYPE html>
<%@page import="edu.ucam.commons.Singleton"%>
<%@page import="edu.ucam.entity.Espacio"%>
<%@page import="java.util.HashMap"%>
<html>
<head>
	<meta charset="UTF-8">
	<title>Gestión de Reservas</title>
	<script type="text/javascript" src="js/jquery-3.7.1.js"></script>
	<script type="text/javascript">
		function loadReserva(id, idEspacio, inicio, fin){
			var entry = document.createElement('li');

			//BORRAR
			var aBorrar = document.createElement('a');
			aBorrar.appendChild(document.createTextNode(" [Borrar]"));
			aBorrar.style.cursor = "pointer";
			// Cambio al pasar el ratón
			aBorrar.onmouseover = function() {
				aBorrar.style.color = "blue";
				aBorrar.style.textDecoration = "underline";
			};

			// Vuelve al color original al salir el ratón
			aBorrar.onmouseout = function() {
				aBorrar.style.color = "black";
				aBorrar.style.textDecoration = "none";
			};
			aBorrar.onclick = function () {
			    if (confirm("¿Estás seguro de que quieres borrar esta reserva?")) {
			        $.ajax({
			            url: 'rest/reserva/borra/' + id,
			            type: 'DELETE',
			            success: function() { document.getElementById(id).remove(); },
			            error: function() { alert('Error al borrar'); }
			        });
			    }
			};

			//EDITAR
			var aEditar = document.createElement('a');
			aEditar.appendChild(document.createTextNode(" [Editar]"));
			aEditar.style.cursor = "pointer";
			// Cambio al pasar el ratón
			aEditar.onmouseover = function() {
				aEditar.style.color = "blue";
				aEditar.style.textDecoration = "underline";
			};

			// Vuelve al color original al salir el ratón
			aEditar.onmouseout = function() {
				aEditar.style.color = "black";
				aEditar.style.textDecoration = "none";
			};
			aEditar.onclick = function () {
				$("#idReserva").val(id);
				$("#idEspacio").val(idEspacio);
				$("#idEspacio").prop("disabled", true);
				$("#fechaInicio").val(inicio);
				$("#fechaFin").val(fin);
			};
			
			entry.id = id;
			entry.innerHTML = "(" + id + ") <strong>Espacio:</strong> " + idEspacio + " <strong>| De:</strong> <u>" + inicio + "</u> <strong>a</strong> <u>" + fin + "</u>";
			entry.appendChild(document.createTextNode(" "));
			entry.appendChild(aEditar);
			entry.appendChild(document.createTextNode(" "));
			entry.appendChild(aBorrar);

			$('#reservas').append(entry);
		}

		function limpiarFormulario(){
			$("#idReserva").val("");
			$("#idEspacio").val("");
			$("#idEspacio").prop("disabled", false);
			$("#fechaInicio").val("");
			$("#fechaFin").val("");
		}
		
		function listarReservas(){
			$.ajax({
				url: 'rest/reserva/todos',
				type: 'GET',
				dataType: 'json',
				success: function(result){
					var reservas = result.reservas;
					reservas.forEach(function(reserva){
						loadReserva(reserva.idReserva, reserva.idEspacio, reserva.fechaInicio, reserva.fechaFin);
					});
				},
				error: function(){ alert('Error al cargar reservas'); }
			});
		}

		$(document).ready(function(){
			listarReservas();
			$("#sendButton").click(function(){
				
				var reserva = {
					idEspacio: parseInt($('#idEspacio').val()),
					fechaInicio: $('#fechaInicio').val(),
					fechaFin: $('#fechaFin').val()
				};

				var id = $("#idReserva").val();

				if(id === ""){
					$.ajax({
						url: 'rest/reserva/alta',
						type: 'POST',
						headers: {
							'Accept': 'application/json',
							'Content-Type': 'application/json'
						},
						data: JSON.stringify(reserva),
						success: function(result){
							loadReserva(result.reserva.idReserva, result.reserva.idEspacio, result.reserva.fechaInicio, result.reserva.fechaFin);
							limpiarFormulario();
							alert('Reserva creada');
						},
						error: function(xhr){ alert('Error al crear reserva: '+ xhr.responseText); }
					});
				}else {
					reserva.idReserva = parseInt(id);
					$.ajax({
						url: 'rest/reserva/editar',
						type: 'PUT',
						headers: {
							'Accept': 'application/json',
							'Content-Type': 'application/json'
						},
						data: JSON.stringify(reserva),
						success: function(result){
							$("#reservas").empty();//vaciamos la lista para no duplicar el nuevo editado
							listarReservas();
							limpiarFormulario();
							alert('Reserva editada');
						},
						error: function(xhr){ alert('Error al editar reserva: '+xhr.responseText); }
					});
				}
			});

			$("#limpiar").click(function(){ limpiarFormulario(); });
		});
	</script>
</head>
<body>
	<h2 style="display: flex; justify-content: space-between; align-items: center;">
	Gestión de reservas
	<button type="button" onclick="location.href='index.jsp'" style="font-size:16px; color: white; background: blue; padding: 5px;">Volver al panel</button>
	</h2>
	<form>
		<input type="hidden" id="idReserva">
		ID Espacio:
		<select id="idEspacio">
			    <%
			    	HashMap<Integer, Espacio> espacios = (HashMap<Integer, Espacio>)Singleton.getEspacioDAO().obtenerListaEspacios();
			        for (Integer idEspacio : espacios.keySet()) {
			            Espacio espacio = espacios.get(idEspacio);
			    %>
			        <option value="<%= espacio.getId() %>">
			            <%= espacio.getCiudad() %> - <%= espacio.getEdificio() %> - Planta <%= espacio.getPlanta() %>, Puerta <%= espacio.getNumeroPuerta() %>
			        </option>
			    <%
			        }
			    %>
		</select><br>
		Fecha Inicio: <input type="datetime-local" id="fechaInicio" required><br>
		Fecha Fin: <input type="datetime-local" id="fechaFin" required><br>
		<button type="button" id="sendButton">Guardar</button>
		<button type="button" id="limpiar">Limpiar</button>
	</form>
	<br>
	<ul id="reservas"></ul>
</body>
</html>

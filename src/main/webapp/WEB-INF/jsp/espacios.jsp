<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Gestión de Espacios</title>
	<script type="text/javascript" src="js/jquery-3.7.1.js"></script>
	<script type="text/javascript">
		function loadEspacio(id, ciudad, edificio, planta, numeroPuerta, descripcion){
			var entry = document.createElement('li');
			var aBorrar = document.createElement('a');
			aBorrar.appendChild(document.createTextNode("[Borrar]"));
			aBorrar.style.cursor = "pointer";
			// Cambio al pasar el ratón
			aBorrar.onmouseover = function() {
			  aBorrar.style.color = "blue";
			};

			// Vuelve al color original al salir el ratón
			aBorrar.onmouseout = function() {
			  aBorrar.style.color = "black";
			};
			aBorrar.onclick = function () {
				if (confirm("¿Estás seguro de que quieres borrar este espacio?")) {
					$.ajax({
						url: 'rest/espacio/borra/' + id,
						type: 'DELETE',
						success: function() {
							document.getElementById(id).remove();
						},
						error: function() {
							alert('Error al borrar');
						}
					});
				}
			};

			var aEditar = document.createElement('a');
			aEditar.appendChild(document.createTextNode("[Editar]"));
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
				$("#id").val(id);
				$("#ciudad").val(ciudad);
				$("#edificio").val(edificio);
				$("#planta").val(planta);
				$("#numeroPuerta").val(numeroPuerta);
				$("#descripcion").val(descripcion);
			};

			entry.id = id;
			entry.innerHTML = "(" + id + ") <strong>Ciudad:</strong> " + ciudad + ", <strong>Edificio:</strong> " + edificio + ", <strong>Planta</strong> " + planta + ", <strong>Puerta</strong> " + numeroPuerta + " - " + descripcion;
			entry.appendChild(document.createTextNode(" "));
			entry.appendChild(aEditar);
			entry.appendChild(document.createTextNode(" "));
			entry.appendChild(aBorrar);

			$('#espacios').append(entry);
		}

		function limpiarFormulario(){
			$("#id").val("");
			$("#ciudad").val("");
			$("#edificio").val("");
			$("#planta").val("");
			$("#numeroPuerta").val("");
			$("#descripcion").val("");
		}
		
		function listarEspacios(){
			$.ajax({
				url: 'rest/espacio/todos',
				type: 'GET',
				dataType: 'json',
				success: function(result){
					var espacios = result.espacios;
					espacios.forEach(function(espacio){
						loadEspacio(espacio.id, espacio.ciudad, espacio.edificio, espacio.planta, espacio.numeroPuerta, espacio.descripcion);
					});
				},
				error: function(){ alert('Error al cargar espacios'); }
			});
		}

		$(document).ready(function(){
			listarEspacios();
			$("#sendButton").click(function(){
				if (!$('#ciudad').val() || !$('#edificio').val() || !$('#planta').val() || !$('#numeroPuerta').val() || !$('#descripcion').val()) {
				    alert("Por favor, completa todos los campos");
				    return;
				}

				var espacio = {
					ciudad: $('#ciudad').val(),
					edificio: $('#edificio').val(),
					planta: parseInt($('#planta').val()),
					numeroPuerta: parseInt($('#numeroPuerta').val()),
					descripcion: $('#descripcion').val()
				};

				var id = $("#id").val();

				if(id === ""){
					$.ajax({
						url: 'rest/espacio/alta',
						type: 'POST',
						headers: {
							'Accept': 'application/json',
							'Content-Type': 'application/json'
						},
						data: JSON.stringify(espacio),
						success: function(result){
							loadEspacio(result.espacio.id, result.espacio.ciudad, result.espacio.edificio, result.espacio.planta, result.espacio.numeroPuerta, result.espacio.descripcion);
							limpiarFormulario();
							alert('Espacio creado');
						},
						error: function(xhr){ alert('Error al crear espacio: '+xhr.responseText); }
					});
				} else {
					espacio.id = parseInt(id);
					$.ajax({
						url: 'rest/espacio/editar',
						type: 'PUT',
						headers: {
							'Accept': 'application/json',
							'Content-Type': 'application/json'
						},
						data: JSON.stringify(espacio),
						success: function(result){
							$("#espacios").empty();//vaciamos la lista de espacios
							listarEspacios();
							limpiarFormulario();
							alert('Espacio editado');
						},
						error: function(xhr){ alert('Error al editar espacio: '+xhr.responseText); }
					});
				}
			});

			$("#limpiar").click(function(){ limpiarFormulario(); });
		});
	</script>
</head>
<body>
	<h2 style="display: flex; justify-content: space-between; align-items: center;">
    Gestión de Espacios
    <button type="button" onclick="location.href='index.jsp'" style="font-size:16px; color: white; background: blue; padding: 5px;">Volver al panel</button>
	</h2>
	<form>
		<input type="hidden" id="id">
		Ciudad: <input type="text" id="ciudad" required><br>
		Edificio: <input type="text" id="edificio" required><br>
		Planta: <input type="number" id="planta" required><br>
		Número Puerta: <input type="number" id="numeroPuerta" required><br>
		Descripción: <input type="text" id="descripcion" required><br>
		<button type="button" id="sendButton">Guardar</button>
		<button type="button" id="limpiar">Limpiar</button>
	</form>
	<br><br>
	<ul id="espacios"></ul>
</body>
</html>

package app

import model.*
import ui.IEntradaSalida
import service.IServSeguros
import service.IServUsuarios
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Clase encargada de gestionar el flujo de menús y opciones de la aplicación,
 * mostrando las acciones disponibles según el perfil del usuario autenticado.
 *
 * @property nombreUsuario Nombre del usuario que ha iniciado sesión.
 * @property perfilUsuario Perfil del usuario: admin, gestion o consulta.
 * @property ui Interfaz de usuario.
 * @property gestorUsuarios Servicio de operaciones sobre usuarios.
 * @property gestorSeguros Servicio de operaciones sobre seguros.
 */
class GestorMenu(
    private val nombreUsuario: String,
    private val perfilUsuario: String,
    private val ui: IEntradaSalida,
    private val gestorUsuarios: IServUsuarios,
    private val gestorSeguros: IServSeguros)
{

    /**
     * Inicia un menú según el índice correspondiente al perfil actual.
     *
     * @param indice Índice del menú que se desea mostrar (0 = principal).
     */
    fun iniciarMenu(indice: Int = 0) {
        val (opciones, acciones) = ConfiguracionesApp.obtenerMenuYAcciones(perfilUsuario, indice)
        ejecutarMenu(opciones, acciones)
    }

    /**
     * Formatea el menú en forma numerada.
     */
    private fun formatearMenu(opciones: List<String>): String {
        var cadena = ""
        opciones.forEachIndexed { index, opcion ->
            cadena += "${index + 1}. $opcion\n"
        }
        return cadena
    }

    /**
     * Muestra el menú limpiando pantalla y mostrando las opciones numeradas.
     */
    private fun mostrarMenu(opciones: List<String>) {
        ui.limpiarPantalla()
        ui.mostrar(formatearMenu(opciones), salto = false)
    }

    /**
     * Ejecuta el menú interactivo.
     *
     * @param opciones Lista de opciones que se mostrarán al usuario.
     * @param ejecutar Mapa de funciones por número de opción.
     */
    private fun ejecutarMenu(opciones: List<String>, ejecutar: Map<Int, (GestorMenu) -> Boolean>) {
        do {
            mostrarMenu(opciones)
            val opcion = ui.pedirInfo("Elige opción > ").toIntOrNull()
            if (opcion != null && opcion in 1..opciones.size) {
                // Buscar en el mapa las acciones a ejecutar en la opción de menú seleccionada
                val accion = ejecutar[opcion]
                // Si la accion ejecutada del menú retorna true, debe salir del menú
                if (accion != null && accion(this)) return
            }
            else {
                ui.mostrarError("Opción no válida!")
            }
        } while (true)
    }

    /** Crea un nuevo usuario solicitando los datos necesarios al usuario */
    fun nuevoUsuario() {

        val nombre = ui.pedirInfo("Ingrese el nombre de usuario: ")
        val clave = ui.pedirInfoOculta("Ingrese la contraseña: ")
        val perfilString = ui.pedirInfo("Ingrese el perfil del usuario (ADMIN, GESTION, CONSULTA): ")

        try {
            val perfil = Perfil.valueOf(perfilString.uppercase())
            val nuevoUsuario = Usuario.crearUsuario(listOf(nombre, clave, perfilString))

            val usuarioCreado = gestorUsuarios.agregarUsuario(nuevoUsuario.nombre, nuevoUsuario.clave, perfil)

            if (usuarioCreado) {
                ui.mostrar("Usuario ${nuevoUsuario.nombre} creado exitosamente.")
            } else {
                ui.mostrarError("Error al crear el usuario.")
            }
        } catch (e: IllegalArgumentException) {
            ui.mostrarError("Perfil inválido. Asegúrese de ingresar uno de los siguientes: ADMIN, GESTION, CONSULTA.")
        }
    }

    /** Elimina un usuario si existe */
    fun eliminarUsuario() {
        val nombre = ui.pedirInfo("Ingrese el nombre del usuario a eliminar: ")
        val usuarioEliminado = gestorUsuarios.eliminarUsuario(nombre)
        if (usuarioEliminado) {
            ui.mostrar("Usuario $nombre eliminado exitosamente.")
        } else {
            ui.mostrarError("No se encontró el usuario o hubo un error al eliminarlo.")
        }
    }

    /** Cambia la contraseña del usuario actual */
    fun cambiarClaveUsuario() {
        val nuevaClave = ui.pedirInfoOculta("Ingrese la nueva contraseña: ")
        val usuario = gestorUsuarios.buscarUsuario(nombreUsuario)

        if (usuario != null) {
            val usuarioClaveCambiada = gestorUsuarios.cambiarClave(usuario, nuevaClave)
            if (usuarioClaveCambiada) {
                ui.mostrar("Contraseña cambiada exitosamente.")
            } else {
                ui.mostrarError("No se pudo cambiar la contraseña.")
            }
        } else {
            ui.mostrarError("Usuario no encontrado.")
        }
    }

    /**
     * Mostrar la lista de usuarios (Todos o filstrados por un perfil)
     */
    fun consultarUsuarios() {
        val opciones = listOf("Ver todos los usuarios", "Ver usuarios por perfil")
        val accion = ui.pedirInfo("Elige una opción: ")

        mostrarMenu(opciones)
        when (accion) {
            "1" -> {
                // Consultar todos los usuarios
                val usuarios = gestorUsuarios.consultarTodos()
                if (usuarios.isEmpty()) {
                    ui.mostrar("No hay usuarios registrados.")
                } else {
                    for (usuario in usuarios) {
                        ui.mostrar(usuario.toString())
                    }
                }
            }
            "2" -> {
                // Consultar usuarios por perfil
                val perfilStr = ui.pedirInfo("Ingrese el perfil (ADMIN, GESTION, CONSULTA): ")
                val perfil = Perfil.valueOf(perfilStr.uppercase())
                val usuarios = gestorUsuarios.consultarPorPerfil(perfil)
                if (usuarios.isEmpty()) {
                    ui.mostrar("No hay usuarios con el perfil $perfil.")
                } else {
                    for (usuario in usuarios) {
                        ui.mostrar(usuario.toString())
                    }
                }
            }
            else -> {
                ui.mostrarError("Opción no válida.")
            }
        }
    }



    /**
     * Solicita al usuario un DNI y verifica que tenga el formato correcto: 8 dígitos seguidos de una letra.
     *
     * @return El DNI introducido en mayúsculas.
     */
    private fun pedirDni(): String {
        var dni: String
        try {
            do {
                dni = ui.pedirInfo("Ingrese el DNI (8 dígitos EN MAYUSCULA seguido de una letra): ").uppercase()
                if (!dni.matches(Regex("\\d{8}[A-Z]"))) {
                    ui.mostrarError("El DNI ingresado no tiene el formato correcto.")
                }
            } while (!dni.matches(Regex("\\d{8}[A-Z]")))
        } catch (e: IllegalArgumentException) {
            ui.mostrarError("Ocurrió un error al procesar el DNI.")
            throw e
        }
        return dni
    }


    /**
     * Solicita al usuario un importe positivo, usado para los seguros.
     *
     * @return El valor introducido como `Double` si es válido.
     */
    private fun pedirImporte(): Double {
        return ui.pedirDouble("Ingrese el importe del seguro: ", "Debe ser un valor positivo.", "Valor para conversion no válido.") {
            it > 0
        }
    }

    fun contratarSeguroHogar() {
        try {
            val dni = pedirDni()
            val importe = pedirImporte()
            val metrosCuadrados = ui.pedirInfo("Ingrese los metros cuadrados de la vivienda: ").toInt()
            val valorContenido = ui.pedirInfo("Ingrese el valor del contenido del hogar: ").toDouble()
            val direccion = ui.pedirInfo("Ingrese la dirección del hogar: ")
            val anioConstruccion = LocalDate.parse(ui.pedirInfo("Ingrese el año de construcción del hogar (dd/MM/yyyy): "), DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            val seguroCreado = gestorSeguros.contratarSeguroHogar(
                dni,
                importe,
                metrosCuadrados,
                valorContenido,
                direccion,
                anioConstruccion
            )

            if (seguroCreado) {
                ui.mostrar("Seguro de hogar contratado con éxito.")
            } else {
                ui.mostrarError("Error al contratar el seguro.")
            }

        } catch (e: NumberFormatException) {
            ui.mostrarError("Formato de número inválido.")
        } catch (e: DateTimeParseException) {
            ui.mostrarError("Formato de fecha inválido.")
        } catch (e: Exception) {
            ui.mostrarError("Ocurrió un error inesperado.")
        }
    }

    /** Crea un nuevo seguro de auto solicitando los datos al usuario */
    fun contratarSeguroAuto() {
        try {
            val dni = pedirDni()
            val importe = pedirImporte()
            val descripcion = ui.pedirInfo("Ingrese la descripción del auto: ")
            val combustible = ui.pedirInfo("Ingrese el tipo de combustible del auto: ")
            val tipoAuto = Auto.getAuto(ui.pedirInfo("Ingrese el tipo de auto (ejemplo: SEDAN, SUV, etc.): "))
            val cobertura = Cobertura.getCobertura(ui.pedirInfo("Ingrese el tipo de cobertura (ejemplo: TERCEROS, TODO RIESGO): "))
            val asistenciaCarretera = ui.pedirInfo("¿Tiene asistencia en carretera? (true/false): ").toBoolean()
            val numPartes = ui.pedirInfo("Ingrese el número de partes realizados por el auto: ").toInt()

            val seguroCreado = gestorSeguros.contratarSeguroAuto(
                dni,
                importe,
                descripcion,
                combustible,
                tipoAuto,
                cobertura,
                asistenciaCarretera,
                numPartes
            )

            if (seguroCreado) {
                ui.mostrar("Seguro de auto contratado con éxito.")
            } else {
                ui.mostrarError("Error al contratar el seguro de auto.")
            }

        } catch (e: NumberFormatException) {
            ui.mostrarError("Formato de número inválido.")
        } catch (e: DateTimeParseException) {
            ui.mostrarError("Formato de fecha inválido.")
        } catch (e: Exception) {
            ui.mostrarError("Ocurrió un error inesperado.")
        }
    }


    /** Crea un nuevo seguro de vida solicitando los datos al usuario */
    fun contratarSeguroVida() {
        try {
            val dni = pedirDni()
            val importe = pedirImporte()
            val fechaNacimiento = LocalDate.parse(ui.pedirInfo("Ingrese la fecha de nacimiento (dd/MM/yyyy): "), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val nivelRiesgo = Riesgo.getRiesgo(ui.pedirInfo("Ingrese el nivel de riesgo (ejemplo: BAJO, MEDIO, ALTO): "))
            val indemnizacion = ui.pedirInfo("Ingrese la indemnización del seguro de vida: ").toDouble()

            val seguroCreado = gestorSeguros.contratarSeguroVida(
                dni,
                importe,
                fechaNacimiento,
                nivelRiesgo,
                indemnizacion
            )

            if (seguroCreado) {
                ui.mostrar("Seguro de vida contratado con éxito.")
            } else {
                ui.mostrarError("Error al contratar el seguro de vida.")
            }

        } catch (e: NumberFormatException) {
            ui.mostrarError("Formato de número inválido.")
        } catch (e: DateTimeParseException) {
            ui.mostrarError("Formato de fecha inválido.")
        } catch (e: Exception) {
            ui.mostrarError("Ocurrió un error inesperado.")
        }
    }


    /** Elimina un seguro si existe por su número de póliza */
    fun eliminarSeguro() {
        try {
            val numPoliza = ui.pedirInfo("Ingrese el número de póliza a eliminar: ").toInt()
            val seguroEliminado = gestorSeguros.eliminarSeguro(numPoliza)
            if (seguroEliminado) {
                ui.mostrar("Seguro eliminado con éxito.")
            } else {
                ui.mostrarError("No se encontró el seguro con el número de póliza: $numPoliza.")
            }
        } catch (e: NumberFormatException) {
            ui.mostrarError("Formato de número inválido.")
        } catch (e: Exception) {
            ui.mostrarError("Ocurrió un error inesperado.")
        }
    }


    /** Muestra todos los seguros existentes */
    fun consultarSeguros() {
        val opciones = listOf("Seguros de hogar", "Seguros de auto", "Seguros de vida")
        mostrarMenu(opciones)
        when (ui.pedirInfo("Elige una opción: ")) {
            "1" -> consultarSegurosPorTipo("Hogar")
            "2" -> consultarSegurosPorTipo("Auto")
            "3" -> consultarSegurosPorTipo("Vida")
            else -> ui.mostrarError("Opción no válida.")
        }
    }

    /**
     * Muestra los seguros de un tipo específico.
     *
     * @param tipoSeguro El tipo de seguro que se quiere consultar (ejemplo: "Hogar", "Auto", "Vida").
     */
    private fun consultarSegurosPorTipo(tipoSeguro: String) {
        try {
            val seguros = gestorSeguros.consultarPorTipo(tipoSeguro)
            if (seguros.isEmpty()) {
                ui.mostrarError("No hay seguros de tipo $tipoSeguro registrados.")
            } else {
                ui.mostrar("Seguros de tipo $tipoSeguro registrados:")
                seguros.forEach { ui.mostrar(it.toString()) }
            }
        } catch (e: Exception) {
            ui.mostrarError("Ocurrió un error al consultar los seguros de tipo $tipoSeguro.")
        }
    }


    /** Muestra todos los seguros de tipo hogar */
    fun consultarSegurosHogar() {
        consultarSegurosPorTipo("Hogar")
    }

    /** Muestra todos los seguros de tipo auto */
    fun consultarSegurosAuto() {
        consultarSegurosPorTipo("Auto")
    }

    /** Muestra todos los seguros de tipo vida */
    fun consultarSegurosVida() {
        consultarSegurosPorTipo("Vida")
    }



}
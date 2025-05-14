package app

import data.ICargarSegurosIniciales
import data.ICargarUsuariosIniciales
import ui.IEntradaSalida

/**
 * Clase encargada de cargar los datos iniciales de usuarios y seguros desde ficheros,
 * necesarios para el funcionamiento del sistema en modo persistente.
 *
 * @param usuarios Repositorio que permite cargar usuarios desde un fichero.
 * @param seguros Repositorio que permite cargar seguros desde un fichero.
 * @param ui Interfaz de entrada/salida para mostrar mensajes de error.
 */
class CargadorInicial(private val usuarios: ICargarUsuariosIniciales, private val seguros: ICargarSegurosIniciales, private val ui: IEntradaSalida) {

    /**
     * Carga los usuarios desde el archivo configurado en el repositorio.
     * Muestra errores si ocurre un problema en la lectura o conversión de datos.
     */
    fun cargarUsuarios() {
        try {
            usuarios.cargarUsuarios()
        } catch (e: IllegalArgumentException) {
            ui.mostrarError(e.message.toString())
        }
    }

    /**
     * Carga los seguros desde el archivo configurado en el repositorio.
     * Utiliza el mapa de funciones de creación definido en la configuración de la aplicación
     * (ConfiguracionesApp.mapaCrearSeguros).
     * Muestra errores si ocurre un problema en la lectura o conversión de datos.
     */
    fun cargarSeguros() {
        try {
            seguros.cargarSeguros(ConfiguracionesApp.mapaCrearSeguros)
        } catch (e: IllegalArgumentException) {
            ui.mostrarError(e.message.toString())
        }
    }
}
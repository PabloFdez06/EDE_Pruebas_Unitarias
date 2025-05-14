package service

import model.*
import java.time.LocalDate

interface IServSeguros {
    fun contratarSeguroHogar(
        dniTitular: String,
        importe: Double,
        metrosCuadrados: Int,
        valorContenido: Double,
        direccion: String,
        anioConstruccion: LocalDate
    ): Boolean

    fun contratarSeguroAuto(
        dniTitular: String,
        importe: Double,
        descripcion: String,
        combustible: String,
        tipoAuto: Auto,
        cobertura: Cobertura,
        asistenciaCarretera: Boolean,
        numPartes: Int
    ): Boolean

    fun contratarSeguroVida(
        dniTitular: String,
        importe: Double,
        fechaNacimiento: LocalDate,
        nivelRiesgo: Riesgo,
        indemnizacion: Double
    ): Boolean

    fun eliminarSeguro(numPoliza: Int): Boolean

    fun consultarTodos(): List<Seguro>

    fun consultarPorTipo(tipoSeguro: String): List<Seguro>
}
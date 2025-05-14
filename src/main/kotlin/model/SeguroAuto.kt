package org.example.model

import model.Auto
import model.Cobertura
import model.Seguro
import model.SeguroHogar
import model.SeguroHogar.Companion
import java.time.LocalDate

class SeguroAuto: Seguro {

    private var descripcion: String
    private var combustible: String
    private var tipoAuto: Auto
    private var cobertura: Cobertura
    private var asistenciaCarretera: Boolean
    private var numPartes: Int


    companion object{
        const val PORCENTAJE_INCREMENTO_PARTES = 2
        var numPolizaAuto = 400000


        fun crearSeguro(datos: List<String>) : SeguroAuto? {
//            return try {
//                if (datos.size < 9) return null
//
//                val numPoliza = datos[0].toInt()
//                val dniTitular = datos[1]
//                val importe = datos[2].toDouble()
//                val descripcion = datos[3]
//                val combustible = datos[4]
//                val tipoAuto = Auto.getAuto(datos[5])
//                val cobertura = Cobertura.getCobertura(datos[6])
//                val asistenciaCarretera = datos[7].toBoolean()
//                val numPartes = datos[8].toInt()
//
//
//                SeguroAuto(numPoliza, dniTitular, importe, descripcion, combustible, tipoAuto, cobertura, asistenciaCarretera, numPartes)
//            } catch (e: Exception) {
//                null
//            }

            return SeguroAuto(
                numPoliza = datos[0].toInt(),
                dniTitular = datos[1],
                importe = datos[2].toDouble(),
                descripcion = datos[3],
                combustible = datos[4],
                tipoAuto = Auto.getAuto(datos[5]),
                cobertura = Cobertura.getCobertura(datos[6]),
                asistenciaCarretera = datos[7].toBoolean(),
                numPartes = datos[8].toInt()
            )
        }
    }

    constructor(dniTitular: String, importe: Double, descripcion: String, combustible: String, tipoAuto: Auto, cobertura: Cobertura, asistenciaCarretera: Boolean, numPartes: Int) :
            super(numPoliza = numPolizaAuto++, dniTitular, importe) {

                this.descripcion = descripcion
                this.combustible = combustible
                this.tipoAuto = tipoAuto
                this.cobertura = cobertura
                this.asistenciaCarretera = asistenciaCarretera
                this.numPartes = numPartes

    }

    private constructor(numPoliza: Int, dniTitular: String, importe: Double, descripcion: String, combustible: String, tipoAuto: Auto, cobertura: Cobertura, asistenciaCarretera: Boolean, numPartes: Int) :
            super(numPoliza, dniTitular, importe) {

        this.descripcion = descripcion
        this.combustible = combustible
        this.tipoAuto = tipoAuto
        this.cobertura = cobertura
        this.asistenciaCarretera = asistenciaCarretera
        this.numPartes = numPartes

    }

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val incrementoPorPartes = numPartes * (PORCENTAJE_INCREMENTO_PARTES / 100.0)
        return importe * (1 + interes + incrementoPorPartes)
    }


    override fun serializar(separador: String): String {
        return super.serializar(separador) + "$descripcion$separador$combustible$separador$tipoAuto$separador$cobertura$separador$asistenciaCarretera$separador$numPartes"
    }

    override fun toString(): String {
        return "SeguroAuto(numPoliza = ${numPoliza}, dniTitular = ${dniTitular}, importe = ${"%.2f".format(super.importe)} descripcion = $descripcion, combustible = $combustible, tipoAuto = $tipoAuto, cobertura = $cobertura, asistenciaCarretera = $asistenciaCarretera, numPartes = $numPartes)"
    }
}

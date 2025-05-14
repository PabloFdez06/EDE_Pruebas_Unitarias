package org.example.model

import model.Auto
import model.Cobertura
import model.Riesgo
import model.Seguro
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SeguroVida : Seguro {

    private var fechaNac: LocalDate
    private var nivelRiesgo: Riesgo
    private var indemnizacion: Double



    companion object{

        var numPolizaVida = 800000



        fun crearSeguro(datos: List<String>) : SeguroVida? {
//            return try {
//                if (datos.size < 6) return null
//
//                val numPoliza = datos[0].toInt()
//                val dniTitular = datos[1]
//                val importe = datos[2].toDouble()
//                val fechaNac = LocalDate.parse(datos[3], DateTimeFormatter.ofPattern("dd/MM/yyyy"))
//                val nivelRiesgo = Riesgo.getRiesgo(datos[4])
//                val indemnizacion = datos[5].toDouble()
//
//                SeguroVida(numPoliza, dniTitular, importe, fechaNac, nivelRiesgo, indemnizacion)
//            } catch (e: Exception) {
//                null
//            }

            return SeguroVida(
                numPoliza = datos[0].toInt(),
                dniTitular = datos[1],
                importe = datos[2].toDouble(),
                fechaNac = LocalDate.parse(datos[3], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                nivelRiesgo = Riesgo.getRiesgo(datos[4]),
                indemnizacion = datos[5].toDouble()
            )
        }
    }

    constructor(dniTitular: String, importe: Double, fechaNac: LocalDate, nivelRiesgo: Riesgo, indemnizacion: Double) :
            super(numPoliza = ++numPolizaVida, dniTitular, importe) {

        this.fechaNac = fechaNac
        this.nivelRiesgo = nivelRiesgo
        this.indemnizacion = indemnizacion


    }

    private constructor(numPoliza: Int, dniTitular: String, importe: Double, fechaNac: LocalDate, nivelRiesgo: Riesgo, indemnizacion: Double) :
            super(numPoliza, dniTitular, importe) {

        this.fechaNac = fechaNac
        this.nivelRiesgo = nivelRiesgo
        this.indemnizacion = indemnizacion

    }

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val edad = LocalDate.now().year - fechaNac.year
        val incrementoPorEdad = edad * 0.05 //

        return importe * (1 + interes + incrementoPorEdad)
    }



    override fun serializar(separador: String): String {
        return super.serializar(separador) + "$fechaNac$separador$nivelRiesgo$separador$indemnizacion"
    }

    override fun toString(): String {
        return "SeguroAuto(numPoliza = ${super.numPoliza}, dniTitular = ${super.dniTitular}, importe = ${"%.2f".format(super.importe)} fechaNac = $fechaNac, nivelRiesgo = $nivelRiesgo, indemnizacion = $indemnizacion)"
    }
}
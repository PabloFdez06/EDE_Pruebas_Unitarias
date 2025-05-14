package model

enum class Cobertura(val desc: String) {
    TERCEROS("Terceros"),
    ERCEROS_AMPLIADO("Terceros+"),
    RANQUICIA_200("Franquicia200"),
    FRANQUICIA_300("Franquicia300"),
    FRANQUICIA_400("Franquicia400"),
    FRANQUICIA_500("Franquicia500"),
    TODO_RIESGO("Todo Riesgo");

    companion object {
        fun getCobertura(valor: String): Cobertura {
            return try {
                valueOf(valor.uppercase())
            } catch (e: IllegalArgumentException) {
                TERCEROS
            }
        }
    }
}
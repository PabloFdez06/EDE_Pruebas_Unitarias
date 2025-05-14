
package model

import utils.Seguridad

/**
 * Clase que representa a un usuario del sistema. Contiene información básica como el nombre, la clave (encriptada)
 * y el perfil del usuario. La clase incluye métodos para verificar y cambiar la clave del usuario, así como para serializar
 * los datos del usuario a un formato específico.
 *
 * @param nombre El nombre del usuario.
 * @param clave La clave del usuario, que será encriptada durante la inicialización.
 * @param perfil El perfil del usuario, que define su nivel de acceso en el sistema.
 */
class Usuario(var nombre: String, clave: String, var perfil: Perfil) : IExportable {

    companion object {

        /**
         * Crea un nuevo usuario a partir de una lista de cadenas. Se valida que la lista contenga 3 elementos:
         * nombre, clave y perfil. Si los datos no son válidos, se lanzan excepciones.
         *
         * @param datos Lista de cadenas con los datos del usuario: [nombre, clave, perfil].
         * @return Un nuevo objeto [Usuario] con los datos proporcionados.
         * @throws IllegalArgumentException Si los datos no son válidos.
         */
        fun crearUsuario(datos: List<String>): Usuario {
            require(datos.size == 3) { "*ERROR* No hay suficientes datos para crear un usuario." }
            require(datos[0].isNotBlank()) { "*ERROR* No has introducido un nombre." }
            require(datos[1].isNotBlank()) { "*ERROR* Error con la clave." }
            require(datos[2].isNotBlank()) { "ERROR Error con el perfil." }
            return Usuario(datos[0], datos[1], Perfil.getPerfil(datos[2]))
        }
    }

    /**
     * La clave del usuario, encriptada utilizando un algoritmo de encriptación seguro.
     * Este valor se establece al crear el usuario y no puede ser modificado directamente.
     */
    var clave: String = Seguridad().encriptarClave(clave)
        private set

    /**
     * Verifica si una clave encriptada proporcionada coincide con la clave encriptada del usuario.
     *
     * @param claveEncriptada La clave encriptada que se desea verificar.
     * @return `true` si la clave encriptada proporcionada coincide con la clave del usuario, `false` en caso contrario.
     */
    fun verificarClave(claveEncriptada: String): Boolean {
        return Seguridad().verificarClave(claveEncriptada, clave)
    }

    /**
     * Cambia la clave del usuario a una nueva clave encriptada.
     *
     * @param nuevaClaveEncriptada La nueva clave encriptada que se establecerá para el usuario.
     */
    fun cambiarClave(nuevaClaveEncriptada: String) {
        this.clave = nuevaClaveEncriptada
    }

    /**
     * Serializa los datos del usuario a una cadena de texto, utilizando el separador proporcionado.
     * La cadena resultante contiene el nombre, la clave y el perfil del usuario.
     *
     * @param separador El carácter o cadena que se utilizará para separar los datos del usuario en la serialización.
     * @return Una cadena de texto con los datos serializados del usuario.
     */
    override fun serializar(separador: String): String {
        return "$nombre$separador$clave$separador$perfil"
    }

    /**
     *
     */
    override fun toString(): String {
        return "Usuario($nombre, $clave, $perfil)"
    }
}

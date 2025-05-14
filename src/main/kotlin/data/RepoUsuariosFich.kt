package org.example.data

import data.ICargarUsuariosIniciales
import data.RepoUsuariosMem
import model.Usuario
import utils.IUtilFicheros

class RepoUsuariosFich(
    private val rutaArchivo: String,
    private val fich: IUtilFicheros
): RepoUsuariosMem(), ICargarUsuariosIniciales {

    override fun agregar(usuario: Usuario): Boolean {
        if (super.agregar(usuario) && fich.agregarLinea(rutaArchivo, usuario.serializar())) {
            return true
        }
        return false
    }

    override fun eliminar(usuario: Usuario): Boolean {
        if (fich.escribirArchivo(rutaArchivo, usuarios.filter { it != usuario })) {
            return super.eliminar(usuario)
        }
        return false
    }

    override fun cargarUsuarios(): Boolean {
        val lineas = fich.leerArchivo(rutaArchivo)

        if (lineas.isNotEmpty()) {
            usuarios.clear()
            for (linea in lineas) {
                val datos = linea.split(";")
                if (datos.size == 3){
                    usuarios.add(Usuario.crearUsuario((datos)))
                }
            }
            return usuarios.isNotEmpty()
        }
        return false
    }
}
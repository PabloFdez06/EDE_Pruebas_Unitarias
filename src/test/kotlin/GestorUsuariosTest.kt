import kotlin.collections.listOf
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.*
import model.Perfil
import model.Usuario
import service.GestorUsuarios
import data.IRepoUsuarios
import java.util.Collections.emptyList

class GestorUsuariosTest : DescribeSpec({

    val mockRepo = mockk<IRepoUsuarios>()
    val servicio = GestorUsuarios(mockRepo)

    val usuarioEjemplo = Usuario("pablo", "pablo", Perfil.ADMIN)

    describe("iniciarSesion") {

        it("debe retornar el perfil correspondiente si la clave es correcta") {
            // Mock para devolver el usuario y verificar clave correcta
            every { mockRepo.buscar("pablo") } returns usuarioEjemplo
            every { usuarioEjemplo.verificarClave("pablo") } returns true

            val resultado = servicio.iniciarSesion("pablo", "pablo")
            resultado shouldBe usuarioEjemplo.perfil
        }

        it("debe retornar null si la clave es incorrecta") {
            // Mock para devolver el usuario y verificar clave incorrecta
            every { mockRepo.buscar("pablo") } returns usuarioEjemplo
            every { usuarioEjemplo.verificarClave("malaclave") } returns false

            val resultado = servicio.iniciarSesion("pablo", "malaclave")
            resultado shouldBe null  // Esperamos null cuando la clave es incorrecta
        }
    }

    describe("agregarUsuario") {

        it("debe retornar true si el usuario se agrega") {
            every { mockRepo.agregar(any()) } returns true

            val resultado = servicio.agregarUsuario("ana", "1234", Perfil.GESTION)
            resultado shouldBe true
        }

        it("debe retornar false si el usuario ya existe") {
            every { mockRepo.agregar(any()) } returns false

            val resultado = servicio.agregarUsuario("ana", "1234", Perfil.GESTION)
            resultado shouldBe false
        }
    }

    describe("eliminarUsuario") {

        it("debe retornar true si el usuario se elimina") {
            every { mockRepo.eliminar("pablo") } returns true

            val resultado = servicio.eliminarUsuario("pablo")
            resultado shouldBe true
        }

        it("debe retornar false si el usuario no existe") {
            every { mockRepo.eliminar("pablo") } returns false

            val resultado = servicio.eliminarUsuario("pablo")
            resultado shouldBe false
        }
    }

    describe("cambiarClave") {

        it("debe retornar true si la clave se cambia") {
            every { mockRepo.cambiarClave(usuarioEjemplo, "nueva") } returns true

            val resultado = servicio.cambiarClave(usuarioEjemplo, "nueva")
            resultado shouldBe true
        }

        it("debe retornar false si falla el cambio") {
            every { mockRepo.cambiarClave(usuarioEjemplo, "nueva") } returns false

            val resultado = servicio.cambiarClave(usuarioEjemplo, "nueva")
            resultado shouldBe false
        }
    }

    describe("buscarUsuario") {

        it("debe retornar usuario si existe") {
            every { mockRepo.buscar("pablo") } returns usuarioEjemplo

            val resultado = servicio.buscarUsuario("pablo")
            resultado shouldBe usuarioEjemplo
        }

        it("debe retornar null si no existe") {
            every { mockRepo.buscar("pablo") } returns null

            val resultado = servicio.buscarUsuario("pablo")
            resultado shouldBe null
        }
    }

    describe("consultarTodos") {

        it("debe retornar lista con usuarios") {
            every { mockRepo.obtenerTodos() } returns listOf(usuarioEjemplo)

            val resultado = servicio.consultarTodos()
            resultado shouldBe listOf(usuarioEjemplo)
        }

        it("debe retornar lista vacía si no hay usuarios") {
            every { mockRepo.obtenerTodos() } returns emptyList()

            val resultado = servicio.consultarTodos()
            resultado shouldBe emptyList()
        }
    }

    describe("consultarPorPerfil") {

        it("debe retornar usuarios con el perfil") {
            every { mockRepo.obtener(Perfil.ADMIN) } returns listOf(usuarioEjemplo)

            val resultado = servicio.consultarPorPerfil(Perfil.ADMIN)
            resultado shouldBe listOf(usuarioEjemplo)
        }

        it("debe retornar lista vacía si no hay usuarios con ese perfil") {
            every { mockRepo.obtener(Perfil.CONSULTA) } returns emptyList()

            val resultado = servicio.consultarPorPerfil(Perfil.CONSULTA)
            resultado shouldBe emptyList()
        }
    }
})
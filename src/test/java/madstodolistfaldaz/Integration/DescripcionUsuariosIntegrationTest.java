package madstodolistfaldaz.Integration;

import madstodolistfaldaz.authentication.ManagerUserSession;
import madstodolistfaldaz.controller.HomeController;
import madstodolistfaldaz.dto.UsuarioData;
import madstodolistfaldaz.model.Usuario;
import madstodolistfaldaz.repository.UsuarioRepository;
import madstodolistfaldaz.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class DescripcionUsuariosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void testDetallesUsuarioExistente() throws Exception {
        // Configuración
        Long userId = 1L;
        Long usuarioDetalleId = 2L;

        UsuarioData usuarioLogueado = new UsuarioData();
        usuarioLogueado.setId(userId);
        usuarioLogueado.setNombre("Usuario Logueado");

        Usuario usuarioDetalle = new Usuario("detalle@test.com");
        usuarioDetalle.setId(usuarioDetalleId);
        usuarioDetalle.setNombre("Usuario Detalle");
        usuarioDetalle.setFechaNacimiento(new Date());

        UsuarioData usuarioDetalleData = new UsuarioData();
        usuarioDetalleData.setId(usuarioDetalleId);
        usuarioDetalleData.setNombre("Usuario Detalle");
        usuarioDetalleData.setEmail("detalle@test.com");
        usuarioDetalleData.setFechaNacimiento(new Date());

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioService.findById(userId)).thenReturn(usuarioLogueado);
        when(usuarioRepository.findById(usuarioDetalleId)).thenReturn(Optional.of(usuarioDetalle));
        when(usuarioService.findById(usuarioDetalleId)).thenReturn(usuarioDetalleData);

        // Ejecución y verificación
        mockMvc.perform(get("/registrados/" + usuarioDetalleId))
                .andExpect(status().isOk())
                .andExpect(view().name("descripcionUsuario"))
                .andExpect(model().attributeExists("usuarioDetalle"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("isAuthenticated"))
                .andExpect(content().string(containsString("Usuario Detalle")))
                .andExpect(content().string(containsString("detalle@test.com")))
                .andExpect(content().string(containsString("Fecha de Nacimiento")))
                .andExpect(content().string(containsString("Volver a la lista")));
    }

    @Test
    public void testDetallesUsuarioNoExistente() throws Exception {
        Long userId = 1L;
        Long usuarioInexistenteId = 999L;

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioRepository.findById(usuarioInexistenteId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/registrados/" + usuarioInexistenteId))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(content().string(containsString("Error al cargar usuario")));
    }

    @Test
    public void testDetallesUsuarioNoAutenticado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/registrados/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
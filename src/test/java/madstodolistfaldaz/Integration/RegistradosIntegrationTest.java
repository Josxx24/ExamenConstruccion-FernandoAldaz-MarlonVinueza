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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class RegistradosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void testListaUsuariosAutenticado() throws Exception {
        // Configuración
        Long userId = 1L;
        UsuarioData usuarioLogueado = new UsuarioData();
        usuarioLogueado.setId(userId);
        usuarioLogueado.setNombre("Usuario Logueado");

        Usuario usuario1 = new Usuario("user1@test.com");
        usuario1.setId(1L);
        usuario1.setNombre("Usuario 1");

        Usuario usuario2 = new Usuario("user2@test.com");
        usuario2.setId(2L);
        usuario2.setNombre("Usuario 2");

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioService.findById(userId)).thenReturn(usuarioLogueado);
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario1, usuario2));

        // Ejecución y verificación
        mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(view().name("registrados"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("isAuthenticated"))
                .andExpect(content().string(containsString("Usuario 1")))
                .andExpect(content().string(containsString("Usuario 2")))
                .andExpect(content().string(containsString("Ver Detalles")));
    }

    @Test
    public void testListaUsuariosNoAutenticado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/registrados"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testListaUsuariosVacia() throws Exception {
        Long userId = 1L;
        UsuarioData usuarioLogueado = new UsuarioData();
        usuarioLogueado.setId(userId);
        usuarioLogueado.setNombre("Usuario Logueado");

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioService.findById(userId)).thenReturn(usuarioLogueado);
        when(usuarioRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(view().name("registrados"))
                .andExpect(content().string(not(containsString("Ver Detalles"))));
    }
}

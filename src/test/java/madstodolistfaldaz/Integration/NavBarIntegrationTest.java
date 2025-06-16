package madstodolistfaldaz.Integration;

import madstodolistfaldaz.authentication.ManagerUserSession;
import madstodolistfaldaz.controller.HomeController;
import madstodolistfaldaz.dto.UsuarioData;
import madstodolistfaldaz.repository.UsuarioRepository;
import madstodolistfaldaz.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class NavBarIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    public void testNavBarForAuthenticatedUser() throws Exception {
        // Arrange
        Long userId = 1L;
        UsuarioData mockUser = new UsuarioData();
        mockUser.setId(userId);
        mockUser.setNombre("Fernando Aldaz");
        mockUser.setEmail("fernando@test.com");

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioService.findById(userId)).thenReturn(mockUser);

        // Act & Assert - Verificar que se muestra la navbar correcta
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tareas"))) // Link de Tareas
                .andExpect(content().string(containsString("Fernando Aldaz"))) // Nombre del usuario
                .andExpect(content().string(containsString("nav-link dropdown-toggle"))) // Link de Cuenta
                .andExpect(content().string(containsString("navbar navbar-expand-lg navbar-dark bg-dark"))) // Fragmento correcto
                .andExpect(model().attribute("isAuthenticated", true));
    }

    @Test
    public void testNavBarForUnauthenticatedUser() throws Exception {
        // Arrange
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // Act & Assert - Verificar que se muestra la navbar de invitado
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Iniciar Sesión"))) // Link de Login
                .andExpect(content().string(containsString("Registrarse"))) // Link de Registro
                .andExpect(content().string(containsString("navbar navbar-expand-lg navbar-dark bg-dark"))) // Fragmento correcto
                .andExpect(model().attribute("isAuthenticated", false));
    }

    @Test
    public void testNavBarLinksForAuthenticatedUser() throws Exception {
        // Arrange
        Long userId = 1L;
        UsuarioData mockUser = new UsuarioData();
        mockUser.setId(userId);
        mockUser.setNombre("Fernando Aldaz");

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioService.findById(userId)).thenReturn(mockUser);

        // Act & Assert - Verificar enlaces específicos
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/tareas\""))) // Link a tareas
                .andExpect(content().string(containsString("href=\"/account\""))) // Link a cuenta
                .andExpect(content().string(containsString("href=\"/logout\""))) // Link a logout
                .andExpect(content().string(containsString("href=\"/about\""))) // Brand link
                // Verificar que NO aparecen los links de invitado
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("href=\"/login\""))))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("href=\"/registro\""))));
    }

    @Test
    public void testNavBarLinksForUnauthenticatedUser() throws Exception {
        // Arrange
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // Act & Assert - Verificar enlaces específicos
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/login\""))) // Link a login
                .andExpect(content().string(containsString("href=\"/registro\""))) // Link a registro
                .andExpect(content().string(containsString("href=\"/about\""))) // Brand link
                // Verificar que NO aparecen los links de usuario autenticado
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("href=\"/tareas\""))))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("href=\"/account\""))));
    }

    @Test
    public void testNavBarBootstrapClasses() throws Exception {
        // Arrange
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // Act & Assert - Verificar clases CSS de Bootstrap
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("navbar navbar-expand-lg navbar-dark bg-dark")))
                .andExpect(content().string(containsString("navbar-brand")))
                .andExpect(content().string(containsString("navbar-nav")))
                .andExpect(content().string(containsString("nav-link")));
    }
}
package madstodolistfaldaz.controller;

import madstodolistfaldaz.authentication.ManagerUserSession;
import madstodolistfaldaz.dto.UsuarioData;
import madstodolistfaldaz.model.Usuario;
import madstodolistfaldaz.service.UsuarioService;
import madstodolistfaldaz.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    // Pruebas existentes
    @Test
    public void testAboutPageAuthenticated() throws Exception {
        Long userId = 1L;
        UsuarioData usuario = new UsuarioData();
        usuario.setId(userId);
        usuario.setNombre("Test User");

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioService.findById(userId)).thenReturn(usuario);

        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andExpect(model().attribute("isAuthenticated", true))
                .andExpect(model().attribute("usuario", usuario));
    }

    @Test
    public void testAboutPageUnauthenticated() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andExpect(model().attribute("isAuthenticated", false))
                .andExpect(model().attributeDoesNotExist("usuario"));
    }

    @Test
    public void testAccountPageAuthenticated() throws Exception {
        Long userId = 1L;
        UsuarioData usuario = new UsuarioData();
        usuario.setId(userId);

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioService.findById(userId)).thenReturn(usuario);

        mockMvc.perform(get("/account"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    @Test
    public void testAccountPageUnauthenticated() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/account"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // Nuevas pruebas para el endpoint de detalles de usuario
    @Test
    public void testMostrarUsuario() throws Exception {
        // Configurar datos de prueba
        Long userId = 1L;
        Usuario usuario = new Usuario("test@example.com");
        usuario.setId(userId);
        usuario.setNombre("Test User");
        usuario.setFechaNacimiento(new Date());

        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setId(userId);
        usuarioData.setNombre("Test User");
        usuarioData.setEmail("test@example.com");
        usuarioData.setFechaNacimiento(new Date());

        // Configurar mocks
        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(usuarioService.findById(userId)).thenReturn(usuarioData);

        // Ejecutar y verificar
        mockMvc.perform(get("/registrados/" + userId))
                .andExpect(status().isOk())
                .andExpect(view().name("descripcionUsuario"))
                .andExpect(model().attributeExists("usuarioDetalle"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("isAuthenticated"));
    }

    @Test
    public void testMostrarUsuarioNoAutenticado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/registrados/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testMostrarUsuarioNoEncontrado() throws Exception {
        Long userId = 1L;
        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/registrados/" + userId))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

    // Nueva prueba para listar usuarios registrados
    @Test
    public void testListarUsuarios() throws Exception {
        Long userId = 1L;
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setId(userId);
        usuarioData.setNombre("Test User");

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioService.findById(userId)).thenReturn(usuarioData);

        mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(view().name("registrados"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("isAuthenticated"));
    }

    @Test
    public void testListarUsuariosNoAutenticado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/registrados"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
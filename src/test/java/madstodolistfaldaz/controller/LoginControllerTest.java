package madstodolistfaldaz.controller;

import madstodolistfaldaz.authentication.ManagerUserSession;
import madstodolistfaldaz.dto.LoginData;
import madstodolistfaldaz.dto.RegistroData;
import madstodolistfaldaz.dto.UsuarioData;
import madstodolistfaldaz.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void testHomeRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("formLogin"))
                .andExpect(model().attributeExists("loginData"));
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        LoginData loginData = new LoginData();
        loginData.seteMail("test@example.com");
        loginData.setPassword("password");

        UsuarioData usuario = new UsuarioData();
        usuario.setId(1L);

        when(usuarioService.login("test@example.com", "password"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("test@example.com"))
                .thenReturn(usuario);

        mockMvc.perform(post("/login")
                        .flashAttr("loginData", loginData))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    @Test
    public void testFailedLoginUserNotFound() throws Exception {
        LoginData loginData = new LoginData();
        loginData.seteMail("nonexistent@example.com");
        loginData.setPassword("password");

        when(usuarioService.login("nonexistent@example.com", "password"))
                .thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        mockMvc.perform(post("/login")
                        .flashAttr("loginData", loginData))
                .andExpect(status().isOk())
                .andExpect(view().name("formLogin"))
                .andExpect(model().attribute("error", "No existe usuario"));
    }

    @Test
    public void testRegisterPage() throws Exception {
        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(view().name("formRegistro"))
                .andExpect(model().attributeExists("registroData"));
    }

    @Test
    public void testSuccessfulRegistration() throws Exception {
        RegistroData registroData = new RegistroData();
        registroData.setEmail("new@example.com");
        registroData.setPassword("password");
        registroData.setNombre("New User");

        when(usuarioService.findByEmail("new@example.com")).thenReturn(null);

        mockMvc.perform(post("/registro")
                        .flashAttr("registroData", registroData))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(managerUserSession).logout();
    }
}
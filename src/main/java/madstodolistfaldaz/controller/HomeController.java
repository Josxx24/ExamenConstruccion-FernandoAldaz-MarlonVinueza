package madstodolistfaldaz.controller;

import madstodolistfaldaz.authentication.ManagerUserSession;
import madstodolistfaldaz.dto.UsuarioData;
import madstodolistfaldaz.model.Usuario;
import madstodolistfaldaz.service.UsuarioService;
import madstodolistfaldaz.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class HomeController {

    private final ManagerUserSession managerUserSession;

    private final UsuarioService usuarioService;

    private final UsuarioRepository usuarioRepository;

    public HomeController(ManagerUserSession managerUserSession, UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.managerUserSession = managerUserSession;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }


    @GetMapping("/about")
    public String about(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        addUserDataToModel(model, idUsuarioLogeado);
        return "about";
    }

    @GetMapping("/account")
    public String account(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();

        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
        model.addAttribute("usuario", usuario);

        // Redirecciona a la lista de tareas del usuario
        return "redirect:/usuarios/" + idUsuarioLogeado;
    }

    @GetMapping("/registrados")
    public String listarUsuarios(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();

        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        addUserDataToModel(model, idUsuarioLogeado);

        try {
            Iterable<Usuario> usuariosIterable = usuarioRepository.findAll();
            List<Usuario> usuarios = StreamSupport.stream(usuariosIterable.spliterator(), false)
                    .collect(Collectors.toList());
            model.addAttribute("usuarios", usuarios);
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar usuarios: " + e.getMessage());
        }

        return "registrados";
    }
    @GetMapping("/registrados/{id}")
    public String mostrarUsuario(@PathVariable("id") Long idUsuario, Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();

        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        addUserDataToModel(model, idUsuarioLogeado);

        try {
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Convertimos la entidad Usuario a UsuarioData
            UsuarioData usuarioData = new UsuarioData();
            usuarioData.setId(usuario.getId());
            usuarioData.setNombre(usuario.getNombre());
            usuarioData.setEmail(usuario.getEmail());
            usuarioData.setFechaNacimiento(usuario.getFechaNacimiento());
            // No incluimos la contraseña

            model.addAttribute("usuarioDetalle", usuarioData);
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar usuario: " + e.getMessage());
        }

        return "descripcionUsuario";
    }

    // Método auxiliar para añadir datos del usuario al modelo
    private void addUserDataToModel(Model model, Long idUsuarioLogeado) {
        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            model.addAttribute("usuario", usuario);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
    }
}

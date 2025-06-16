package madstodolistfaldaz.repository;

import madstodolistfaldaz.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String s);

    // Sobrescribir findAll para devolver List en lugar de Iterable
    List<Usuario> findAll();
}
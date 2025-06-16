package madstodolistfaldaz.repository;

import madstodolistfaldaz.model.Tarea;
import org.springframework.data.repository.CrudRepository;

public interface TareaRepository extends CrudRepository<Tarea, Long> {
}

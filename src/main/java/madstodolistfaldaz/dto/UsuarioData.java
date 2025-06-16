package madstodolistfaldaz.dto;

import madstodolistfaldaz.model.Usuario;

import java.util.Date;
import java.util.Objects;

import java.time.LocalDate;
// Data Transfer Object para la clase Usuario
public class UsuarioData {


    private Date fechaNacimiento;
    private Long id;
    private String email;
    private String nombre;
    private String password;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    // Método para convertir este DTO a una entidad Usuario (modelo)
    public Usuario toUsuario() {
        Usuario u = new Usuario(this.email);
        u.setId(this.id);
        u.setNombre(this.nombre);
        u.setPassword(this.password);
        u.setFechaNacimiento(this.fechaNacimiento);
        return u;
    }

    // Sobreescribimos equals y hashCode para que dos usuarios sean iguales
    // si tienen el mismo ID (ignoramos el resto de atributos)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioData)) return false;
        UsuarioData that = (UsuarioData) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

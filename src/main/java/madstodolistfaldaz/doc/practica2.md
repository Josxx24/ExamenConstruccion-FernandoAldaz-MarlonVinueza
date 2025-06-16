# Documentacion tecnica

## 1. lista de clases y metodos implementados:

### Clases

- **HomeController:** Es un controlador Spring MVC que gestiona varias rutas de la aplicación relacionadas con la información general y los usuarios.
- **AcercaDeWebTest:** Es un test de integración para el endpoint /about del controlador (HomeController).
- **HomeControllerTest:** La clase HomeControllerTest es un conjunto de pruebas unitarias para el controlador HomeController de una aplicación Spring Boot. Utiliza MockMvc para simular peticiones HTTP y verificar las respuestas del controlador en distintos escenarios
- **LoginControllerTest:** La clase LoginControllerTest contiene pruebas unitarias para el controlador LoginController en una aplicación Spring Boot. Utiliza MockMvc para simular peticiones HTTP y verificar el comportamiento de los endpoints relacionados con el inicio de sesión, registro y cierre de sesión.
- **DescripcionUsuarioIntegrationTest:** La clase DescripcionUsuariosIntegrationTest es una clase de pruebas de integración para el controlador HomeController que verifica la funcionalidad relacionada con la visualización de detalles de usuarios registrados.
- **NavBarIntegrationTest:** La clase NavBarIntegrationTest es una clase de pruebas de integración que verifica el comportamiento y la presentación de la barra de navegación (navbar) en la aplicación web, específicamente en el controlador HomeController.
- **RegistradosIntegrationTest:** Esta clase de pruebas de integración valida el comportamiento del endpoint /registrados del controlador HomeController, que muestra la lista de usuarios registrados en la aplicación.

### Metodos

- **account(Model model):** Redirige a la vista de tareas del usuario logueado. Si no hay sesión activa, redirige a /login.
- **listarUsuarios(Model model):** Carga la lista de todos los usuarios registrados desde la base de datos y la muestra en la plantilla registrados.html. 
- **mostrarUsuario(@PathVariable Long idUsuario, Model model):** Muestra la descripción detallada de un usuario específico usando la plantilla descripcionUsuario.html. 
- **addUserDataToModel(Model model, Long idUsuario):** Método auxiliar para agregar los datos del usuario logueado al modelo.

## 2. Lista de plantillas Thyemeleaf añadidas:

- about.html
- descripcionUsuario
- navBar
- registrados


## 3. Explicacion de test implementados:

### AcercaDeWebTest.java
**Propósito:** Verifica el contenido devuelto por el endpoint /about.

**Test implementado:** 

**getAboutDevuelveNombreAplicacion:** Hace una solicitud GET a /about. Verifica que la respuesta contenga el texto "ToDoList".

### HomeControllerTest.java
**Propósito:**
Prueba el comportamiento del HomeController, que gestiona vistas como /about, /account, /registrados, etc.

**Pruebas para /about:** 

**testAboutPageAuthenticated:** 
- Simula un usuario autenticado.
- Verifica que la vista retornada sea "about".
- Verifica que el modelo tenga los atributos isAuthenticated = true y el usuario correspondiente.

**testAboutPageUnauthenticated:**
- Simula que no hay ningún usuario logueado. 
- Verifica que la vista también sea "about" pero sin datos de usuario, y isAuthenticated = false.

**Pruebas para /account:**

**testAccountPageAuthenticated:**
- Si el usuario está autenticado, se redirige a /usuarios/{id}/tareas. 
- testAccountPageUnauthenticated:
- Si no está autenticado, se redirige a /login.

**Pruebas para /registrados/{id} (detalle del usuario):**

**testMostrarUsuario:**
- Simula un usuario autenticado y con ID válido.
- Verifica que se muestra la vista descripcionUsuario con los datos correctos.

**testMostrarUsuarioNoAutenticado:**
- Usuario no autenticado: debe redirigir a /login.

**testMostrarUsuarioNoEncontrado:**
- Si el usuario con ese ID no existe, se muestra la vista "error" con un atributo "error" en el modelo.

**Pruebas para /registrados (lista de usuarios):**

**testListarUsuarios:**
- Usuario autenticado: se muestra la vista "registrados" con la lista de usuarios.
- testListarUsuariosNoAutenticado:
- Usuario no autenticado: redirección a /login.

### LoginControllerTest.java
**Propósito:** Verifica el comportamiento del controlador que maneja inicio de sesión, registro y cierre de sesión.

**Pruebas para / y /login:**

**testHomeRedirectsToLogin:**
- La ruta raíz / redirige a /login.

**testLoginPage:**
- Verifica que se muestra el formulario de login (formLogin) con un objeto loginData.

**Pruebas de inicio de sesión:**

**testSuccessfulLogin:**
- Si el usuario existe y el login es exitoso, redirige a /usuarios/{id}/tareas.

**testFailedLoginUserNotFound:**
- Si el usuario no existe, se muestra formLogin con un mensaje de error.

**Pruebas de registro:**

**testRegisterPage:**

- Muestra el formulario de registro (formRegistro) con registroData.

**testSuccessfulRegistration:**

- Si el email no está en uso, se redirige al login al registrar un nuevo usuario.

**Prueba de cierre de sesión:**

**testLogout:**
- Verifica que al acceder a /logout, se llama al método logout() de ManagerUserSession y se redirige a /login.

### DescripcionUsuariosIntegrationTest.java
**Propósito:** Verifica que la vista que muestra la descripción de los usuarios funcione correctamente y contenga los datos esperados.

**Pruebas de contenido de la vista de usuarios:**

**testDescripcionUsuariosContieneNombreYCorreo:**
- Verifica que al acceder a /usuarios, la respuesta contiene el nombre y correo de los usuarios registrados.

**testDescripcionUsuariosContieneTareas:**
- Verifica que también se muestren las tareas asociadas a cada usuario en la vista de descripción.

**testDescripcionUsuariosUsaVistaCorrecta:**
- Confirma que se renderiza la plantilla listaUsuarios al acceder a la descripción de usuarios.


## 4. Codigo de fuente relevante

1. **Método listarUsuarios**

Demuestra cómo transformar un Iterable<Usuario> obtenido desde el repositorio en una List<Usuario> utilizando StreamSupport, lo cual es una técnica útil cuando se trabaja con Spring Data:

```java
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
        return "error";
    }

    return "registrados";
}
```

2. **DescripcionUsuariosIntegrationTest.java**
- Se hace una petición GET a /usuarios.
- Se verifica que la respuesta HTTP sea 200 OK.
- Se asegura que el modelo contenga un atributo llamado "usuarios".
- Además, se comprueba que el contenido HTML de la respuesta incluya el nombre y correo de un usuario esperado.

```java
@Test
public void testDescripcionUsuariosContieneNombreYCorreo() throws Exception {
    mockMvc.perform(get("/usuarios"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("usuarios"))
        .andExpect(content().string(containsString("nombreUsuario")))
        .andExpect(content().string(containsString("correoUsuario@example.com")));
}
```

3. **NavBarIntegrationTest.java**
- Se accede a la ruta /usuarios.
- Se verifica que la respuesta tenga un código HTTP 200 OK.
- Se comprueba que el contenido HTML incluya los enlaces principales de la barra de navegación: "Inicio", "Usuarios", "Tareas" y "Cerrar sesión".

```java
@Test
public void testNavBarContieneEnlacesPrincipales() throws Exception {
    mockMvc.perform(get("/usuarios"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Inicio")))
        .andExpect(content().string(containsString("Usuarios")))
        .andExpect(content().string(containsString("Tareas")))
        .andExpect(content().string(containsString("Cerrar sesión")));
}
```

4. **RegistradosIntegrationTest.java**
- Se envía un formulario POST a /registro con parámetros para email y contraseña.
- Se espera que la respuesta sea una redirección (3xx).
- Se confirma que la redirección es hacia la página de login (/login), lo que indica un registro exitoso.

```java
@Test
public void testRegistroExitosoRedirigeALogin() throws Exception {
    mockMvc.perform(post("/registro")
            .param("email", "nuevo@usuario.com")
            .param("password", "password123")
            .param("confirmPassword", "password123"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"));
}
```
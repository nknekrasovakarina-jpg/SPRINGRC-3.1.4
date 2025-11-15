package ru.kata.spring.boot_security.demo.REST;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.DTO.UserDTO;
import ru.kata.spring.boot_security.demo.MODEL.Role;
import ru.kata.spring.boot_security.demo.MODEL.User;
import ru.kata.spring.boot_security.demo.SERVICE.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;
    private final PasswordEncoder encoder;

    public AdminRestController(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @GetMapping("/users")
    public List<UserDTO> getAll() {
        return userService.getAllUsers().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return toDTO(userService.getUserById(id));
    }

    @PostMapping("/users")
    public UserDTO create(@RequestBody UserDTO dto) {

        User user = fromDTO(dto);

        user.setPassword(encoder.encode(dto.getPassword()));

        return toDTO(userService.saveUser(user));
    }

    @PutMapping("/users/{id}")
    public UserDTO update(@PathVariable Long id, @RequestBody UserDTO dto) {

        User user = fromDTO(dto);

        return toDTO(userService.updateUser(id, user));
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/roles")
    public List<String> getRoles() {
        return userService.getAllRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    // ---------------- DTO MAP ----------------

    private UserDTO toDTO(User u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setAge(u.getAge());
        dto.setRoles(u.getRoles().stream()
                .map(Role::getName)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.toList()));
        return dto;
    }

    private User fromDTO(UserDTO dto) {

        User u = new User();
        u.setId(dto.getId());
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        u.setAge(dto.getAge());

        if (dto.getRoles() != null) {
            u.setRoles(dto.getRoles().stream()
                    .map(r -> {

                        // ЕСЛИ роль уже начинается с ROLE_, НЕ добавляем префикс !!!
                        String dbRoleName = r.startsWith("ROLE_") ? r : "ROLE_" + r;

                        return userService.findRoleByName(dbRoleName)
                                .orElseThrow(() -> new RuntimeException("Role not found: " + dbRoleName));
                    })
                    .collect(Collectors.toSet()));
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            u.setPassword(dto.getPassword());
        }

        return u;
    }

}
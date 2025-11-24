package ru.kata.spring.boot_security.demo.REST;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.DTO.UserDTO;
import ru.kata.spring.boot_security.demo.MAPPER.UserMapper;
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
    private final UserMapper userMapper;

    public AdminRestController(UserService userService,
                               PasswordEncoder encoder,
                               UserMapper userMapper) {
        this.userService = userService;
        this.encoder = encoder;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public List<UserDTO> getAll() {
        return userService.getAllUsers().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return userMapper.toDTO(userService.getUserById(id));
    }

    @PostMapping("/users")
    public UserDTO create(@RequestBody UserDTO dto) {
        User user = userMapper.fromDTO(dto);
        user.setPassword(encoder.encode(dto.getPassword()));
        return userMapper.toDTO(userService.saveUser(user));
    }

    @PutMapping("/users/{id}")
    public UserDTO update(@PathVariable Long id, @RequestBody UserDTO dto) {
        User user = userMapper.fromDTO(dto);
        return userMapper.toDTO(userService.updateUser(id, user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    public List<String> getRoles() {
        return userService.getAllRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
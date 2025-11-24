package ru.kata.spring.boot_security.demo.MAPPER;

import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.DTO.UserDTO;
import ru.kata.spring.boot_security.demo.MODEL.Role;
import ru.kata.spring.boot_security.demo.MODEL.User;
import ru.kata.spring.boot_security.demo.SERVICE.UserService;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final UserService userService;

    public UserMapper(UserService userService) {
        this.userService = userService;
    }

    public UserDTO toDTO(User u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setAge(u.getAge());
        dto.setRoles(
                u.getRoles().stream()
                        .map(Role::getName)
                        .map(r -> r.replace("ROLE_", ""))
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public User fromDTO(UserDTO dto) {
        User u = new User();
        u.setId(dto.getId());
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        if (dto.getAge() != null) u.setAge(dto.getAge());

        if (dto.getRoles() != null) {
            u.setRoles(
                    dto.getRoles().stream()
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                            .map(dbRole ->
                                    userService.findRoleByName(dbRole)
                                            .orElseThrow(() ->
                                                    new RuntimeException("Role not found: " + dbRole))
                            )
                            .collect(Collectors.toSet())
            );
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            u.setPassword(dto.getPassword());
        }

        return u;
    }
}
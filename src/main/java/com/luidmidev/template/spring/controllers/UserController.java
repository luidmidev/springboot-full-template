package com.luidmidev.template.spring.controllers;


import com.luidmidev.template.spring.dto.UpdateUser;
import com.luidmidev.template.spring.models.User;
import com.luidmidev.template.spring.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {


    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> find(@PathVariable String id) {
        return ResponseEntity.ok(userService.find(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateDetails(@PathVariable String id, @RequestBody UpdateUser detailsUser) {
        userService.updateDetails(id, detailsUser);
        return ResponseEntity.ok("Usuario actualizado");
    }
}

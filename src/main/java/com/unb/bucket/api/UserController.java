package com.unb.bucket.api;

import com.unb.bucket.core.dao.UserDAO;
import com.unb.bucket.core.exception.ResourceNotFoundException;
import com.unb.bucket.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {

    @Autowired
    UserDAO userDAO;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable(value = "id") final long userId) {
        return userDAO.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        return userDAO.save(user);
    }
}

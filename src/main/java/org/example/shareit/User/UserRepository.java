package org.example.shareit.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int id);
    List<User> findAll();
    User save(User user);
    void deleteById(int id);
}

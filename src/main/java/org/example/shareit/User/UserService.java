package org.example.shareit.User;

import lombok.extern.slf4j.Slf4j;
import org.example.shareit.User.Exceptions.NonExistingUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user,int userId) {
        if(!userRepository.existsById(userId)) {
            throw new NonExistingUserException(userId);
        }
        user.setId(userId);
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public User findUserById(int userId) throws NonExistingUserException {
        User user = userRepository.findById(userId);
        if(user == null) {
            throw new NonExistingUserException(userId);
        }
        return userRepository.findById(userId);
    }
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}

package services;

import entity.User;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveEntry(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Arrays.asList("USER"));
        }
        userRepository.save(user);
    }

    public boolean saveNewEntry(User user) {
        return userRepository.save(user) != null;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id) {
        return userRepository.findById(id);
    }

    public User findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteById(ObjectId id) {
        userRepository.deleteById(id);
    }

    public void deleteByUserName(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            userRepository.deleteById(user.getId());
        }
    }
}

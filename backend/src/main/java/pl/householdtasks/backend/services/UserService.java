package pl.householdtasks.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.householdtasks.backend.model.User;
import pl.householdtasks.backend.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean checkIfUserWithUsernameExists(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        return byUsername.isPresent();
    }

    public boolean checkIfUserWithEmailExists(String email) {
        Optional<User> byUsername = userRepository.findByEmail(email);
        return byUsername.isPresent();
    }

}

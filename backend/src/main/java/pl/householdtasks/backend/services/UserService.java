package pl.householdtasks.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import pl.householdtasks.backend.model.User;
import pl.householdtasks.backend.model.VerificationToken;
import pl.householdtasks.backend.model.dtos.UserDTO;
import pl.householdtasks.backend.repositories.UserRepository;
import pl.householdtasks.backend.repositories.VerificationTokenRepository;
import pl.householdtasks.backend.services.events.OnRegistrationCompleteEvent;
import pl.householdtasks.backend.services.events.OnResendVerificationTokenEvent;

import javax.persistence.EntityNotFoundException;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public boolean checkIfUserWithUsernameExists(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        return byUsername.isPresent();
    }

    public boolean checkIfUserWithEmailExists(String email) {
        Optional<User> byUsername = userRepository.findByEmail(email);
        return byUsername.isPresent();
    }

    public boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(email).matches();
    }

    public User registerNewUser(UserDTO user) {
        User savedUser = userDetailsService.save(user);
        String url = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(url, savedUser));

        return savedUser;
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public VerificationToken createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        return verificationTokenRepository.save(verificationToken);
    }

    public Optional<VerificationToken> getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }


    public boolean isVerificationTokenExpired(VerificationToken token) {
        Calendar calendar = Calendar.getInstance();
        return token.getExpiryDate().getTime() - calendar.getTime().getTime() <= 0;
    }

    public VerificationToken generateNewVerificationToken(String token) {
        Optional<VerificationToken> byToken = verificationTokenRepository.findByToken(token);
        if (byToken.isPresent()) {
            VerificationToken verificationToken = byToken.get();
            verificationToken.setToken(UUID.randomUUID().toString());
            verificationToken.setExpiryDate(verificationToken.calculateExpiryDate(VerificationToken.EXPIRATION));
            return verificationTokenRepository.save(verificationToken);
        } else {
            throw new EntityNotFoundException("Token not found");
        }
    }

    public void resendNewVerificationToken(VerificationToken newToken) {
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest().build();

        String url = uriComponents.getScheme() + "://"
                + uriComponents.getHost() + ":"
                + uriComponents.getPort() + "/"
                + uriComponents.getPathSegments().get(0);
        eventPublisher.publishEvent(new OnResendVerificationTokenEvent(url, newToken));
    }
}

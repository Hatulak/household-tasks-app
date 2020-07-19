package pl.householdtasks.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import pl.householdtasks.backend.model.PasswordResetToken;
import pl.householdtasks.backend.model.User;
import pl.householdtasks.backend.model.VerificationToken;
import pl.householdtasks.backend.model.dtos.UserDTO;
import pl.householdtasks.backend.repositories.PasswordResetTokenRepository;
import pl.householdtasks.backend.repositories.UserRepository;
import pl.householdtasks.backend.repositories.VerificationTokenRepository;
import pl.householdtasks.backend.services.events.OnRegistrationCompleteEvent;
import pl.householdtasks.backend.services.events.OnResendVerificationTokenEvent;
import pl.householdtasks.backend.services.events.OnResetPasswordEvent;

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

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    public boolean checkIfUserWithUsernameExists(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        return byUsername.isPresent();
    }

    public boolean checkIfUserWithEmailExists(String email) {
        Optional<User> byUsername = userRepository.findByEmail(email);
        return byUsername.isPresent();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
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

    public void sendPasswordResetToken(User user) {
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest().build();

        String url = uriComponents.getScheme() + "://"
                + uriComponents.getHost() + ":"
                + uriComponents.getPort() + "/"
                + uriComponents.getPathSegments().get(0);

        eventPublisher.publishEvent(new OnResetPasswordEvent(url, user));
    }

    public PasswordResetToken createPasswordResetToken(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    public Optional<PasswordResetToken> getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public boolean isPasswordResetTokenExpired(PasswordResetToken passwordResetToken) {
        Calendar calendar = Calendar.getInstance();
        return passwordResetToken.getExpiryDate().getTime() - calendar.getTime().getTime() <= 0;
    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(bcryptEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deletePasswordRequestToken(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.delete(passwordResetToken);
    }
}

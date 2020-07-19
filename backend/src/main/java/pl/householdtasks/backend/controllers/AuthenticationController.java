package pl.householdtasks.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.householdtasks.backend.config.utils.JwtRequest;
import pl.householdtasks.backend.config.utils.JwtResponse;
import pl.householdtasks.backend.config.utils.JwtTokenUtil;
import pl.householdtasks.backend.controllers.utils.ResponseGenerator;
import pl.householdtasks.backend.model.PasswordResetToken;
import pl.householdtasks.backend.model.User;
import pl.householdtasks.backend.model.VerificationToken;
import pl.householdtasks.backend.model.dtos.PasswordDTO;
import pl.householdtasks.backend.model.dtos.UserDTO;
import pl.householdtasks.backend.services.JwtUserDetailsService;
import pl.householdtasks.backend.services.UserService;

import java.util.Optional;


@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;


    @PostMapping(value = "/signin")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) {

        boolean userWithUsernameExists = userService.checkIfUserWithUsernameExists(user.getUsername());
        if (userWithUsernameExists) {
            return ResponseGenerator.createBadRequestWithMessage("User with that username already exists");
        }
        boolean userWithEmailExists = userService.checkIfUserWithEmailExists(user.getEmail());
        if (userWithEmailExists) {
            return ResponseGenerator.createBadRequestWithMessage("There is an account connected to this email");
        }
        if (!userService.isEmailValid(user.getEmail())) {
            return ResponseGenerator.createBadRequestWithMessage("Email is not correct");
        }
        User savedUser = userService.registerNewUser(user);

        return ResponseEntity.ok(savedUser);
    }

    @GetMapping(value = "/signup/registrationConfirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        Optional<VerificationToken> verificationToken = userService.getVerificationToken(token);

        if (!verificationToken.isPresent()) {
            return ResponseGenerator.createBadRequestWithMessage("Invalid token");
        }
        User user = verificationToken.get().getUser();
        if (userService.isVerificationTokenExpired(verificationToken.get())) {
            return ResponseGenerator.createBadRequestWithMessage("Token is expired");
        }

        user.setIsEmailVerified(true);
        userService.updateUser(user);
        return ResponseGenerator.createOkRequestWithMessage("Email has been verified");
    }

    @GetMapping(value = "/signup/resendRegistrationToken")
    public ResponseEntity<?> resendRegistrationToken(@RequestParam("token") String token) {
        VerificationToken newToken = userService.generateNewVerificationToken(token);

        userService.resendNewVerificationToken(newToken);

        return ResponseGenerator.createOkRequestWithMessage("Email has been sent");
    }

    @GetMapping(value = "/signin/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email) {
        Optional<User> userByEmail = userService.getUserByEmail(email);
        if (!userByEmail.isPresent()) {
            return ResponseGenerator.createBadRequestWithMessage("There is no account with this email");
        }
        userService.sendPasswordResetToken(userByEmail.get());
        return ResponseGenerator.createOkRequestWithMessage("Email has been sent");
    }

    @PostMapping(value = "/signin/changePassword")
    public ResponseEntity<?> changePasswordRequest(@RequestParam("token") String token) {
        Optional<PasswordResetToken> passwordResetToken = userService.getPasswordResetToken(token);

        if (!passwordResetToken.isPresent()) {
            return ResponseGenerator.createBadRequestWithMessage("Invalid token");
        }
        User user = passwordResetToken.get().getUser();
        if (userService.isPasswordResetTokenExpired(passwordResetToken.get())) {
            return ResponseGenerator.createBadRequestWithMessage("Token is expired");
        }
        return ResponseGenerator.createOkRequestWithMessageAndBooleanValue("Request accepted", "passed", true);
    }

    @PostMapping(value = "/signin/saveNewPassword")
    public ResponseEntity<?> saveNewPassword(@RequestBody PasswordDTO passwordDTO) {
        Optional<PasswordResetToken> passwordResetToken = userService.getPasswordResetToken(passwordDTO.getToken());

        if (!passwordResetToken.isPresent()) {
            return ResponseGenerator.createBadRequestWithMessage("Invalid token");
        }
        User user = passwordResetToken.get().getUser();
        if (userService.isPasswordResetTokenExpired(passwordResetToken.get())) {
            return ResponseGenerator.createBadRequestWithMessage("Token is expired");
        }

        userService.changePassword(user, passwordDTO.getNewPassword());
        userService.deletePasswordRequestToken(passwordResetToken.get());
        return ResponseGenerator.createOkRequestWithMessage("Password has been changed");
    }


}

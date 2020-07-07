package pl.householdtasks.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.householdtasks.backend.config.utils.JwtRequest;
import pl.householdtasks.backend.config.utils.JwtResponse;
import pl.householdtasks.backend.config.utils.JwtTokenUtil;
import pl.householdtasks.backend.model.dtos.UserDTO;
import pl.householdtasks.backend.services.JwtUserDetailsService;


@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

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
        //TODO - brakuje sprawdzenia czy użytkownik o danym username jest zarejestrowany lub czy pod danym adresem email jest ktoś zarejestrowany
        //TODO - sprawdzenia email patternu

        //TODO - wysyłka na maila linka do potwierdzenia utworzenia konta
        return ResponseEntity.ok(userDetailsService.save(user));
    }

}

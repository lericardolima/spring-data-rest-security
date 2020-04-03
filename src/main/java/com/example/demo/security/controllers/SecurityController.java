package com.example.demo.security.controllers;

import com.example.demo.enums.RoleType;
import com.example.demo.exceptions.UsernameAlreadyInUseException;
import com.example.demo.models.User;
import com.example.demo.security.dto.TokenDTO;
import com.example.demo.security.dto.UserDTO;
import com.example.demo.security.http.Response;
import com.example.demo.security.utils.JwtTokenUtils;
import com.example.demo.security.utils.PasswordUtils;
import com.example.demo.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@CrossOrigin
public class SecurityController {

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response<TokenDTO>> doRegister(@Valid @RequestBody UserDTO userDTO, BindingResult result) {

        // Validates if there are errors in the request body.
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(buildErrorResponse(result));
        }

        // Persists user but return a custom error if the username is already in use.
        User user = null;
        try {
            user = buildAndPersistUser(userDTO);
        } catch (UsernameAlreadyInUseException e) {
            result.addError(new ObjectError("username", e.getMessage()));
            return ResponseEntity.badRequest().body(buildErrorResponse(result));
        }

        // Generates the token and builds the response.
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return ResponseEntity.ok(buildTokenResponse(userDetails));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<TokenDTO>> doLogin(@Valid @RequestBody UserDTO userDTO, BindingResult result) {

        // Validates if there are errors in the request body.
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(buildErrorResponse(result));
        }

        // Loads the user credentials but returns an error if the user with given username was not found.
        UserDetails userDetails = null;
        try {
            userDetails = userDetailsService.loadUserByUsername(userDTO.getUsername());
        } catch (UsernameNotFoundException e) {
            result.addError(new ObjectError("username", e.getMessage()));
            return ResponseEntity.badRequest().body(buildErrorResponse(result));
        }

        // Generates the token and the response body.
        return ResponseEntity.ok(buildTokenResponse(userDetails));
    }

    private User buildAndPersistUser(UserDTO userDTO) throws UsernameAlreadyInUseException {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(PasswordUtils.encode(userDTO.getPassword()));
        user.setRole(RoleType.ROLE_USER);
        return userService.save(user);
    }

    private Response<TokenDTO> buildErrorResponse(BindingResult result) {
        log.error("Errors during login: {}", result.getAllErrors());
        Response<TokenDTO> response = new Response<TokenDTO>();
        result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
        return response;
    }

    private Response<TokenDTO> buildTokenResponse(UserDetails userDetails) {
        log.info("Generating token for user {}", userDetails.getUsername());
        Response<TokenDTO> response = new Response<TokenDTO>();
        String token = jwtTokenUtils.getToken(userDetails);
        LocalDateTime expiration = jwtTokenUtils.getExpiration(token);
        response.setData(new TokenDTO(token, expiration));
        return response;
    }

}

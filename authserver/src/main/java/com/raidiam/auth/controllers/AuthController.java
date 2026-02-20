package com.raidiam.auth.controllers;

import com.raidiam.auth.model.AccessTokenResponse;
import com.raidiam.auth.services.AuthService;
import com.raidiam.auth.services.DiscoveryService;
import com.raidiam.auth.services.TokenService;
import com.raidiam.auth.model.IntrospectionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthController {

    private AuthService authService;

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private TokenService tokenService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping("/.well-known/openid-configuration")
    public ResponseEntity<?> discovery() {
        return ResponseEntity.ok(discoveryService.discovery());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/token", produces = "application/json", consumes = {"application/x-www-form-urlencoded", "application/x-www-form-urlencoded;charset=UTF-8"})
    public ResponseEntity<?> issueToken(@RequestBody MultiValueMap<String, String> params) {
            AccessTokenResponse response = authService.requestToken(params);
            switch(response.getRequestStatus()) {
                case BAD_REQUEST -> {
                    return  ResponseEntity.badRequest().build();
                }
                case UNAUTHORIZED -> {
                    return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }
            return ResponseEntity.ok(response.getAccessToken());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/token/introspect", produces = "application/json", consumes = {"application/x-www-form-urlencoded", "application/x-www-form-urlencoded;charset=UTF-8"})
    public ResponseEntity<?> introspect(@RequestBody MultiValueMap<String, String> params) {
        try {
            IntrospectionResponse response = tokenService.introspect(params.getFirst("token"));

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }



}

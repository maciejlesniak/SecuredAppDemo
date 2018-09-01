package pl.sparkidea.security.securedapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.sparkidea.security.securedapp.dto.SimpleResponse;

import java.security.Principal;

import reactor.core.publisher.Mono;

/**
 * @author Maciej Lesniak
 */

@RestController
public class SimpleController {

    @GetMapping(value = "/public")
    public Mono<SimpleResponse> getPublicResource() {
        return Mono.just(new SimpleResponse("public resource"));
    }

    @GetMapping(value = "/api/secured")
    public Mono<SimpleResponse> getSecuredResource(Mono<Principal> principal) {

        return principal
                .map(user -> new SimpleResponse("secured resource requested", user));

    }

}

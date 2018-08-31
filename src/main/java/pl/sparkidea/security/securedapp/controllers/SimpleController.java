package pl.sparkidea.security.securedapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import reactor.core.publisher.Mono;

/**
 * @author Maciej Lesniak
 */

@RestController
public class SimpleController {

    @GetMapping("/")
    public Mono<String> getPublicResource() {
        return Mono.just("public resource");
    }

    @GetMapping("/api/secured")
    public Mono<String> getSecuredResource(Mono<Principal> principal) {

        return principal
                .map(Principal::toString)
                .map(userId -> "secured resource requested by: " + userId);

    }

}

package pl.sparkidea.security.securedapp.dto;

import java.security.Principal;

/**
 * @author Maciej Lesniak
 */
public class SimpleResponse {

    private final String response;
    private final Principal principal;

    public SimpleResponse(String response) {
        this(response, null);
    }

    public SimpleResponse(String response, Principal principal) {
        this.response = response;
        this.principal = principal;
    }

    public String getResponse() {
        return response;
    }

    public Principal getPrincipal() {
        return principal;
    }
}

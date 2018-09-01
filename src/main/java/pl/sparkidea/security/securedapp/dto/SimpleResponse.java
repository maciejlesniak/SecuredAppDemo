package pl.sparkidea.security.securedapp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.Principal;

/**
 * @author Maciej Lesniak
 */
public class SimpleResponse {

    private final static String RESPONSE = "response";
    private final static String PRINCIPAL = "principal";

    private final String response;
    private final Principal principal;

    @JsonCreator
    public SimpleResponse(@JsonProperty(RESPONSE) String response) {
        this(response, null);
    }

    @JsonCreator
    public SimpleResponse(@JsonProperty(RESPONSE) String response,
                          @JsonProperty(PRINCIPAL) Principal principal) {
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

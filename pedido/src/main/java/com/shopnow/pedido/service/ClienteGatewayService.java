package com.shopnow.pedido.service;

import com.shopnow.pedido.dto.AuthLoginRequest;
import com.shopnow.pedido.dto.AuthTokenResponse;
import com.shopnow.pedido.dto.ClienteRemotoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class ClienteGatewayService {

    private final RestTemplate restTemplate;

    @Value("${cliente.service.base-url}")
    private String clienteServiceBaseUrl;

    @Value("${cliente.service.username}")
    private String clienteServiceUsername;

    @Value("${cliente.service.password}")
    private String clienteServicePassword;

    public ClienteGatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ClienteRemotoResponse obtenerClientePorId(Long clienteId) {
        String token = obtenerToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        try {
            ResponseEntity<ClienteRemotoResponse> response = restTemplate.exchange(
                    clienteServiceBaseUrl + "/api/v1/clientes/" + clienteId,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    ClienteRemotoResponse.class
            );

            ClienteRemotoResponse body = response.getBody();
            if (body == null) {
                throw new ResponseStatusException(BAD_GATEWAY, "El microservicio cliente respondio sin datos");
            }
            return body;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(NOT_FOUND, "El cliente indicado no existe");
        } catch (RestClientException ex) {
            throw new ResponseStatusException(BAD_GATEWAY, "No fue posible comunicarse con el microservicio cliente");
        }
    }

    private String obtenerToken() {
        try {
            AuthLoginRequest request = new AuthLoginRequest(clienteServiceUsername, clienteServicePassword);
            ResponseEntity<AuthTokenResponse> response = restTemplate.postForEntity(
                    clienteServiceBaseUrl + "/auth/login",
                    request,
                    AuthTokenResponse.class
            );

            AuthTokenResponse body = response.getBody();
            if (body == null || body.token() == null || body.token().isBlank()) {
                throw new ResponseStatusException(BAD_GATEWAY, "No fue posible obtener token del microservicio cliente");
            }

            return body.token();
        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciales invalidas para comunicarse con cliente");
        } catch (RestClientException ex) {
            throw new ResponseStatusException(BAD_GATEWAY, "No fue posible autenticar contra el microservicio cliente");
        }
    }
}

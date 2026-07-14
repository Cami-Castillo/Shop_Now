package com.shopnow.cliente.service;

import com.shopnow.cliente.dto.PedidoRemotoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
public class PedidoGatewayService {

    private static final ParameterizedTypeReference<List<PedidoRemotoResponse>> PEDIDO_LIST_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestTemplate restTemplate;

    @Value("${pedido.service.base-url}")
    private String pedidoServiceBaseUrl;

    public PedidoGatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PedidoRemotoResponse> obtenerPedidosPorCliente(int clienteId) {
        try {
            ResponseEntity<List<PedidoRemotoResponse>> response = restTemplate.exchange(
                    pedidoServiceBaseUrl + "/api/v1/pedidos/cliente/" + clienteId,
                    HttpMethod.GET,
                    null,
                    PEDIDO_LIST_TYPE
            );

            return response.getBody() != null ? response.getBody() : List.of();
        } catch (RestClientException ex) {
            throw new ResponseStatusException(BAD_GATEWAY, "No fue posible comunicarse con el microservicio pedido");
        }
    }
}

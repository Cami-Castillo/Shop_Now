package com.shopnow.pedido;

import com.shopnow.pedido.dto.ClienteRemotoResponse;
import com.shopnow.pedido.dto.PedidoCreateRequest;
import com.shopnow.pedido.dto.PedidoResponse;
import com.shopnow.pedido.model.Pedido;
import com.shopnow.pedido.repository.PedidoRepository;
import com.shopnow.pedido.service.ClienteGatewayService;
import com.shopnow.pedido.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTests {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteGatewayService clienteGatewayService;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void crearPedidoValidaClienteYGuarda() {
        PedidoCreateRequest request = new PedidoCreateRequest(1L, "Notebook Gamer", 2);
        ClienteRemotoResponse cliente = new ClienteRemotoResponse(1, "Benjamin", "benjamin@shopnow.com", "Santiago", "ROLE_ADMIN");

        when(clienteGatewayService.obtenerClientePorId(1L)).thenReturn(cliente);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(99L);
            pedido.setCreatedAt(LocalDateTime.of(2026, 4, 27, 18, 0));
            return pedido;
        });

        PedidoResponse response = pedidoService.crear(request);

        assertEquals(99L, response.id());
        assertEquals(1L, response.clienteId());
        assertEquals("Benjamin", response.clienteNombre());
        assertEquals("Notebook Gamer", response.descripcion());
        assertEquals(2, response.cantidad());
    }

    @Test
    void buscarPorIdLanzaErrorSiNoExiste() {
        when(pedidoRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> pedidoService.buscarPorId(50L));
    }
}

package com.shopnow.pedido.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopnow.pedido.dto.PedidoCreateRequest;
import com.shopnow.pedido.dto.PedidoResponse;
import com.shopnow.pedido.dto.PedidoUpdateRequest;
import com.shopnow.pedido.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoService pedidoService;

    @Test
    void listarRetornaPedidos() throws Exception {
        // CONFIGURACIÓN: Simular que el servicio devuelve un listado con un pedido
        PedidoResponse response = new PedidoResponse(1L, 10L, "Juan", "juan@email.com", "Teclado Mecanico", 2, LocalDateTime.now());
        when(pedidoService.listar()).thenReturn(List.of(response));

        // EJECUCIÓN y VERIFICACIÓN: Simular petición GET y comprobar el formato del JSON retornado
        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].descripcion").value("Teclado Mecanico"))
                .andExpect(jsonPath("$[0].clienteNombre").value("Juan"));

        verify(pedidoService, times(1)).listar();
    }

    @Test
    void buscarPorIdExito() throws Exception {
        // CONFIGURACIÓN: Simular que se encuentra el pedido por ID
        PedidoResponse response = new PedidoResponse(1L, 10L, "Juan", "juan@email.com", "Teclado Mecanico", 2, LocalDateTime.now());
        when(pedidoService.buscarPorId(1L)).thenReturn(response);

        // EJECUCIÓN y VERIFICACIÓN: Simular GET con ID 1 y comprobar el cuerpo de respuesta
        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.descripcion").value("Teclado Mecanico"));

        verify(pedidoService, times(1)).buscarPorId(1L);
    }

    @Test
    void buscarPorIdNoExisteRetorna404() throws Exception {
        // CONFIGURACIÓN: Simular que lanzar error 404 cuando el pedido con ID 99 no existe
        when(pedidoService.buscarPorId(99L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        // EJECUCIÓN y VERIFICACIÓN: Simular GET con ID 99 y comprobar respuesta de error
        mockMvc.perform(get("/api/v1/pedidos/99"))
                .andExpect(status().isNotFound());

        verify(pedidoService, times(1)).buscarPorId(99L);
    }

    @Test
    void crearPedidoExito() throws Exception {
        PedidoCreateRequest request = new PedidoCreateRequest(10L, "Teclado Mecanico", 2);
        PedidoResponse response = new PedidoResponse(1L, 10L, "Juan", "juan@email.com", "Teclado Mecanico", 2, LocalDateTime.now());

        // CONFIGURACIÓN: Simular creación del pedido en el servicio
        when(pedidoService.crear(any(PedidoCreateRequest.class))).thenReturn(response);

        // EJECUCIÓN y VERIFICACIÓN: Enviar petición POST con el JSON y esperar status 201 CREATED
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.descripcion").value("Teclado Mecanico"));

        verify(pedidoService, times(1)).crear(any(PedidoCreateRequest.class));
    }

    @Test
    void crearPedidoCuerpoInvalidoRetorna400() throws Exception {
        // Enviar datos erróneos: clienteId nulo, descripción vacía y cantidad negativa
        PedidoCreateRequest request = new PedidoCreateRequest(null, "", -1);

        // EJECUCIÓN y VERIFICACIÓN: Comprobar que Spring intercepta el error y devuelve 400
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Garantizar que la lógica de negocio no haya sido llamada
        verify(pedidoService, never()).crear(any(PedidoCreateRequest.class));
    }

    @Test
    void actualizarPedidoExito() throws Exception {
        PedidoUpdateRequest request = new PedidoUpdateRequest("Teclado Mecanico RGB", 3);
        PedidoResponse response = new PedidoResponse(1L, 10L, "Juan", "juan@email.com", "Teclado Mecanico RGB", 3, LocalDateTime.now());

        // CONFIGURACIÓN: Simular actualización en el servicio
        when(pedidoService.actualizar(eq(1L), any(PedidoUpdateRequest.class))).thenReturn(response);

        // EJECUCIÓN y VERIFICACIÓN: Enviar petición PUT y esperar 200 OK con los datos modificados
        mockMvc.perform(put("/api/v1/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Teclado Mecanico RGB"))
                .andExpect(jsonPath("$.cantidad").value(3));

        verify(pedidoService, times(1)).actualizar(eq(1L), any(PedidoUpdateRequest.class));
    }

    @Test
    void eliminarPedidoExito() throws Exception {
        // CONFIGURACIÓN: Simular eliminación void exitosa
        doNothing().when(pedidoService).eliminar(1L);

        // EJECUCIÓN y VERIFICACIÓN: Enviar petición DELETE y esperar status 204 NO CONTENT
        mockMvc.perform(delete("/api/v1/pedidos/1"))
                .andExpect(status().isNoContent());

        verify(pedidoService, times(1)).eliminar(1L);
    }
}

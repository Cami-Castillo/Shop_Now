package com.shopnow.cliente.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopnow.cliente.dto.ClienteCreateRequest;
import com.shopnow.cliente.dto.ClienteResponse;
import com.shopnow.cliente.dto.ClienteUpdateRequest;
import com.shopnow.cliente.model.Role;
import com.shopnow.cliente.service.ClienteService;
import com.shopnow.cliente.service.PedidoGatewayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private PedidoGatewayService pedidoGatewayService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void obtenerClientesRetornaLista() throws Exception {
        // CONFIGURACIÓN: Simular respuesta del servicio
        ClienteResponse cliente = new ClienteResponse(1, "Benjamin", "benjamin@shopnow.com", "Santiago", Role.ROLE_USER);
        when(clienteService.obtenerClientes()).thenReturn(List.of(cliente));

        // EJECUCIÓN y VERIFICACIÓN: Simular petición GET HTTP y validar respuesta JSON y status 200 OK
        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Benjamin"))
                .andExpect(jsonPath("$[0].correo").value("benjamin@shopnow.com"));

        verify(clienteService, times(1)).obtenerClientes();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void buscarPorIdExito() throws Exception {
        // CONFIGURACIÓN: Simular que se encuentra el cliente en el servicio
        ClienteResponse cliente = new ClienteResponse(1, "Benjamin", "benjamin@shopnow.com", "Santiago", Role.ROLE_USER);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);

        // EJECUCIÓN y VERIFICACIÓN: Simular petición GET con ID 1
        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Benjamin"));

        verify(clienteService, times(1)).buscarPorId(1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void buscarPorIdNoExisteRetorna404() throws Exception {
        // CONFIGURACIÓN: Simular que el servicio lanza 404 al no encontrar el ID 99
        when(clienteService.buscarPorId(99)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        // EJECUCIÓN y VERIFICACIÓN: Simular petición GET con ID 99 y esperar status 404 NOT FOUND
        mockMvc.perform(get("/api/v1/clientes/99"))
                .andExpect(status().isNotFound());

        verify(clienteService, times(1)).buscarPorId(99);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void guardarClienteExito() throws Exception {
        ClienteCreateRequest request = new ClienteCreateRequest("Benjamin", "benjamin@shopnow.com", "pass123", "Santiago");
        ClienteResponse response = new ClienteResponse(1, "Benjamin", "benjamin@shopnow.com", "Santiago", Role.ROLE_USER);
        
        // CONFIGURACIÓN: Simular registro del cliente en el servicio
        when(clienteService.registrar(any(ClienteCreateRequest.class))).thenReturn(response);

        // EJECUCIÓN y VERIFICACIÓN: Simular petición POST enviando el JSON del DTO y esperar 201 CREATED
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Benjamin"));

        verify(clienteService, times(1)).registrar(any(ClienteCreateRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void guardarClienteCuerpoInvalidoRetorna400() throws Exception {
        // Petición con campos vacíos que violan las validaciones de las propiedades (@NotBlank)
        ClienteCreateRequest request = new ClienteCreateRequest("", "", "", "");

        // EJECUCIÓN y VERIFICACIÓN: Enviar petición POST errónea y esperar error 400 BAD REQUEST
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Asegurar que el servicio de persistencia nunca llegó a ser llamado
        verify(clienteService, never()).registrar(any(ClienteCreateRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizarClienteExito() throws Exception {
        ClienteUpdateRequest request = new ClienteUpdateRequest("Benjamin Updated", "benjamin@shopnow.com", "pass123", "Santiago", Role.ROLE_USER);
        ClienteResponse response = new ClienteResponse(1, "Benjamin Updated", "benjamin@shopnow.com", "Santiago", Role.ROLE_USER);

        // CONFIGURACIÓN: Simular la actualización exitosa en el servicio
        when(clienteService.actualizar(eq(1), any(ClienteUpdateRequest.class))).thenReturn(response);

        // EJECUCIÓN y VERIFICACIÓN: Simular petición PUT y esperar 200 OK
        mockMvc.perform(put("/api/v1/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Benjamin Updated"));

        verify(clienteService, times(1)).actualizar(eq(1), any(ClienteUpdateRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eliminarClienteExito() throws Exception {
        // CONFIGURACIÓN: Simular eliminación del servicio sin retornar valor (void)
        doNothing().when(clienteService).eliminar(1);

        // EJECUCIÓN y VERIFICACIÓN: Simular petición DELETE y verificar mensaje JSON de confirmación
        mockMvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Cliente eliminado"));

        verify(clienteService, times(1)).eliminar(1);
    }
}

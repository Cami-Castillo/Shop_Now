package com.shopnow.cliente.service;

import com.shopnow.cliente.dto.ClienteCreateRequest;
import com.shopnow.cliente.dto.ClienteDTO;
import com.shopnow.cliente.dto.ClienteResponse;
import com.shopnow.cliente.dto.ClienteUpdateRequest;
import com.shopnow.cliente.model.Cliente;
import com.shopnow.cliente.model.Role;
import com.shopnow.cliente.repository.ClienteRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTests {

    @Mock
    private ClienteRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ClienteService service;

    private Cliente clienteMock;

    @BeforeEach
    void setUp() {
        // Inicializar objeto simulado de Cliente para usar en los tests
        clienteMock = new Cliente(1, "Benjamin", "benjamin@shopnow.com", "encoded-pass", "Santiago", Role.ROLE_USER);
        // Inyectar manualmente el EntityManager mockeado usando ReflectionTestUtils
        org.springframework.test.util.ReflectionTestUtils.setField(service, "entityManager", entityManager);
    }

    @Test
    void obtenerClientesRetornaLista() {
        // CONFIGURACIÓN (Mock): Simular que el repositorio devuelve una lista con nuestro cliente mock
        when(repository.findAll()).thenReturn(List.of(clienteMock));

        // EJECUCIÓN: Llamar al método del servicio
        List<ClienteResponse> resultado = service.obtenerClientes();

        // VERIFICACIÓN: Comprobar que la respuesta no sea nula, tenga tamaño 1 y los datos coincidan
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Benjamin", resultado.get(0).nombre());
        verify(repository, times(1)).findAll();
    }

    @Test
    void buscarPorIdExito() {
        // CONFIGURACIÓN: Simular que se encuentra el cliente con ID 1
        when(repository.findById(1)).thenReturn(Optional.of(clienteMock));

        // EJECUCIÓN: Llamar al método del servicio
        ClienteResponse resultado = service.buscarPorId(1);

        // VERIFICACIÓN: Validar que el cliente retornado es el esperado
        assertNotNull(resultado);
        assertEquals("Benjamin", resultado.nombre());
        verify(repository, times(1)).findById(1);
    }

    @Test
    void buscarPorIdNoExisteLanzaNotFound() {
        // CONFIGURACIÓN: Simular que el ID 99 no existe en la BD
        when(repository.findById(99)).thenReturn(Optional.empty());

        // EJECUCIÓN y VERIFICACIÓN: Asegurar que se lance la excepción 404 Not Found
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.buscarPorId(99));
        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Cliente no encontrado", exception.getReason());
    }

    @Test
    void registrarExito() {
        ClienteCreateRequest request = new ClienteCreateRequest("Benjamin", "benjamin@shopnow.com", "pass123", "Santiago");
        
        // CONFIGURACIÓN:
        // 1. Simular que el correo está disponible (no existe en BD)
        when(repository.findByCorreo("benjamin@shopnow.com")).thenReturn(Optional.empty());
        // 2. Simular el cifrado de la contraseña
        when(passwordEncoder.encode("pass123")).thenReturn("encoded-pass");
        // 3. Simular el guardado en el repositorio
        when(repository.save(any(Cliente.class))).thenReturn(clienteMock);

        // EJECUCIÓN: Registrar al cliente
        ClienteResponse resultado = service.registrar(request);

        // VERIFICACIÓN: Comprobar el retorno exitoso y que se llamó a la BD
        assertNotNull(resultado);
        assertEquals("Benjamin", resultado.nombre());
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    void registrarCorreoDuplicadoLanzaConflict() {
        ClienteCreateRequest request = new ClienteCreateRequest("Benjamin", "benjamin@shopnow.com", "pass123", "Santiago");
        
        // CONFIGURACIÓN: Simular que el correo ya está en uso por otro cliente
        when(repository.findByCorreo("benjamin@shopnow.com")).thenReturn(Optional.of(clienteMock));

        // EJECUCIÓN y VERIFICACIÓN: Asegurar que lance excepción 409 Conflict
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.registrar(request));
        assertEquals(409, exception.getStatusCode().value());
        assertEquals("Ya existe un cliente con ese correo", exception.getReason());
    }

    @Test
    void actualizarExito() {
        ClienteUpdateRequest request = new ClienteUpdateRequest("Benjamin Updated", "benjamin@shopnow.com", "newpass", "Santiago", Role.ROLE_USER);
        
        // CONFIGURACIÓN:
        // 1. Simular que el cliente a actualizar existe
        when(repository.findById(1)).thenReturn(Optional.of(clienteMock));
        // 2. Simular que el nuevo correo no choca con otros IDs
        when(repository.findByCorreo("benjamin@shopnow.com")).thenReturn(Optional.empty());
        // 3. Simular codificación de contraseña
        when(passwordEncoder.encode("newpass")).thenReturn("new-encoded-pass");
        // 4. Simular persistencia y retorno del objeto modificado
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUCIÓN: Actualizar el cliente
        ClienteResponse resultado = service.actualizar(1, request);

        // VERIFICACIÓN: Validar los campos modificados
        assertNotNull(resultado);
        assertEquals("Benjamin Updated", resultado.nombre());
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    void actualizarNoExisteLanzaNotFound() {
        ClienteUpdateRequest request = new ClienteUpdateRequest("Benjamin Updated", "benjamin@shopnow.com", "newpass", "Santiago", Role.ROLE_USER);
        
        // CONFIGURACIÓN: Simular que el ID 99 no existe
        when(repository.findById(99)).thenReturn(Optional.empty());

        // EJECUCIÓN y VERIFICACIÓN: Verificar error 404
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.actualizar(99, request));
        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Cliente no existe", exception.getReason());
    }

    @Test
    void eliminarExito() {
        // CONFIGURACIÓN: Simular que el cliente existe
        when(repository.existsById(1)).thenReturn(true);
        doNothing().when(repository).deleteById(1);

        // EJECUCIÓN: Eliminar cliente
        assertDoesNotThrow(() -> service.eliminar(1));

        // VERIFICACIÓN: Comprobar invocación al repositorio
        verify(repository, times(1)).deleteById(1);
    }

    @Test
    void eliminarNoExisteLanzaNotFound() {
        // CONFIGURACIÓN: Simular que el cliente no existe
        when(repository.existsById(99)).thenReturn(false);

        // EJECUCIÓN y VERIFICACIÓN: Verificar error 404
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.eliminar(99));
        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Cliente no existe", exception.getReason());
    }

    @Test
    void cargarMasivoExito() {
        // Creación de lista con 1 cliente DTO
        List<ClienteDTO> dtos = new ArrayList<>();
        ClienteDTO dto1 = new ClienteDTO();
        dto1.setNombre("Juan");
        dto1.setCorreo("juan@shopnow.com");
        dto1.setPassword("pass1");
        dto1.setDireccion("Direccion 1");
        dtos.add(dto1);

        // CONFIGURACIÓN:
        // 1. Simular la codificación de la password del lote
        when(passwordEncoder.encode("pass1")).thenReturn("encoded-pass1");
        // 2. Simular métodos de persistencia del EntityManager de Hibernate
        doNothing().when(entityManager).persist(any(Cliente.class));
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).clear();

        // EJECUCIÓN: Realizar carga masiva
        String resultado = service.cargarMasivo(dtos);

        // VERIFICACIÓN: Comprobar mensaje de éxito y llamadas del EntityManager
        assertNotNull(resultado);
        assertTrue(resultado.startsWith("Exito: 1"));
        verify(entityManager, times(1)).persist(any(Cliente.class));
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();
    }
}

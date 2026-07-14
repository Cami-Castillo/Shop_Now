package com.shopnow.cliente.service;

import com.shopnow.cliente.dto.ClienteCreateRequest;
import com.shopnow.cliente.dto.ClienteDTO;
import com.shopnow.cliente.dto.ClienteResponse;
import com.shopnow.cliente.dto.ClienteUpdateRequest;
import com.shopnow.cliente.model.Cliente;
import com.shopnow.cliente.model.Role;
import com.shopnow.cliente.repository.ClienteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public ClienteService(ClienteRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> obtenerClientes() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(int id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }

    public ClienteResponse registrar(ClienteCreateRequest request) {
        Cliente cliente = buildCliente(request);
        validateCorreoDisponible(cliente.getCorreo(), null);
        return toResponse(repository.save(cliente));
    }

    public ClienteResponse actualizar(int id, ClienteUpdateRequest request) {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no existe"));

        String correoNormalizado = normalizeCorreo(request.correo());
        validateCorreoDisponible(correoNormalizado, id);

        existente.setNombre(clean(request.nombre(), "El nombre es obligatorio"));
        existente.setCorreo(correoNormalizado);
        existente.setDireccion(clean(request.direccion(), "La direccion es obligatoria"));
        existente.setRole(request.role() != null ? request.role() : existente.getRole());

        if (request.password() != null && !request.password().isBlank()) {
            existente.setPassword(passwordEncoder.encode(request.password().trim()));
        }

        return toResponse(repository.save(existente));
    }

    public void eliminar(int id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no existe");
        }

        repository.deleteById(id);
    }

    public String cargarMasivo(List<ClienteDTO> clientes) {
        int batchSize = 50;
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < clientes.size(); i++) {
            ClienteDTO dto = clientes.get(i);
            Cliente cliente = new Cliente();
            cliente.setNombre(clean(dto.getNombre(), "El nombre es obligatorio"));
            cliente.setCorreo(normalizeCorreo(dto.getCorreo()));
            cliente.setPassword(passwordEncoder.encode(clean(dto.getPassword(), "La password es obligatoria")));
            cliente.setDireccion(clean(dto.getDireccion(), "La direccion es obligatoria"));
            cliente.setRole(Role.ROLE_USER);

            entityManager.persist(cliente);

            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        entityManager.flush();
        entityManager.clear();

        long fin = System.currentTimeMillis();
        return "Exito: " + clientes.size() + " procesados en " + (fin - inicio) + "ms";
    }

    private void validateCorreoDisponible(String correo, Integer currentId) {
        repository.findByCorreo(correo).ifPresent(cliente -> {
            if (currentId == null || !cliente.getId().equals(currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese correo");
            }
        });
    }

    private Cliente buildCliente(ClienteCreateRequest request) {
        return new Cliente(
                null,
                clean(request.nombre(), "El nombre es obligatorio"),
                normalizeCorreo(request.correo()),
                passwordEncoder.encode(clean(request.password(), "La password es obligatoria")),
                clean(request.direccion(), "La direccion es obligatoria"),
                Role.ROLE_USER
        );
    }

    private String normalizeCorreo(String correo) {
        return clean(correo, "El correo es obligatorio").toLowerCase(Locale.ROOT);
    }

    private String clean(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }

        return value.trim();
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getCorreo(),
                cliente.getDireccion(),
                cliente.getRole()
        );
    }
}

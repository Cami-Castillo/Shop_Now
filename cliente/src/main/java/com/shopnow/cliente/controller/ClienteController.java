package com.shopnow.cliente.controller;

import com.shopnow.cliente.dto.ClienteCreateRequest;
import com.shopnow.cliente.dto.ClienteDTO;
import com.shopnow.cliente.dto.ClienteResponse;
import com.shopnow.cliente.dto.ClienteUpdateRequest;
import com.shopnow.cliente.dto.PedidoRemotoResponse;
import com.shopnow.cliente.service.ClienteService;
import com.shopnow.cliente.service.PedidoGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Gestion de clientes de ShopNow")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService service;
    private final PedidoGatewayService pedidoGatewayService;

    public ClienteController(ClienteService service, PedidoGatewayService pedidoGatewayService) {
        this.service = service;
        this.pedidoGatewayService = pedidoGatewayService;
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public ResponseEntity<List<ClienteResponse>> obtenerClientes() {
        return ResponseEntity.ok(service.obtenerClientes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/{id}/pedidos")
    @Operation(summary = "Listar pedidos de un cliente")
    public ResponseEntity<List<PedidoRemotoResponse>> obtenerPedidosDelCliente(@PathVariable int id) {
        service.buscarPorId(id);
        return ResponseEntity.ok(pedidoGatewayService.obtenerPedidosPorCliente(id));
    }

    @PostMapping
    @Operation(summary = "Registrar cliente")
    public ResponseEntity<ClienteResponse> guardar(@Valid @RequestBody ClienteCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable int id,
            @Valid @RequestBody ClienteUpdateRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable int id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado"));
    }

    @PostMapping("/masivo")
    @Operation(summary = "Cargar clientes masivamente")
    public ResponseEntity<?> cargaMasiva(@Valid @RequestBody List<ClienteDTO> clientes) {
        try {
            if (clientes == null || clientes.isEmpty()) {
                return ResponseEntity.badRequest().body("La lista esta vacia");
            }

            return ResponseEntity.ok(service.cargarMasivo(clientes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la carga: " + e.getMessage());
        }
    }
}

package com.shopnow.pedido.service;

import com.shopnow.pedido.dto.ClienteRemotoResponse;
import com.shopnow.pedido.dto.PedidoCreateRequest;
import com.shopnow.pedido.dto.PedidoResponse;
import com.shopnow.pedido.dto.PedidoUpdateRequest;
import com.shopnow.pedido.model.Pedido;
import com.shopnow.pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteGatewayService clienteGatewayService;

    public PedidoService(PedidoRepository pedidoRepository, ClienteGatewayService clienteGatewayService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteGatewayService = clienteGatewayService;
    }

    public List<PedidoResponse> listar() {
        return pedidoRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapearConCliente)
                .toList();
    }

    public List<PedidoResponse> listarPorClienteId(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByCreatedAtDesc(clienteId).stream()
                .map(this::mapearConCliente)
                .toList();
    }

    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pedido no encontrado"));
        return mapearConCliente(pedido);
    }

    public PedidoResponse crear(PedidoCreateRequest request) {
        ClienteRemotoResponse cliente = clienteGatewayService.obtenerClientePorId(request.clienteId());

        Pedido pedido = new Pedido();
        pedido.setClienteId(request.clienteId());
        pedido.setDescripcion(request.descripcion());
        pedido.setCantidad(request.cantidad());

        Pedido guardado = pedidoRepository.save(pedido);
        return mapear(guardado, cliente);
    }

    public List<PedidoResponse> crearMasivo(List<PedidoCreateRequest> requests) {
        return requests.stream()
                .map(this::crear)
                .toList();
    }

    public PedidoResponse actualizar(Long id, PedidoUpdateRequest request) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pedido no encontrado"));
        
        pedido.setDescripcion(request.descripcion());
        pedido.setCantidad(request.cantidad());
        
        Pedido actualizado = pedidoRepository.save(pedido);
        return mapearConCliente(actualizado);
    }

    public void eliminar(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Pedido no encontrado");
        }
        pedidoRepository.deleteById(id);
    }

    private PedidoResponse mapearConCliente(Pedido pedido) {
        ClienteRemotoResponse cliente = clienteGatewayService.obtenerClientePorId(pedido.getClienteId());
        return mapear(pedido, cliente);
    }

    private PedidoResponse mapear(Pedido pedido, ClienteRemotoResponse cliente) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getClienteId(),
                cliente.nombre(),
                cliente.correo(),
                pedido.getDescripcion(),
                pedido.getCantidad(),
                pedido.getCreatedAt()
        );
    }
}

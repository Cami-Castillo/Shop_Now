package com.shopnow.pedido.repository;

import com.shopnow.pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByCreatedAtDesc();

    List<Pedido> findByClienteIdOrderByCreatedAtDesc(Long clienteId);
}

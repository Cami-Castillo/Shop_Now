package com.shopnow.cliente.repository;

import com.shopnow.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Cliente> findAllByCorreoIn(Collection<String> correos);
}

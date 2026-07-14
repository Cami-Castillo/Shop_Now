package com.shopnow.cliente.config;

import com.shopnow.cliente.model.Cliente;
import com.shopnow.cliente.model.Role;
import com.shopnow.cliente.repository.ClienteRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (clienteRepository.count() == 0) {
            Faker faker = new Faker();
            for (int i = 0; i < 20; i++) {
                Cliente cliente = new Cliente();
                cliente.setNombre(faker.name().fullName());
                cliente.setCorreo(faker.internet().emailAddress());
                cliente.setPassword(passwordEncoder.encode("password123"));
                cliente.setDireccion(faker.address().fullAddress());
                cliente.setRole(Role.ROLE_USER);
                clienteRepository.save(cliente);
            }
            System.out.println("DataLoader: Creados 20 clientes de prueba usando Faker.");
        }
    }
}

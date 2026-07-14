package com.shopnow.pedido.config;

import com.shopnow.pedido.model.Pedido;
import com.shopnow.pedido.repository.PedidoRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    private final PedidoRepository pedidoRepository;

    public DataLoader(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (pedidoRepository.count() == 0) {
            Faker faker = new Faker();
            Random random = new Random();
            for (int i = 0; i < 15; i++) {
                Pedido pedido = new Pedido();
                // Asignar IDs de clientes ficticios (por ejemplo entre 1 y 20)
                pedido.setClienteId((long) (random.nextInt(20) + 1));
                pedido.setDescripcion(faker.commerce().productName());
                pedido.setCantidad(faker.number().numberBetween(1, 10));
                pedidoRepository.save(pedido);
            }
            System.out.println("DataLoader: Creados 15 pedidos de prueba usando Faker.");
        }
    }
}

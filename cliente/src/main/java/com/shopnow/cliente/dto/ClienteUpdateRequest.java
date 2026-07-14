package com.shopnow.cliente.dto;

import com.shopnow.cliente.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteUpdateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String correo,
        String password,
        @NotBlank(message = "La dirección es obligatoria")
        String direccion,
        Role role
) {
}

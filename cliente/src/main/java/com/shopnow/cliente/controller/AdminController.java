package com.shopnow.cliente.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administracion", description = "Endpoints disponibles solo para usuarios administradores")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @GetMapping("/test")
    @Operation(summary = "Probar acceso administrador")
    public String admin() {
        return "Acceso solo ADMIN";
    }
}

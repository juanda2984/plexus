package com.org.plane.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.plane.model.Ship;
import com.org.plane.service.impl.ShipServiceImpl;
import com.org.plane.util.KafkaProducer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/ships")
public class ShipController {
	
	private final ShipServiceImpl shipService;
    private final KafkaProducer shipKafkaProducer;

    @Autowired
    public ShipController(ShipServiceImpl shipService, KafkaProducer shipKafkaProducer) {
        this.shipService = shipService;
        this.shipKafkaProducer = shipKafkaProducer;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las naves", description = "Devuelve una lista paginada de todas las naves.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Naves obtenidas correctamente."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public Page<Ship> getAllShips(Pageable pageable) {
        return shipService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una nave por ID", description = "Devuelve la nave correspondiente al ID proporcionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nave obtenida correctamente."),
        @ApiResponse(responseCode = "404", description = "Nave no encontrada."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<Ship> getShipById(@Parameter(description = "ID de la nave a obtener", required = true) @PathVariable Long id) {
        Optional<Ship> ship = shipService.findById(id);
        return ship.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar naves por nombre", description = "Devuelve una lista paginada de naves que coinciden con el nombre proporcionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Naves encontradas correctamente."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public Page<Ship> searchByName(@Parameter(description = "Nombre de la nave a buscar", required = true) @RequestParam String name, Pageable pageable) {
        return shipService.findByName(name, pageable);
    }

    @PostMapping
    @Operation(summary = "Crear una nueva nave", description = "Crea una nueva nave y la devuelve.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Nave creada correctamente."),
        @ApiResponse(responseCode = "400", description = "Solicitud no válida."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public Ship createShip(@RequestBody Ship ship) {
        Ship createdShip = shipService.save(ship);
        shipKafkaProducer.sendMessage("ship-topic", "Nave creada: " + createdShip.toJson());
        return createdShip;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una nave existente", description = "Actualiza la nave correspondiente al ID proporcionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nave actualizada correctamente."),
        @ApiResponse(responseCode = "404", description = "Nave no encontrada."),
        @ApiResponse(responseCode = "400", description = "Solicitud no válida."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<Ship> updateShip(@Parameter(description = "ID de la nave a actualizar", required = true) @PathVariable Long id, @RequestBody Ship shipDetails) {
        Optional<Ship> ship = shipService.findById(id);
        if (!ship.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        shipDetails.setId(id);
        shipKafkaProducer.sendMessage("ship-topic", "Nave actualizada: " + shipDetails.toJson());
        return ResponseEntity.ok(shipService.save(shipDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una nave", description = "Elimina la nave correspondiente al ID proporcionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Nave eliminada correctamente."),
        @ApiResponse(responseCode = "404", description = "Nave no encontrada."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<Void> deleteShip(@Parameter(description = "ID de la nave a eliminar", required = true) @PathVariable Long id) {
        Optional<Ship> ship = shipService.findById(id);
        if (!ship.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        shipKafkaProducer.sendMessage("ship-topic", "Nave eliminada: " + ship.get().toJson());
        shipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.metrix.controller;

import com.example.metrix.model.Boleto;
import com.example.metrix.model.Compra;
import com.example.metrix.model.Funcion;
import com.example.metrix.repository.CompraRepository;
import com.example.metrix.repository.FuncionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/compra")
public class CompraController {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private FuncionRepository funcionRepository;

    @CrossOrigin
    @PostMapping("registrar-compra")
    @Transactional
    public ResponseEntity<?> registrarCompra(@RequestBody Compra compra) {
        Optional<Funcion> funcionOptional = funcionRepository.findById(compra.getFuncion().getId());
        if (!funcionOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La función no existe.");
        }

        Funcion funcion = funcionOptional.get();
        List<Boleto> boletos = compra.getBoletos();

        for (Boleto boleto : boletos) {
            if (boleto.getCodigo() == null || boleto.getCodigo().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cada boleto debe tener un código válido.");
            }
            boleto.setFuncion(funcion);
            boleto.setCompra(compra);
        }

        double montoTotal = boletos.size() * funcion.getPrecioBoleto();
        compra.setMonto(montoTotal);

        Compra compraGuardada = compraRepository.save(compra);
        funcion.setDineroRecaudado(funcion.getDineroRecaudado() + montoTotal);
        funcionRepository.save(funcion);

        return ResponseEntity.status(HttpStatus.CREATED).body(compraGuardada);
    }

    @CrossOrigin
    @GetMapping("asientos-ocupados/{idFuncion}")
    public ResponseEntity<?> obtenerAsientosOcupados(@PathVariable Integer idFuncion) {
        Optional<Funcion> funcionOptional = funcionRepository.findById(idFuncion);

        if (!funcionOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La función no existe.");
        }

        Funcion funcion = funcionOptional.get();
        List<String> asientosOcupados = funcion.getBoletosVendidos().stream()
                                               .map(Boleto::getCodigo)
                                               .collect(Collectors.toList());

        return ResponseEntity.ok(asientosOcupados);
    }

    // Nuevo endpoint para obtener todas las compras realizadas
    @CrossOrigin
    @GetMapping("/todas")
    public ResponseEntity<List<Compra>> obtenerTodasLasCompras() {
        List<Compra> compras = compraRepository.findAll();
        return ResponseEntity.ok(compras);
    }

    // Endpoint para cancelar una compra por ID
    @CrossOrigin
    @DeleteMapping("/cancelar/{idCompra}")
    @Transactional
    public ResponseEntity<?> cancelarCompra(@PathVariable Integer idCompra) {
        Optional<Compra> compraOptional = compraRepository.findById(idCompra);

        if (!compraOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La compra no existe.");
        }

        Compra compra = compraOptional.get();
        Funcion funcion = compra.getFuncion();

        // Revertir el dinero recaudado
        funcion.setDineroRecaudado(funcion.getDineroRecaudado() - compra.getMonto());
        funcionRepository.save(funcion);

        compraRepository.delete(compra);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

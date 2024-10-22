package com.example.metrix.controller;

import com.example.metrix.model.DatosHistoricos;
import com.example.metrix.model.Funcion;
import com.example.metrix.repository.DatosHistoricosRepository;
import com.example.metrix.repository.FuncionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/estadisticas")
public class EstadisticasController {

    @Autowired
    private DatosHistoricosRepository datosHistoricosRepository;

    @Autowired
    private FuncionRepository funcionRepository;

    @CrossOrigin
    @GetMapping("/generales")
    public DatosHistoricos obtenerEstadisticasGenerales() {
        // Obtener el registro de datos históricos (asumiendo que solo hay uno)
        Optional<DatosHistoricos> datosHistoricosOptional = datosHistoricosRepository.findById(1);
        if (datosHistoricosOptional.isPresent()) {
            return datosHistoricosOptional.get();
        } else {
            // Retornar un objeto vacío o con valores por defecto si no existe
            DatosHistoricos datosHistoricos = new DatosHistoricos();
            datosHistoricos.setTotalDeVentas(0.0);
            datosHistoricos.setTotalBoletosOfertados(0);
            datosHistoricos.setTotalAsientosOcupados(0);
            datosHistoricos.setNumeroDeFuncionesImpartidas(0);
            return datosHistoricos;
        }
    }

    @CrossOrigin
    @GetMapping("/popular")
    public String obtenerFuncionMasPopular() {
        List<Funcion> funciones = funcionRepository.findAll();
        Funcion funcionMasPopular = null;
        int maxAsientosOcupados = 0;

        // Buscar la función con más boletos vendidos
        for (Funcion funcion : funciones) {
            int asientosOcupados = funcion.getBoletosVendidos().size();
            if (asientosOcupados > maxAsientosOcupados) {
                maxAsientosOcupados = asientosOcupados;
                funcionMasPopular = funcion;
            }
        }

        // Retornar el título de la función más popular
        return funcionMasPopular != null ? funcionMasPopular.getPelicula().getTitulo() : "N/A";
    }

    @CrossOrigin
    @GetMapping("/asistencia")
    public double obtenerPorcentajeAsistencia() {
        List<Funcion> funciones = funcionRepository.findAll();
        int totalAsientosOfertados = 0;
        int totalAsientosOcupados = 0;

        for (Funcion funcion : funciones) {
            totalAsientosOfertados += 88; // Suponiendo que cada función tiene 88 asientos ofertados
            totalAsientosOcupados += funcion.getBoletosVendidos().size();
        }

        return totalAsientosOfertados > 0 ? (totalAsientosOcupados * 100.0 / totalAsientosOfertados) : 0.0;
    }
}

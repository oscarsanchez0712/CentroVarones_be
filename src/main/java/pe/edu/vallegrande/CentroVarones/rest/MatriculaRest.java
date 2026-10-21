package pe.edu.vallegrande.CentroVarones.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.CentroVarones.model.Matricula;
import pe.edu.vallegrande.CentroVarones.service.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaRest {

    private final MatriculaService service;

    public MatriculaRest(MatriculaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Matricula> listar() {
        return service.listar();
    }

    @GetMapping("/{numeroSolicitud}")
    public Matricula buscarPorSolicitud(@PathVariable String numeroSolicitud) {
        return service.buscarPorSolicitud(numeroSolicitud);
    }

    @GetMapping("/dni/{dni}")
    public Matricula buscarPorDni(@PathVariable String dni) {
        return service.buscarPorDni(dni);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Matricula crear(@Valid @RequestBody Matricula matricula) {
        return service.crear(matricula);
    }

    @PutMapping("/{numeroSolicitud}/aprobar")
    public Matricula aprobar(@PathVariable String numeroSolicitud) {
        return service.aprobar(numeroSolicitud);
    }
}

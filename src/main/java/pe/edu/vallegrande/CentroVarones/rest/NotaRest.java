package pe.edu.vallegrande.CentroVarones.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.CentroVarones.model.Nota;
import pe.edu.vallegrande.CentroVarones.service.NotaService;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
public class NotaRest {

    private final NotaService service;

    public NotaRest(NotaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Nota> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Nota crear(@Valid @RequestBody Nota nota) {
        return service.crear(nota);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}

package pe.edu.vallegrande.CentroVarones.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.vallegrande.CentroVarones.model.Matricula;
import pe.edu.vallegrande.CentroVarones.repository.MatriculaRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository repository;

    public MatriculaService(MatriculaRepository repository) {
        this.repository = repository;
    }

    public List<Matricula> listar() {
        return repository.findAll();
    }

    public Matricula buscarPorSolicitud(String numeroSolicitud) {
        return repository.findById(numeroSolicitud.trim().toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula no encontrada"));
    }

    public Matricula buscarPorDni(String dni) {
        return repository.findFirstByEstudianteDniOrderByNumeroSolicitudDesc(dni.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay matrícula para ese DNI"));
    }

    public synchronized Matricula crear(Matricula matricula) {
        matricula.setNumeroSolicitud(generarNumeroSolicitud());
        matricula.setFecha(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        matricula.setEstado(Matricula.EN_REVISION);
        return repository.save(matricula);
    }

    public Matricula aprobar(String numeroSolicitud) {
        Matricula matricula = buscarPorSolicitud(numeroSolicitud);
        matricula.setEstado(Matricula.APROBADA);
        return repository.save(matricula);
    }

    /** Formato MAT-2026-000125 */
    private String generarNumeroSolicitud() {
        long siguiente = repository.count() + 1;
        String numero;
        do {
            numero = String.format("MAT-2026-%06d", siguiente++);
        } while (repository.existsById(numero));
        return numero;
    }
}

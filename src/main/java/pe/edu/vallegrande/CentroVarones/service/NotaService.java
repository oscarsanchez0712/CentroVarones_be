package pe.edu.vallegrande.CentroVarones.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.vallegrande.CentroVarones.model.Cursos;
import pe.edu.vallegrande.CentroVarones.model.Matricula;
import pe.edu.vallegrande.CentroVarones.model.Nota;
import pe.edu.vallegrande.CentroVarones.repository.MatriculaRepository;
import pe.edu.vallegrande.CentroVarones.repository.NotaRepository;

import java.util.List;

@Service
public class NotaService {

    private final NotaRepository notas;
    private final MatriculaRepository matriculas;

    public NotaService(NotaRepository notas, MatriculaRepository matriculas) {
        this.notas = notas;
        this.matriculas = matriculas;
    }

    public List<Nota> listar() {
        return notas.findAll();
    }

    public Nota crear(Nota nota) {
        Matricula matricula = matriculas.findFirstByEstudianteDniOrderByNumeroSolicitudDesc(nota.getDni())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estudiante no está matriculado"));
        if (!Cursos.porGrado(matricula.getEstudiante().getGrado()).contains(nota.getCurso())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El curso no corresponde al nivel del estudiante");
        }
        nota.setId(null);
        return notas.save(nota);
    }

    public void eliminar(Long id) {
        if (!notas.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota no encontrada");
        }
        notas.deleteById(id);
    }
}

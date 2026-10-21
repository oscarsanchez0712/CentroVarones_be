package pe.edu.vallegrande.CentroVarones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.vallegrande.CentroVarones.model.Matricula;

import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, String> {

    Optional<Matricula> findFirstByEstudianteDniOrderByNumeroSolicitudDesc(String dni);
}

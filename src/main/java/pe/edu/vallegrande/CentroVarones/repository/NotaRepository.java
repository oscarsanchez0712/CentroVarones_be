package pe.edu.vallegrande.CentroVarones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.vallegrande.CentroVarones.model.Nota;

public interface NotaRepository extends JpaRepository<Nota, Long> {
}

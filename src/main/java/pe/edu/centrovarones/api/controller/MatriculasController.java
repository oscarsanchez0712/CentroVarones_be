package pe.edu.centrovarones.api.controller;
import pe.edu.centrovarones.api.service.AcademicService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController @RequestMapping("/api/matriculas")
public class MatriculasController {
 private final AcademicService service; public MatriculasController(AcademicService s){service=s;}
 @GetMapping public List<Map<String,Object>> list(){return service.matriculas();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Map<String,Object> create(@RequestBody Map<String,Object> r){try{return service.createMatricula(r);}catch(DuplicateKeyException e){throw new ResponseStatusException(HttpStatus.CONFLICT,"El estudiante ya tiene matrícula registrada para este año.");}catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
 @GetMapping("/solicitud/{numero}") public Map<String,Object> solicitud(@PathVariable String numero){var x=service.bySolicitud(numero);if(x==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Solicitud no encontrada.");return x;}
 @GetMapping("/dni/{dni}") public Map<String,Object> dni(@PathVariable String dni){var x=service.byDni(dni);if(x==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No hay matrícula para ese DNI.");return x;}
 @PatchMapping("/{numero}/estado") public Map<String,Object> state(@PathVariable String numero,@RequestBody Map<String,String> body){try{var x=service.updateEstado(numero,body.getOrDefault("estado",""));if(x==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Solicitud no encontrada.");return x;}catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
}

package pe.edu.centrovarones.api.controller;
import pe.edu.centrovarones.api.service.AcademicService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController @RequestMapping("/api/notas")
public class NotasController { private final AcademicService s; public NotasController(AcademicService s){this.s=s;}
 @GetMapping public List<Map<String,Object>> list(){return s.notas();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Map<String,Object> create(@RequestBody Map<String,Object> r){try{return s.createNota(r);}catch(NoSuchElementException e){throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());}catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}catch(DuplicateKeyException e){throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());}}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable long id){if(!s.deleteNota(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
}

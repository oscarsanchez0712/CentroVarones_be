package pe.edu.centrovarones.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AcademicService {
  private final JdbcTemplate db;
  private final ObjectMapper json;
  public AcademicService(JdbcTemplate db, ObjectMapper json) { this.db=db; this.json=json; }
  private static final TypeReference<Map<String,Object>> MAP = new TypeReference<>() {};
  private Map<String,Object> read(String s) { try { return json.readValue(s, MAP); } catch(Exception e) { throw new IllegalStateException("No se pudo leer matrícula guardada",e); } }
  private String write(Object o) { try { return json.writeValueAsString(o); } catch(Exception e) { throw new IllegalArgumentException("Datos de matrícula no válidos",e); } }
  private Object get(Map<String,Object> map,String key) { return map.entrySet().stream().filter(e->e.getKey().equalsIgnoreCase(key)).map(Map.Entry::getValue).findFirst().orElse(null); }
  private Map<String,Object> object(Object v) { return v instanceof Map<?,?> m ? (Map<String,Object>)m : Map.of(); }
  private String text(Object o) { return o==null?"":String.valueOf(o).trim(); }
  private String level(String grade) { if(grade.toLowerCase().contains("primaria")) return "Primaria"; if(grade.toLowerCase().contains("secundaria")) return "Secundaria"; return ""; }
  private String suggestedGrade(int age) {
    return switch(age) {
      case 6 -> "1er Grado Primaria"; case 7 -> "2do Grado Primaria"; case 8 -> "3er Grado Primaria"; case 9 -> "4to Grado Primaria"; case 10 -> "5to Grado Primaria"; case 11 -> "6to Grado Primaria";
      case 12 -> "1er Grado Secundaria"; case 13 -> "2do Grado Secundaria"; case 14 -> "3er Grado Secundaria"; case 15 -> "4to Grado Secundaria"; case 16 -> "5to Grado Secundaria"; default -> "";
    };
  }
  private Map<String,Object> matriculaBy(String field,String value) {
    List<String> rows=db.query("SELECT TOP (1) DatosJson FROM dbo.Matriculas WHERE "+field+"=? ORDER BY MatriculaId DESC",(rs,n)->rs.getString(1),value);
    return rows.isEmpty()?null:read(rows.get(0));
  }
  public List<Map<String,Object>> matriculas() { return db.query("SELECT DatosJson FROM dbo.Matriculas ORDER BY MatriculaId DESC",(rs,n)->read(rs.getString(1))); }
  public Map<String,Object> bySolicitud(String n) { return matriculaBy("NumeroSolicitud",n); }
  public Map<String,Object> byDni(String dni) { return matriculaBy("DNI",dni); }
  @Transactional
  public Map<String,Object> createMatricula(Map<String,Object> request) {
    Map<String,Object> student=object(get(request,"estudiante")), guardian=object(get(request,"apoderado"));
    String dni=text(get(student,"dni")), grade=text(get(student,"grado")), dob=text(get(student,"fechaNacimiento"));
    if(!dni.matches("\\d{8}")) throw new IllegalArgumentException("El DNI del estudiante debe tener 8 dígitos.");
    if(text(get(guardian,"dni")).equals(dni)) throw new IllegalArgumentException("El DNI del apoderado debe ser distinto al del estudiante.");
    if(level(grade).isBlank()) throw new IllegalArgumentException("El grado debe ser de Primaria o Secundaria.");
    LocalDate birth;
    try { birth=LocalDate.parse(dob,DateTimeFormatter.ISO_LOCAL_DATE); } catch(Exception ex) { try { birth=LocalDate.parse(dob,DateTimeFormatter.ofPattern("MM/dd/yyyy")); } catch(Exception e) { throw new IllegalArgumentException("La fecha de nacimiento no es válida."); } }
    LocalDate cutoff=LocalDate.of(2026,3,31); if(birth.isAfter(cutoff)) throw new IllegalArgumentException("La fecha de nacimiento es posterior al corte escolar.");
    if(!suggestedGrade(Period.between(birth,cutoff).getYears()).equalsIgnoreCase(grade)) throw new IllegalArgumentException("El grado no corresponde a la fecha de nacimiento al 31 de marzo de 2026.");
    if(db.queryForObject("SELECT COUNT(*) FROM dbo.Matriculas WHERE AnioEscolar=2026 AND DNI=?",Integer.class,dni)>0) throw new DuplicateKeyException("Ya existe matrícula para ese DNI en 2026.");
    Long id=db.queryForObject("INSERT INTO dbo.Matriculas(NumeroSolicitud,AnioEscolar,DNI,Grado,Estado,DatosJson) OUTPUT INSERTED.MatriculaId VALUES(?,2026,?,?,N'EN REVISIÓN',N'{}')",Long.class,"TMP-"+UUID.randomUUID(),dni,grade);
    String number=String.format("MAT-2026-%06d",id);
    Map<String,Object> result=new LinkedHashMap<>(); result.put("numeroSolicitud",number); result.put("fechaRegistro",LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); result.put("estudiante",student); result.put("apoderado",guardian); result.put("documentos",get(request,"documentos")); result.put("estado","EN REVISIÓN");
    db.update("UPDATE dbo.Matriculas SET NumeroSolicitud=?,DatosJson=? WHERE MatriculaId=?",number,write(result),id); return result;
  }
  public Map<String,Object> updateEstado(String numero,String estado) {
    if(!List.of("APROBADA","EN REVISIÓN").contains(estado)) throw new IllegalArgumentException("Estado inválido.");
    Map<String,Object> row=bySolicitud(numero); if(row==null)return null; row.put("estado",estado);
    db.update("UPDATE dbo.Matriculas SET Estado=?,DatosJson=? WHERE NumeroSolicitud=?",estado,write(row),numero); return row;
  }
  public List<String> cursos(String nivel) {
    if(!List.of("primaria","secundaria").contains(nivel.toLowerCase())) throw new IllegalArgumentException("El nivel debe ser Primaria o Secundaria.");
    return db.query("SELECT Nombre FROM dbo.Cursos WHERE Nivel=? AND Activo=1 ORDER BY CursoId",(rs,n)->rs.getString(1),nivel.substring(0,1).toUpperCase()+nivel.substring(1).toLowerCase());
  }
  public List<Map<String,Object>> notas() { return db.query("SELECT n.NotaId AS id,RTRIM(m.DNI) AS dni,c.Nombre AS curso,n.Calificacion AS nota FROM dbo.Notas n JOIN dbo.Matriculas m ON m.MatriculaId=n.MatriculaId JOIN dbo.Cursos c ON c.CursoId=n.CursoId ORDER BY n.NotaId DESC",(rs,n)->Map.of("id",rs.getLong("id"),"dni",rs.getString("dni"),"curso",rs.getString("curso"),"nota",rs.getBigDecimal("nota"))); }
  public Map<String,Object> createNota(Map<String,Object> req) {
    String dni=text(req.get("dni")), course=text(req.get("curso")); double score;
    try { score=Double.parseDouble(String.valueOf(req.get("nota"))); } catch(Exception e) { throw new IllegalArgumentException("La nota debe ser numérica."); }
    if(score<0||score>20) throw new IllegalArgumentException("La nota debe estar entre 0 y 20.");
    Map<String,Object> mat=byDni(dni); if(mat==null)throw new NoSuchElementException("No existe matrícula para ese DNI.");
    String grade=text(get(object(get(mat,"estudiante")),"grado")),lvl=level(grade); if(lvl.isBlank())throw new IllegalArgumentException("No se reconoce el nivel del grado.");
    List<Integer> cid=db.query("SELECT CursoId FROM dbo.Cursos WHERE Nombre=? AND Nivel=? AND Activo=1",(rs,n)->rs.getInt(1),course,lvl); if(cid.isEmpty())throw new IllegalArgumentException("El curso no corresponde al nivel del estudiante.");
    try { Long id=db.queryForObject("INSERT INTO dbo.Notas(MatriculaId,CursoId,Calificacion) OUTPUT INSERTED.NotaId SELECT MatriculaId,?,? FROM dbo.Matriculas WHERE NumeroSolicitud=?",Long.class,cid.get(0),score,text(get(mat,"numeroSolicitud"))); return Map.of("id",id,"dni",dni,"curso",course,"nota",score); }
    catch(DuplicateKeyException e) { throw new DuplicateKeyException("Ya existe una nota para ese estudiante y curso."); }
  }
  public boolean deleteNota(long id) { return db.update("DELETE FROM dbo.Notas WHERE NotaId=?",id)>0; }
}

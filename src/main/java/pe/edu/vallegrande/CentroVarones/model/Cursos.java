package pe.edu.vallegrande.CentroVarones.model;

import java.util.List;

/** Cursos permitidos según el nivel del estudiante (indicado por el profesor). */
public final class Cursos {

    private Cursos() {}

    public static final List<String> PRIMARIA = List.of(
            "Matemática", "Comunicación", "Personal Social", "Ciencia y Tecnología",
            "Arte y Cultura", "Educación Física", "Educación Religiosa", "Tutoría y Orientación Educativa");

    public static final List<String> SECUNDARIA = List.of(
            "Matemática", "Comunicación", "Inglés", "Ciencias Sociales",
            "Desarrollo Personal, Ciudadanía y Cívica", "Arte y Cultura", "Educación Física",
            "Educación Religiosa", "Ciencia y Tecnología", "Educación para el Trabajo",
            "Tutoría y Orientación Educativa");

    public static List<String> porGrado(String grado) {
        if (grado != null && grado.contains("Primaria")) return PRIMARIA;
        if (grado != null && grado.contains("Secundaria")) return SECUNDARIA;
        return List.of();
    }
}

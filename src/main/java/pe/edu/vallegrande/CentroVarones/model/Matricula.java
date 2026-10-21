package pe.edu.vallegrande.CentroVarones.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "matriculas")
public class Matricula {

    public static final String EN_REVISION = "EN REVISIÓN";
    public static final String APROBADA = "APROBADA";

    /** Ej. MAT-2026-000125 (se genera en el servidor). */
    @Id
    private String numeroSolicitud;

    private String fecha;

    private String estado;

    @Embedded
    @Valid
    @NotNull
    private Estudiante estudiante;

    @Embedded
    @Valid
    @NotNull
    @AttributeOverride(name = "dni", column = @Column(name = "apoderado_dni"))
    private Apoderado apoderado;

    @Convert(converter = DocumentosConverter.class)
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private Map<String, DocumentoAdjunto> documentos = new LinkedHashMap<>();

    public String getNumeroSolicitud() {
        return numeroSolicitud;
    }

    public void setNumeroSolicitud(String numeroSolicitud) {
        this.numeroSolicitud = numeroSolicitud;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Apoderado getApoderado() {
        return apoderado;
    }

    public void setApoderado(Apoderado apoderado) {
        this.apoderado = apoderado;
    }

    public Map<String, DocumentoAdjunto> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(Map<String, DocumentoAdjunto> documentos) {
        this.documentos = documentos;
    }
}

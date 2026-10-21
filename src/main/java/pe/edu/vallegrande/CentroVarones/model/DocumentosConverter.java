package pe.edu.vallegrande.CentroVarones.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.LinkedHashMap;
import java.util.Map;

/** Guarda los documentos de la matrícula como JSON en una sola columna. */
@Converter
public class DocumentosConverter implements AttributeConverter<Map<String, DocumentoAdjunto>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, DocumentoAdjunto> documentos) {
        try {
            return MAPPER.writeValueAsString(documentos == null ? Map.of() : documentos);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo guardar los documentos", e);
        }
    }

    @Override
    public Map<String, DocumentoAdjunto> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) return new LinkedHashMap<>();
        try {
            return MAPPER.readValue(json, new TypeReference<LinkedHashMap<String, DocumentoAdjunto>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo leer los documentos", e);
        }
    }
}

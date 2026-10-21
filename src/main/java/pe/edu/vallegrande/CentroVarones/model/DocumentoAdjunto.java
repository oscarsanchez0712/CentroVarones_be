package pe.edu.vallegrande.CentroVarones.model;


public class DocumentoAdjunto {
    private String nombreArchivo = "";
    private String tipo = "";
    private boolean adjuntado;

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isAdjuntado() {
        return adjuntado;
    }

    public void setAdjuntado(boolean adjuntado) {
        this.adjuntado = adjuntado;
    }
}

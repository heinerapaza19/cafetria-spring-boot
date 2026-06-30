package cafeteria.reporte.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoReporte {

    DIA("DIA"),
    SEMANA("SEMANA"),
    MES("MES"),
    ANIO("AÑO");

    private final String label;

    TipoReporte(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static TipoReporte from(String value) {
        for (TipoReporte t : values()) {
            if (t.label.equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo inválido: " + value);
    }
}
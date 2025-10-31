package br.edu.ifpr.gep.model.repository;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PortariaPK {
    private String emissor;
    private Integer numero;
    private Integer ano;

    @JsonCreator  // Essencial: Informa ao Jackson como criar a instância a partir do JSON
    public PortariaPK(@JsonProperty("emissor") String emissor,
                      @JsonProperty("numero") Integer numero,
                      @JsonProperty("ano") Integer ano) {
        this.emissor = emissor.toLowerCase();  // Case-insensitive para buscas
        this.numero = numero;
        this.ano = ano;
    }

    public String getEmissor() { return emissor; }
    public Integer getNumero() { return numero; }
    public Integer getAno() { return ano; }

    @Override
    public String toString() {
        return "PortariaPK [emissor=" + emissor + ", numero=" + numero + ", ano=" + ano + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(emissor, numero, ano);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PortariaPK other = (PortariaPK) obj;
        return Objects.equals(ano, other.ano) &&
                Objects.equals(emissor, other.emissor) &&
                Objects.equals(numero, other.numero);
    }
}
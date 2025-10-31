package br.edu.ifpr.gep.model;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.edu.ifpr.gep.model.utils.EmissorTypes;

/**
 * Classe que representa uma Portaria no sistema.
 * Contém informações sobre o emissor, número, data de publicação e membro relacionado.
 */
public class Portaria {
    private EmissorTypes emissor;   // Agora utiliza o Enum EmissorTypes
    private Integer numero;         // Número da Portaria
    private LocalDate publicacao;   // Data da publicação
    private String membro;          // Nome do membro associado

    public Portaria() {}

    @JsonCreator
    public Portaria(@JsonProperty("emissor") Integer emissor,
                    @JsonProperty("numero") Integer numero,
                    @JsonProperty("publicacao") LocalDate publicacao,
                    @JsonProperty("membro") String membro) {
        this.emissor = EmissorTypes.fromValue(emissor); // Converte número em Enum
        this.numero = numero;
        this.publicacao = publicacao;
        this.membro = membro;
    }

    // Getters e Setters
    public EmissorTypes getEmissor() { return emissor; }
    public void setEmissor(EmissorTypes emissor) { this.emissor = emissor; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public LocalDate getPublicacao() { return publicacao; }
    public void setPublicacao(LocalDate publicacao) { this.publicacao = publicacao; }

    public String getMembro() { return membro; }
    public void setMembro(String membro) { this.membro = membro; }

    @Override
    public String toString() {
        return "Portaria[emissor=" + (emissor != null ? emissor.getNome() : null) +
                ", numero=" + numero +
                ", publicacao=" + publicacao +
                ", membro=" + membro + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        if (this == obj) return true;
        Portaria other = (Portaria) obj;
        return Objects.equals(emissor, other.emissor) &&
                Objects.equals(numero, other.numero) &&
                Objects.equals(publicacao, other.publicacao) &&
                Objects.equals(membro, other.membro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emissor, numero, publicacao, membro);
    }
}
package br.edu.ifpr.gep.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import br.edu.ifpr.gep.model.utils.EmissorTypes;

/**
 * Classe que representa uma Portaria no sistema.
 * Persistida como documento no MongoDB.
 */
@Document(collection = "portarias")
public class Portaria {

    @Id
    private String id; // MongoDB usa String para ObjectId

    @Field("emissor")
    private EmissorTypes emissor; // Armazena o enum diretamente (serializa como nome ou value)

    @Field("numero")
    private Integer numero;

    @Field("publicacao")
    private LocalDate publicacao;

    @Field("membro")
    private String membro;

    // Construtor vazio (necessário para o MongoDB)
    public Portaria() {}

    // Construtor com campos
    public Portaria(EmissorTypes emissor, Integer numero, LocalDate publicacao, String membro) {
        this.emissor = emissor;
        this.numero = numero;
        this.publicacao = publicacao;
        this.membro = membro;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
        return "Portaria[id=" + id +
                ", emissor=" + (emissor != null ? emissor.getNome() : null) +
                ", numero=" + numero +
                ", publicacao=" + publicacao +
                ", membro=" + membro + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Portaria other = (Portaria) obj;
        return java.util.Objects.equals(id, other.id) &&
                java.util.Objects.equals(emissor, other.emissor) &&
                java.util.Objects.equals(numero, other.numero) &&
                java.util.Objects.equals(publicacao, other.publicacao) &&
                java.util.Objects.equals(membro, other.membro);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, emissor, numero, publicacao, membro);
    }
}
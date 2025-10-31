package br.edu.ifpr.gep.model.utils;

/**
 * Classe que representa os emissores de portarias do IFPR.
 * Cada emissor possui um índice numérico e um nome amigável para exibição.
 * Suporte a adição dinâmica de novos emissores.
 */
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class EmissorTypes {
    private static final List<EmissorTypes> VALUES = new ArrayList<>();

    static {
        // Inicialização com os emissores pré-definidos
        VALUES.add(new EmissorTypes(1, "Reitoria"));
        VALUES.add(new EmissorTypes(2, "Pró-Reitoria de Ensino"));
        VALUES.add(new EmissorTypes(3, "Pró-Reitoria de Administração"));
        VALUES.add(new EmissorTypes(4, "Pró-Reitoria de Extensão, Pesquisa, Pós-Graduação e Inovação"));
        VALUES.add(new EmissorTypes(5, "Pró-Reitoria de Pessoas"));
        VALUES.add(new EmissorTypes(6, "Pró-Reitoria de Planejamento e Desenvolvimento Institucional"));
        VALUES.add(new EmissorTypes(7, "Campus Arapongas (DG)"));
        VALUES.add(new EmissorTypes(0, "Campus Assis Chateaubriand (DG)"));
        VALUES.add(new EmissorTypes(8, "Campus Astorga (DG)"));
        VALUES.add(new EmissorTypes(9, "Campus Barracão (DG)"));
        VALUES.add(new EmissorTypes(10, "Campus Campo Largo (DG)"));
        VALUES.add(new EmissorTypes(11, "Campus Capanema (DG)"));
        VALUES.add(new EmissorTypes(12, "Campus Cascavel (DG)"));
        VALUES.add(new EmissorTypes(13, "Campus Colombo (DG)"));
        VALUES.add(new EmissorTypes(14, "Campus Coronel Vivida (DG)"));
        VALUES.add(new EmissorTypes(15, "Campus Curitiba (DG)"));
        VALUES.add(new EmissorTypes(16, "Campus Foz do Iguaçu (DG)"));
        VALUES.add(new EmissorTypes(17, "Campus Goioerê (DG)"));
        VALUES.add(new EmissorTypes(18, "Campus Irati (DG)"));
        VALUES.add(new EmissorTypes(19, "Campus Ivaiporã (DG)"));
        VALUES.add(new EmissorTypes(20, "Campus Jacarezinho (DG)"));
        VALUES.add(new EmissorTypes(21, "Campus Jaguariaíva (DG)"));
        VALUES.add(new EmissorTypes(22, "Campus Londrina (DG)"));
        VALUES.add(new EmissorTypes(23, "Campus Palmas (DG)"));
        VALUES.add(new EmissorTypes(24, "Campus Paranaguá (DG)"));
        VALUES.add(new EmissorTypes(25, "Campus Paranavaí (DG)"));
        VALUES.add(new EmissorTypes(26, "Campus Pinhais (DG)"));
        VALUES.add(new EmissorTypes(27, "Campus Pitanga (DG)"));
        VALUES.add(new EmissorTypes(28, "Campus Ponta Grossa (DG)"));
        VALUES.add(new EmissorTypes(29, "Campus Quedas do Iguaçu (DG)"));
        VALUES.add(new EmissorTypes(30, "Campus Telêmaco Borba (DG)"));
        VALUES.add(new EmissorTypes(31, "Campus Toledo (DG)"));
        VALUES.add(new EmissorTypes(32, "Campus Umuarama (DG)"));
        VALUES.add(new EmissorTypes(33, "Campus União da Vitória (DG)"));
    }

    private final int index;
    private final String nome;

    private EmissorTypes(int index, String nome) {
        this.index = index;
        this.nome = nome;
    }

    public static EmissorTypes add(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do emissor não pode ser vazio.");
        }
        int nextIndex = VALUES.stream()
                .mapToInt(e -> e.index)
                .max()
                .orElse(0) + 1;
        EmissorTypes newEmissor = new EmissorTypes(nextIndex, nome.trim());
        VALUES.add(newEmissor);
        return newEmissor;
    }

    private static String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        return normalized.toLowerCase().trim();
    }

    public static EmissorTypes fromName(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String normName = normalize(name);
        for (EmissorTypes type : values()) {
            String normType = normalize(type.nome);
            if (normType.equals(normName)) {
                return type;
            }
        }
        return null;
    }

    public static EmissorTypes valueOf(String upper) {
        return null;
    }

    /** Retorna o índice numérico associado ao emissor */
    public int getIndex() {
        return index;
    }

    /** Retorna o nome completo do emissor */
    public String getNome() {
        return nome;
    }

    /**
     * Localiza um emissor pelo índice numérico.
     * @param index código numérico do emissor
     * @return a instância correspondente
     * @throws IllegalArgumentException se não houver emissor associado
     */
    public static EmissorTypes fromValue(int index) {
        return VALUES.stream()
                .filter(type -> type.index == index)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum emissor para índice " + index));
    }

    public static List<EmissorTypes> values() {
        return new ArrayList<>(VALUES);
    }

    @JsonValue
    public String toJson() {
        return nome;
    }

    @JsonCreator
    public static EmissorTypes create(String value) {
        return fromName(value);
    }

    public int compareTo(EmissorTypes emissor) {
        return 0;
    }
}
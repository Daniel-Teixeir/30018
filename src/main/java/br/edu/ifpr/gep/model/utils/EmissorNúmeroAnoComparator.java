package br.edu.ifpr.gep.model.utils;

import java.util.Comparator;

import br.edu.ifpr.gep.model.Portaria;

public class EmissorNúmeroAnoComparator implements Comparator<Portaria> {
    @Override
    public int compare(Portaria o1, Portaria o2) {
        // 1. comparar o emissor
        int emissorComp = o1.getEmissor().compareTo(o2.getEmissor());
        if (emissorComp != 0) {
            return emissorComp;
        }

        // 2. se emissores iguais, compara números
        int numeroComp = o1.getNumero().compareTo(o2.getNumero());
        if (numeroComp != 0) {
            return numeroComp;
        }

        // 3. se nomes e números são iguais, compara anos
        return Integer.compare(o1.getPublicacao().getYear(), o2.getPublicacao().getYear());
    }
}
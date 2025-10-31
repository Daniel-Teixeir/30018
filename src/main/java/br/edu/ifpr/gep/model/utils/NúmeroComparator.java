package br.edu.ifpr.gep.model.utils;

import java.util.Comparator;

import br.edu.ifpr.gep.model.Portaria;

public class NúmeroComparator implements Comparator<Portaria> {
	@Override
	public int compare(Portaria o1, Portaria o2) {
		int o1Número = o1.getNumero().intValue();
		int o2Número = o2.getNumero().intValue();

		if (o1Número < o2Número) return -1;
		else
			if (o1Número > o2Número) return 1;

		return 0;
	}
}

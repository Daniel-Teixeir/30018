package br.edu.ifpr.gep.model.utils;

import java.util.Comparator;

import br.edu.ifpr.gep.model.Portaria;

public class PublicaçãoComparator implements Comparator<Portaria> {
	@Override
	public int compare(Portaria o1, Portaria o2) {
		return o1.getPublicacao().compareTo(o2.getPublicacao());
	}
}

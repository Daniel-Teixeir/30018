package br.edu.ifpr.gep.model.utils;

import java.util.Comparator;

import br.edu.ifpr.gep.model.Portaria;

public class EmissorComparator implements Comparator<Portaria> {
	@Override
	public int compare(Portaria o1, Portaria o2) {
		return o1.getEmissor().compareTo(o2.getEmissor());
	}
}

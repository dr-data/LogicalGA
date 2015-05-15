package net.jsar.logical;

import java.util.ArrayList;
import java.util.List;

public class Individual {
	private List<LogicalNode> gene = new ArrayList<LogicalNode>();
	private double fitness = -1;

	public void addLogicalNode(LogicalNode node) {
		gene.add(node);
	}
	
	public List<LogicalNode> getGene() {
		return gene;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Fit = "+fitness+"\n");
		for (LogicalNode ln : gene) {
			sb.append(ln.toString()+"\n");
		}
		return sb.toString();
	}

	public void setFitness(double fitness) {
		this.fitness  = fitness;
	}
	
	public double getFitness() {
		return this.fitness;
	}
}

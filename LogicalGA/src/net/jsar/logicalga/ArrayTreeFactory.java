package net.jsar.logicalga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

public class ArrayTreeFactory<T> extends AbstractCandidateFactory<T> {

	private List<TreeFactory> factories = new ArrayList<TreeFactory>();
	private Class clazz = null;
	
	public ArrayTreeFactory(int parameterCount,
			int outputCount,
            int maxDepth,
            Probability functionProbability,
            Probability parameterProbability,
            Class clazz)
	{
		this.clazz  = clazz;
		for (int i = 0; i < outputCount; i++) {
			factories.add(new TreeFactory(parameterCount,maxDepth,functionProbability,parameterProbability));
		}
	}
	@Override
	public T generateRandomCandidate(Random paramRandom) {
		// TODO Auto-generated method stub
		List<Node> out = new ArrayList<Node>();
		for (TreeFactory f: factories) {
			out.add(f.generateRandomCandidate(paramRandom));
		}
		return (T) new MultipleNode(out,'O');
	}
	public List<TreeFactory> getFactories() {
		// TODO Auto-generated method stub
		return factories;
	}

}

package net.jsar.logical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class LogicalTest {
	public static void main(String[] args)
    {
        // The test circuit
        // !((A&&B) || C)
        // A    B   C   Out
        // 1    1   1   0 
        // 1    1   0   0
        // 1    0   1   0
        // 1    0   0   1
        // 0    1   1   0
        // 0    1   0   1
        // 0    0   1   0
        // 0    0   0   1
        // 
        //
        //
        /*     -------     -------
         * A - |     |     |     |
         *     | AND |-----|     |    -------
         * B - | (D) |     |     |    |     |
         *     -------     | OR  |----| NOT |----
         *                 | (E) |    | (F) |
         * C --------------|     |    |     |
         *                 -------    -------
         */

		
        LogicalInput A = new LogicalInput();
        LogicalInput B = new LogicalInput();
        LogicalInput C = new LogicalInput();
        LogicalAND   D = new LogicalAND();
        LogicalOR    E = new LogicalOR();
        LogicalNOT   F = new LogicalNOT();

        A.SetName("A");
        B.SetName("B");
        C.SetName("C");
        D.SetName("D");
        E.SetName("E");
        F.SetName("F");

        D.AddInput(A);
        D.AddInput(B);

        E.AddInput(D);
        E.AddInput(E);

        F.AddInput(E);

        /*
		Input	A
		Input	B
		Input	C
		AND		D	A	B
		OR		E	D	C
		NOT		F	E
		*/
		
        // Truth Table
        boolean[] states = new boolean[]{ true, false };
        for(int idxA = 0; idxA < 2; idxA++)
        {
            for(int idxB = 0; idxB < 2; idxB++)
            {
                for(int idxC = 0; idxC < 2; idxC++)
                {
                    A.SetState(states[idxA]);
                    B.SetState(states[idxB]);
                    C.SetState(states[idxC]);

                    boolean result = F.ComputeOutput();

                    System.out.println("A = "+A.GetState()+", B = "+B.GetState()+", C = "+C.GetState()+", Output = "+String.valueOf(result));
                }
            }
        }
    }
	
	private static Random random = new Random(0);
	private static int populationSize = 50;
	private static int maxIterations = 1000;
	private static double mutationProb = 0.05;
	private static double crossoverProb = 0.2;
	private static int maxGates = 8;
	private static int minGlobalGates = 4;
	//private static int maxInputs = 2;
	private static int maxOutputs = 2;
	private static Class[] logicalNodes = {
		//LogicalInput.class,
		LogicalAND.class,
		LogicalOR.class,
		LogicalNOT.class
	};
	private static int maxGlobalInputs = 2;
	
	public List<LogicalNode> getInputs(Individual ind) {
		List<LogicalNode> inputs = new ArrayList<LogicalNode>();
		for (LogicalNode ln : ind.getGene()) {
			if (ln instanceof LogicalInput) {
				inputs.add(ln);
			}
		}	
		return inputs;
	}
	
	public List<LogicalNode> getOutputs(Individual ind) {
		List<LogicalNode> outputs = new ArrayList<LogicalNode>();
		for (LogicalNode ln : ind.getGene()) {
			if (ln instanceof LogicalOutput) {
				outputs.add(ln);
			}
		}
		return outputs;
	}
	
	public static void main2(String[] args)
    {
		LogicalTest lt = new LogicalTest();
		lt.init(args);
    }
	
	public void init(String[] args)
    {
	
		List<Individual> pool = createPopulation(populationSize);
		
		evaluateAll(pool);
		
		for (int iter=0; iter<maxIterations; iter++) {
			System.out.println("Iteration "+iter);
			
			if (random.nextDouble() < crossoverProb) {
				pool = selection(pool);
				crossOver(pool);
			}
			
			mutateAll(pool);
			
			
			evaluateAll(pool);						
		}
		
				
    }		
	
	private void mutateAll(List<Individual> pool) {
		for (int i=0; i<pool.size(); i++) {
			if (random.nextDouble() < mutationProb) {
				Individual ind = pool.get(i);
				mutate(ind);
			}
		}
	}
	
	private long globalId = 0;
	
	private void mutate(Individual ind) {
		int toChange = getRandomPoint(ind);
		Class c = logicalNodes[random.nextInt(logicalNodes.length)];
		
		LogicalNode orig = ind.getGene().get(toChange);
		LogicalNode dest = null;
		try { dest = (LogicalNode) c.newInstance(); dest.SetName(c.getSimpleName()+"-"+(globalId++));
		} catch (Exception e) {	e.printStackTrace(); return; }
		
		for (int j=0; j<orig.GetInputs().size(); j++) {
			LogicalNode in = getInput(orig, j);
			if (in != null) {
				dest.AddInput(in);
				in.SetOutput(dest);
			}
		}
		
		LogicalNode out = orig.GetOutput();
		dest.SetOutput(out);
		if (out != null) {
			for (int k=0; k<out.GetInputs().size();k++) {
				LogicalNode ln = getInput(out,k);
				if (ln == null) {
					out.GetInputs().set(k, null);
					break;
				}
				else if (ln.equals(orig)) {
					out.GetInputs().set(k, dest);
					break;
				}
			}	
		}
		
		ind.getGene().set(toChange, dest);
	}

	private List<Individual> selection(List<Individual> pool) {
		List<Individual> selected = new ArrayList<Individual>();
		Collections.sort(pool, new Comparator<Individual>() {
			@Override
			public int compare(Individual i1, Individual i2) {
				if (i1.getFitness()<i2.getFitness()) return 1;
				if (i1.getFitness()>i2.getFitness()) return -1;
				return 0;
			}
		});
		for (int i=0; i<pool.size()/2; i++) {
			selected.add(pool.get(i));
		}
		return selected;
	}
	
	private int getRandomPoint(Individual p) {
		int point = -1;
		LogicalNode ln = null;
		while (point < 0 || (ln instanceof LogicalInput) || (ln instanceof LogicalOutput)) {
			point = random.nextInt(p.getGene().size());
			ln = p.getGene().get(point);
		}
		return point;
	}
	
	private LogicalNode getInput(LogicalNode cc, int idx) {
		if (idx < cc.GetInputs().size()) {
			return cc.GetInputs().get(idx);
		}
		return null;
	}

	private void crossOver(List<Individual> pool) {
		List<Individual> childs = new ArrayList<Individual>();
		for (int i=0; i<pool.size(); i++) {
			Individual parent1 = pool.get(random.nextInt(pool.size()));
			Individual parent2 = pool.get(random.nextInt(pool.size()));
			
			int cutPoint1 = getRandomPoint(parent1);
			int cutPoint2 = getRandomPoint(parent2);
			
			LogicalNode cp1 = parent1.getGene().get(cutPoint1);
			LogicalNode cp2 = parent2.getGene().get(cutPoint2);
			
			// Ensure the gates are compatible
			if (cp1.GetNumInputs() != cp2.GetNumInputs()
					|| cp1.GetNumOutputs() != cp2.GetNumOutputs()) {
				continue;
			}
			
			
			Individual child1 = new Individual();
			Individual child2 = new Individual();
			
			int c1 = 0;
			for (int k1=0; k1<cutPoint1; k1++) {
				child1.addLogicalNode(parent1.getGene().get(k1).clone());
				c1++;
			}
			for (int k2=cutPoint2; k2<parent2.getGene().size(); k2++) {
				child1.addLogicalNode(parent2.getGene().get(k2).clone());
			}
			//parent1.getGene().get(cutPoint1-1).SetOutput(parent2.getGene().get(cutPoint2));
			//parent2.getGene().get(cutPoint2).GetInputs().set(0, parent1.getGene().get(cutPoint1-1));
			LogicalNode cc1 = child1.getGene().get(c1);
			LogicalNode cc2 = child1.getGene().get(c1+1);
			cc1.SetOutput(cc2.GetOutput());
			cc2.GetInputs().add(getInput(cc1,0));
			cc2.GetInputs().add(getInput(cc1,1));
			
			c1 = 0;
			for (int k1=0; k1<cutPoint2; k1++) {
				child2.addLogicalNode(parent2.getGene().get(k1).clone());
				c1++;
			}
			for (int k2=cutPoint1; k2<parent1.getGene().size(); k2++) {
				child2.addLogicalNode(parent1.getGene().get(k2).clone());
			}
			//child2.getGene().get(cutPoint1-1).SetOutput(child2.getGene().get(cutPoint2).GetOutput());
			//child2.getGene().get(cutPoint2).GetInputs().set(0, child2.getGene().get(cutPoint1-1).GetInputs().get(0));
			//child2.getGene().get(cutPoint2).GetInputs().set(1, child2.getGene().get(cutPoint1-1).GetInputs().get(1));
			cc1 = child2.getGene().get(c1);
			cc2 = child2.getGene().get(c1+1);
			cc1.SetOutput(cc2.GetOutput());
			cc2.GetInputs().add(getInput(cc1,0));
			cc2.GetInputs().add(getInput(cc1,1));
			
			childs.add(child1);
			childs.add(child2);
		}
		pool.addAll(childs);
	}

	private Individual best = null;
	private void evaluateAll(List<Individual> pool) {
		for (Individual ind : pool) {
			double fitness = fitnessMethod(ind);
			ind.setFitness(fitness);
			
			if (best == null || fitness > best.getFitness()) {
				best = ind;
				System.out.println(ind.toString()+"\n");
			}
		}
	}

	public static byte Inputs[][][] = {
		{
			
		},
		{
			{0},
			{1},
		},
		{
			{0,0},
			{0,1},
			{1,0},
			{1,1},
		},
		{
			{0,0,0},
			{0,0,1},
			{0,1,0},
			{0,1,1},
			{1,0,0},
			{1,0,1},
			{1,1,0},
			{1,1,1}
		}
	};
	
	public static boolean[] byte2boolean(byte[] b) {
		boolean[] out = new boolean[b.length];
		for (int i=0; i<b.length; i++) {
			out[i] = (b[i]!=0);
		}
		return out;
	}
	
	public static byte[] boolean2byte(boolean[] b) {
		byte[] out = new byte[b.length];
		for (int i=0; i<b.length; i++) {
			out[i] = (byte) (b[i] ? 1 : 0);
		}
		return out;
	}
	
	public double fitnessMethod(Individual ind) {
		byte inputs[][] = Inputs[3];
		byte outputs[][] = {
				{0,0},
				{1,0},
				{1,0},
				{0,1},
				{1,0},
				{0,1},
				{0,1},
				{1,1}
		};
		double fitness = 0;
		
		for (int i=0; i<inputs.length;i++) {
			boolean stateIn[] = byte2boolean(inputs[i]);
			boolean stateOut[] = execute(ind,stateIn);
			fitness += compare(boolean2byte(stateOut),outputs[i]);
		}
		
		return fitness;
	}

	private int compare(byte[] stateOut, byte[] outputs) {
		int counter = 0;
		for (int i=0; i<stateOut.length;i++) {
			if (stateOut[i] == outputs[i]) counter++;
		}
		return counter;
	}

	private boolean[] execute(Individual ind, boolean[] state) {
		List<LogicalNode> inputs = getInputs(ind);
		List<LogicalNode> outputs = getOutputs(ind);
		
		int count = 0;
		for (LogicalNode li : inputs) {
			//((LogicalInput)li).SetState(random.nextBoolean());
			((LogicalInput)li).SetState(state[count++]);
		}
        StringBuilder sb = new StringBuilder();
        for (LogicalNode li : inputs) {
        	//boolean ch = random.nextBoolean();
			//((LogicalInput)li).SetState(ch);
			sb.append(li.GetName()+" = "+((LogicalInput)li).GetState()+", ");
		}
        sb.append("\n");
        
        int o = 0;
        boolean out[] = new boolean[outputs.size()];
        for (LogicalNode li : outputs) {
        	//boolean result = endNode.ComputeOutput();
        	//sb.append("\nOutput = "+String.valueOf(result));
        	boolean result = li.ComputeOutput();
        	out[o++] = result;
        	sb.append(li.GetName()+" = "+String.valueOf(result)+", ");
		}
        //System.out.println(sb.toString());
        return out;
	}

	private List<Individual> createPopulation(int populationSize) {
		List<Individual> pool = new ArrayList<Individual>();
		
		for (int i=0; i<populationSize; i++) {
			Individual ind = generateRandomIndividual(3,2);
			
			
			pool.add(ind);
		}
		return pool;
	}

	private Individual generateRandomIndividual(int ninputs, int noutputs) {
		Individual ind = new Individual();
		
		// Input creations
		List<LogicalNode> inputs = new ArrayList<LogicalNode>();
		for (int i=0; i<ninputs; i++) {
			LogicalNode ln = new LogicalInput();
			ln.SetName("LogicalInput-"+i);
			ind.addLogicalNode(ln);
			inputs.add(ln);
		}
		
		// Nodes creation
		int gates = random.nextInt(maxGates)+minGlobalGates; 
		for (int i=0; i<gates; i++) {
			LogicalNode ln = createRandomNode((globalId++)+"");
			ind.addLogicalNode(ln);
		}
		
		// Output creations
		List<LogicalNode> outputs = new ArrayList<LogicalNode>();
		for (int i=0; i<noutputs; i++) {
			LogicalNode ln = new LogicalOutput();
			ln.SetName("LogicalOutput-"+i);
			ind.addLogicalNode(ln);
			outputs.add(ln);
		}
		
		int in_size = 0;
		int out_size = 0;
		List<LogicalNode> nodesEmptyInput = getPossibleInputs(ind.getGene());
		List<LogicalNode> nodesEmptyOutput = getPossibleOutputs(ind.getGene());
		while (true) {
			if (nodesEmptyInput.size() == 0) break;
			//List<LogicalNode> nodesEmptyInput = getPossibleInputs(ind.getGene());
			//List<LogicalNode> nodesEmptyOutput = getPossibleOutputs(ind.getGene());
			
			if (in_size == nodesEmptyInput.size() && out_size == nodesEmptyOutput.size()) break;
			
			in_size = nodesEmptyInput.size();
			out_size = nodesEmptyOutput.size();
			
			if (in_size <= 0 && out_size <= 0) break;
			
			int nextWithInput = random.nextInt(nodesEmptyInput.size());
			LogicalNode lni = nodesEmptyInput.get(nextWithInput);
			
			for (int i=0; i<lni.GetNumInputs(); i++) {
				if (nodesEmptyOutput.size() == 0) break;
				
				int nextWithOutput = random.nextInt(nodesEmptyOutput.size());
				LogicalNode lno = nodesEmptyOutput.get(nextWithOutput);
				
				boolean end = false;
				while (lno == null || lni.equals(lno)) {
					nextWithOutput = random.nextInt(nodesEmptyOutput.size());
					lno = nodesEmptyOutput.get(nextWithOutput);
					
					if (!isConnectable(nodesEmptyOutput,lno)) {
						end = true;
						break;
					}
				}				

				if (end) break;
				lni.AddInput(lno);
				lno.SetOutput(lni);
				nodesEmptyOutput.remove(lno);
			}
			nodesEmptyInput.remove(lni);
			/*
			for (LogicalNode lni: nodesEmptyInput) {
				for (int i=0; i<lni.GetNumInputs(); i++) {
					
					for (LogicalNode lno: nodesEmptyOutput) {
						if (!lni.equals(lno)) {
							lni.AddInput(lno);
							lno.SetOutput(lni);
							nodesEmptyOutput.remove(lno);
						}
					}
				}
				nodesEmptyInput.remove(lni);
			}
			*/
		}
		/*
		// Nodes conection
		List<LogicalNode> nodes = ind.getGene();
		//for (int i=0; i<nodes.size(); i++) {
		List<LogicalNode> processed = new ArrayList<LogicalNode>();
		while (true) {
			if (processed.size() == nodes.size()) break;
			LogicalNode nextNode = null;
			while (nextNode == null || processed.contains(nextNode)) {
				int next = random.nextInt(nodes.size());
				nextNode = nodes.get(next);
			}
			processed.add(nextNode);
			
			LogicalNode node = nextNode;

			int numInputs = node.GetNumInputs();
			
			
			for (int j=0; j<numInputs; j++) {
				LogicalNode selectedInput = node;
				boolean end = false;
				while (selectedInput.equals(node) || selectedInput.GetOutput()!=null || isNodeInCycle(node, selectedInput) || selectedInput instanceof LogicalOutput) {
					int sInput = random.nextInt(nodes.size());
					selectedInput = nodes.get(sInput);
					
					if (!isConnectable(nodes,selectedInput)) {
						end = true;
						break;
					}
				}
				if (end) break;
				node.AddInput(selectedInput);
				selectedInput.SetOutput(node);
				
				
			}
		}
		*/
		
		
		return ind;
	}
	
	private List<LogicalNode> getPossibleOutputs(List<LogicalNode> gene) {
		List<LogicalNode> p = new ArrayList<LogicalNode>();
		for (LogicalNode ln: gene) {
			if (ln instanceof LogicalOutput) continue;			
			if (ln.GetInputs().size() < ln.GetNumInputs()) {
				p.add(ln);
			}
		}
		return p;
	}

	private List<LogicalNode> getPossibleInputs(List<LogicalNode> gene) {
		List<LogicalNode> p = new ArrayList<LogicalNode>();
		for (LogicalNode ln: gene) {
			if (ln instanceof LogicalInput) continue;
			if (ln.GetOutput() == null) {
				p.add(ln);
			}
		}
		return p;
	}

	private boolean isConnectable(List<LogicalNode> nodes, LogicalNode selectedInput) {
		// Check if there is any node to connect		
		for (int k=0; k<nodes.size(); k++) {
			LogicalNode ln = nodes.get(k);
			if (!ln.equals(selectedInput) && ln.GetOutput() == null) return true;
		}
		return false;
	}

	private boolean isNodeInCycle(LogicalNode node, LogicalNode selectedInput) {		
		return false;
	}

	private LogicalNode createRandomNode(String name) {
		LogicalNode ln = null;
		
		int type = random.nextInt(logicalNodes.length);
		Class ctype = logicalNodes[type];
		try {
			ln = (LogicalNode) ctype.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		ln.SetName(ctype.getSimpleName()+"-"+name);
		
		return ln;
	}
}


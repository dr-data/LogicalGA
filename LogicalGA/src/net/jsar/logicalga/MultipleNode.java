//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package net.jsar.logicalga;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.random.Probability;
import org.uncommons.util.reflection.ReflectionUtils;

/**
 * Convenient base class for {@link Node}s that have two sub-trees.
 * @author Daniel Dyer
 */
public class MultipleNode implements Node
{
    protected static final double[] NO_ARGS = new double[0];

    /** The first argument to the binary function. */
    protected final List<Node> nodes;
    /** The second argument to the binary function. */
    
    private final char symbol;


    /**
     * @param left The first argument to the binary function.
     * @param right The second argument to the binary function.
     * @param symbol A single character that indicates the type of function.
     */
    protected MultipleNode(List<Node> nodes, char symbol)
    {
        this.nodes = nodes;
        this.symbol = symbol;
    }


    /**
     * {@inheritDoc}
     */
    public String getLabel()
    {
        return String.valueOf(symbol);
    }


    /**
     * The arity of a binary node is two.
     * @return 2
     */
    public int getArity()
    {
        return this.nodes.size();
    }


    /**
     * The depth of a binary node is the depth of its deepest sub-tree plus one.
     * @return The depth of the tree rooted at this node.
     */
    public int getDepth()
    {
    	int t = 0;
    	for (Node n:nodes) {
    		t += n.getDepth();
    	}
        return 1 + t;
    }


    /**
     * The width of a binary node is the sum of the widths of its two sub-trees.
     * @return The width of the tree rooted at this node.
     */
    public int getWidth()
    {
    	int t = 0;
    	for (Node n:nodes) {
    		t += n.getWidth();
    	}
        return t;
    }


    /**
     * {@inheritDoc}
     */
    public int countNodes()
    {
    	int t = 0;
    	for (Node n:nodes) {
    		t += n.countNodes();
    	}
        return 1 + t;
    }


    /**
     * {@inheritDoc}
     */
    public Node getNode(int index)
    {
        if (index == 0)
        {
            return this;
        }
        
        int leftNodes = 0;
        for (Node n:nodes) {
	        leftNodes += n.countNodes();
	        if (index <= leftNodes)
	        {
	            return n.getNode(index - 1);
	        }
	        else
	        {
	            return n.getNode(index - leftNodes - 1);
	        }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Node getChild(int index)
    {
    	if (index >= nodes.size()) {
    		throw new IndexOutOfBoundsException("Invalid child index: " + index);
    	}
    	else {
    		return nodes.get(index);
    	}
    }


    /**
     * {@inheritDoc}
     */
    public Node replaceNode(int index, Node newNode)
    {
        if (index == 0)
        {
            return newNode;
        }

        int leftNodes = 0;
        for (Node n:nodes) {
	        leftNodes += n.countNodes();
	        if (index <= leftNodes)
	        {
	        	return newInstance(n.replaceNode(index - 1, newNode), n);
	        }
	        else
	        {
	        	return newInstance(n, n.replaceNode(index - leftNodes - 1, newNode));
	        }
        }
        return null;             
    }



    /**
     * {@inheritDoc} 
     */
    public String print()
    {
        StringBuilder buffer = new StringBuilder("(");
        for (Node n:nodes) {
        	buffer.append(n.print());
        	buffer.append(' ');
        }
        buffer.append(')');
        return buffer.toString();
    }


    /**
     * {@inheritDoc}
     */
    public Node mutate(Random rng, Probability mutationProbability, TreeFactory treeFactory)
    {
        if (mutationProbability.nextEvent(rng))
        {
            return treeFactory.generateRandomCandidate(rng);
        }
        else
        {
        	/*
            Node newLeft = left.mutate(rng, mutationProbability, treeFactory);
            Node newRight = right.mutate(rng, mutationProbability, treeFactory);
            if (newLeft != left && newRight != right)
            {
                return newInstance(newLeft, newRight);
            }
            else
            {
                // Tree has not changed.
                return this;
            }
            */
        	return this;
        }
    }


    private Node newInstance(Node newLeft, Node newRight)
    {
        Constructor<? extends MultipleNode> constructor = ReflectionUtils.findKnownConstructor(this.getClass(),
                                                                                             Node.class,
                                                                                             Node.class);
        return ReflectionUtils.invokeUnchecked(constructor, newLeft, newRight);
    }


    @Override
    public String toString()
    {
        return print();
    }


	@Override
	public double evaluate(double[] programParameters) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Node simplify() {
		// TODO Auto-generated method stub
		return null;
	}
}

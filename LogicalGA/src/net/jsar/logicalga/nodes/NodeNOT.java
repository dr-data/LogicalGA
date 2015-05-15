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
package net.jsar.logicalga.nodes;

import net.jsar.logicalga.Constant;
import net.jsar.logicalga.Node;
import net.jsar.logicalga.UnitaryNode;
import net.jsar.logicalga.Util;

/**
 * Simple addition operator {@link Node}.
 * @author Daniel Dyer
 */
public class NodeNOT extends UnitaryNode
{
    /**
     * Creates a node that evaluates to the sum of the values of its two
     * child nodes ({@literal left} and {@literal right}).
     * @param left The first operand.
     * @param right The second operand.
     */
    public NodeNOT(Node left, Node right)
    {
        super(left, null, '!');
    }


    /**
     * Evaluates the two sub-trees and returns the sum of these two values.
     * @param programParameters Program parameters (ignored by the addition operator
     * but may be used in evaluating the sub-trees).
     * @return The sum of the values of both child nodes.
     */
    public double evaluate(double[] programParameters)
    {
    	
        //return left.evaluate(programParameters) + right.evaluate(programParameters);
    	
    	boolean d[] = Util.double2boolean(programParameters);
    	boolean out = !d[0];
    	return Util.boolean2double(out);
    }


    /**
     * {@inheritDoc}
     */
    public Node simplify()
    {
        Node simplifiedLeft = left.simplify();
        //Node simplifiedRight = right.simplify();
        
        if (simplifiedLeft instanceof Constant)
        {
        	double e1 = simplifiedLeft.evaluate(NO_ARGS);
        	//double e2 = simplifiedRight.evaluate(NO_ARGS);
        	
            return new Constant(Util.boolean2double(!Util.double2boolean(e1)));
        }
        else if (simplifiedLeft != left)
        {
            return new NodeNOT(simplifiedLeft,null);
        }
        else
        {
            return this;
        }
    }
}

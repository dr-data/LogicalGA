package net.jsar.logical;

import java.util.List;



public class LogicalNOT extends LogicalNode
{
    protected boolean ComputeOutputInternal()
    {
        List<LogicalNode> inputs = GetInputs();
        if (inputs.size() > 0)
        {   // NOTE:  This is not an optimal design for
            // handling distinct different kinds of circuits.
            //
            // It it demonstrative only!!!!
            return !inputs.get(0).ComputeOutput();
        }
        return false;
    }
    
    public int GetNumInputs() {
    	return 1;
    }
    
    public int GetNumOutputs() {
    	return 1;
    }
}
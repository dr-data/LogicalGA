package net.jsar.logical;

import java.util.List;


public class LogicalAND extends LogicalNode
{
    protected boolean ComputeOutputInternal()
    {
        List<LogicalNode> inputs = GetInputs();
        boolean result = true;
        for (int idx = 0; idx < inputs.size() && result; idx++)
        {
            result = result && inputs.get(idx).ComputeOutput();
        }
        return result;
    }
    
    public int GetNumInputs() {
    	return 2;
    }
    
    public int GetNumOutputs() {
    	return 1;
    }
}

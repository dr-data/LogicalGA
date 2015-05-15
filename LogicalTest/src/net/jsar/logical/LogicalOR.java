package net.jsar.logical;

import java.util.List;


public class LogicalOR extends LogicalNode
{
    protected boolean ComputeOutputInternal()
    {
        List<LogicalNode> inputs = GetInputs();
        boolean result = false;
        for (int idx = 0; idx < inputs.size(); idx++)
        {
            result = inputs.get(idx).ComputeOutput();
            if (result)
                // If we get one true, that is enough.
                break;
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

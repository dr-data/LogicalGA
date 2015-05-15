package net.jsar.logical;


public class LogicalInput extends LogicalNode
{
    private boolean _state = true;

    public void SetState(boolean state)
    {
        _state = state;
    }

    public boolean GetState() { return _state; }

    protected boolean ComputeOutputInternal()
    {
        return _state;
    }
    
    public int GetNumInputs() {
    	return 1;
    }
    
    public int GetNumOutputs() {
    	return 1;
    }

}


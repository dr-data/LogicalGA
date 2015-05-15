package net.jsar.logical;

import java.util.ArrayList;
import java.util.List;

public class LogicalNode
{
    private List<LogicalNode> _inputs = new ArrayList<LogicalNode>();
    private LogicalNode _output = null;
    private String _name = "Not Set";  
    
    public String toString()
    {
    	StringBuilder sb = new StringBuilder(_name+" [");
    	for (LogicalNode in : _inputs) {
    		sb.append(in._name+" ");
    	}
    	sb.append("] [");
    	if (_output != null) {
    		sb.append(_output._name);
    	}
    	sb.append("]");
        return sb.toString();
    }

    public void Reset()
    {
        _inputs.clear();
    }

    public void SetName(String name)
    {
        _name = name;
    }
    
    public String GetName()
    {
        return _name;
    }

    protected List<LogicalNode> GetInputsInternal()
    {
        return _inputs;
    }

    public List<LogicalNode> GetInputs()
    {
        return GetInputsInternal();
    }

    protected LogicalNode GetOutputInternal()
    {
        return _output;
    }
    
    public LogicalNode GetOutput()
    {
        return GetOutputInternal();
    }
    
    public void AddInput(LogicalNode node)
    {
        _inputs.add(node);
    }
    
    public void SetOutput(LogicalNode node)
    {
        _output = node;
    }

    protected boolean ComputeOutputInternal()
    {
        return false;
    }
    
    public int GetNumInputs() {
    	return -1;
    }

    public int GetNumOutputs() {
    	return -1;
    }

    public boolean ComputeOutput()
    {
       // Console.WriteLine("Computing output on {0}.", _name);
        return ComputeOutputInternal();
    }
    
    public LogicalNode clone() {
    	LogicalNode out = new LogicalNode();
    	int inputs = GetNumInputs();
    	if (inputs < 0) inputs = 0;
    	List<LogicalNode> ln = new ArrayList<LogicalNode>(inputs);
    	ln.addAll(_inputs);
    	out.SetInputs(ln);
    	out.SetOutput(_output);
    	out.SetName(_name);
    	return out;
    }

	public void SetInputs(List<LogicalNode> inputs) {
		_inputs = inputs;
	}
}

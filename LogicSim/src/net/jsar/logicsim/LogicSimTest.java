package net.jsar.logicsim;

import logicsim.AND;
import logicsim.BININ;
import logicsim.Gate;
import logicsim.GateList;
import logicsim.MODIN;
import logicsim.MODOUT;
import logicsim.NAND;
import logicsim.NOT;
import logicsim.SWITCH;
import logicsim.Wire;


public class LogicSimTest {

	private Simulate sim = null;
	
	public LogicSimTest() {
		test2();
	}
	public void test2() {
		GateList gates = new GateList();
		
		Gate in0 = new SWITCH();
		Gate in1 = new SWITCH();
		Wire w0 = new Wire(in0, 0);
		Wire w1 = new Wire(in1, 0);
		
		Gate not1 = new NOT(w0);
	    Gate not2 = new NOT(w1);
	    Gate n1 = new NAND();
	    Gate n2 = new NAND();

	    n1.setInput(0, new Wire(not1,0));
	    n2.setInput(0, new Wire(not2,0));
	    n1.setInput(1, new Wire(n2, 0));
	    n2.setInput(1, new Wire(n1, 0));

	    gates.addGate(not1);
	    gates.addGate(not2);
	    gates.addGate(n2);
	    gates.addGate(n1);
	    
	    
	    n1.setOutput(false);
		n2.setOutput(false);
		gates.simulate();		
		System.out.println("Result: "+n1.getOutput(0));
		n1.setOutput(true);
		n2.setOutput(false);
		gates.simulate();		
		n1.setOutput(false);
		n2.setOutput(false);
		System.out.println("Result: "+n1.getOutput(0));
	}
	public void test1() {
		GateList gateList = new GateList();
		
		Gate in1 = new SWITCH();
		Gate in2 = new SWITCH();
		//Gate out1 = new BINOUT();
		
		Wire w1 = new Wire(in1, 0);
		Wire w2 = new Wire(in2, 0);
		Gate and1 = new AND(w1,w2);
		
		//Wire w3 = new Wire(and1, 0);
		//out1.setInput(0,w3);
		
		gateList.addGate(in1);
		gateList.addGate(in2);
		gateList.addGate(and1);
		//gateList.addGate(out1);
		
		
		
		in1.setOutput(false);
		in2.setOutput(false);
		gateList.simulate();
		//gateList.simulate();
		//gateList.simulate();
		
		System.out.println("Result: "+and1.getOutput(0));
		
		/*
		if (!(sim!=null && sim.running)) {
			sim=new Simulate(gateList);
		}
		
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (sim!=null) sim.stop();
		*/
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogicSimTest lst = new LogicSimTest();
	}

}

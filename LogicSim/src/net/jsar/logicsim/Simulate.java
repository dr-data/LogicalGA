package net.jsar.logicsim;

import logicsim.Gate;
import logicsim.GateList;


public class Simulate implements Runnable {
  Thread thread;
  boolean running=true;
  //LSPanel lspanel;
  GateList gateList;
  boolean doReset=false;

  public Simulate ( GateList gateList ) {
    this.gateList=gateList;

    thread = new Thread(this);
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.start();

    //lspanel.simulationRunning=true;
  }

  public void stop() {
    running=false;
    //lspanel.simulationRunning=false; 
  }

  public void run()
  {
    while (running) {
    	
      gateList.simulate();
      
      /* Reset */
      if (doReset) {
          for (int i=0; i<gateList.size(); i++) {
              Gate g = (Gate)gateList.get(i);
              g.reset();
          }
          doReset=false;
      }
      
      //lspanel.repaint();
      //lspanel.draw(lspanel.getGraphics());
      
      // ** DM 26.12.2008 ** //
      // Den sleep ans Ende verschoben, damit keine
      // race-condition entsteht.
      try
      {
       thread.sleep(100);
      }
      catch(Exception e){}
      
    }
  }

  public void reset() {
      doReset=true;
 }
  
}


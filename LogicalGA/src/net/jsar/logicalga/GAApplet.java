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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.maths.random.XORShiftRNG;
import org.uncommons.swing.SwingBackgroundTask;
import org.uncommons.watchmaker.examples.AbstractExampleApplet;
import org.uncommons.watchmaker.examples.EvolutionLogger;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.AbortControl;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

/**
 * This program is inspired by Roger Alsing's evolution of the Mona Lisa
 * (http://rogeralsing.com/2008/12/07/genetic-programming-evolution-of-mona-lisa/).
 * It attempts to find the combination of 50 translucent polygons that most closely
 * resembles Leonardo da Vinci's Mona Lisa.
 * @author Daniel Dyer
 */
public class GAApplet extends AbstractExampleApplet
{
    private static final String IMAGE_PATH = "org/uncommons/watchmaker/examples/monalisa/monalisa.jpg";
    public static final Map<double[], double[]> TEST_DATA = new HashMap<double[], double[]>();
    static
    {
    	/*
    	{0,0},
		{1,0},
		{1,0},
		{0,1},
		{1,0},
		{0,1},
		{0,1},
		{1,1}
		*/
        TEST_DATA.put(new double[]{0,0,0}, new double[]{0,0});
        TEST_DATA.put(new double[]{0,0,1}, new double[]{1,0});
        TEST_DATA.put(new double[]{0,1,0}, new double[]{1,0});
        TEST_DATA.put(new double[]{0,1,1}, new double[]{0,1});
        TEST_DATA.put(new double[]{1,0,0}, new double[]{1,0});
        TEST_DATA.put(new double[]{1,0,1}, new double[]{0,1});
        TEST_DATA.put(new double[]{1,1,0}, new double[]{0,1});
        TEST_DATA.put(new double[]{1,1,1}, new double[]{1,1});
    }
    private ProbabilitiesPanel probabilitiesPanel;
    private EvolutionMonitor<Node> monitor;
    private JButton startButton;
    private AbortControl abort;
    private JSpinner populationSpinner;
    private JSpinner elitismSpinner;
    private ProbabilityParameterControl selectionPressureControl;
    private BufferedImage targetImage;


    @Override
    public void init()
    {
        try
        {
            //URL imageURL = GAApplet.class.getClassLoader().getResource(IMAGE_PATH);
            //targetImage = ImageIO.read(imageURL);
            super.init();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex, "Failed to Load Image", JOptionPane.ERROR_MESSAGE);

        }
    }


    /**
     * Initialise and layout the GUI.
     * @param container The Swing component that will contain the GUI controls.
     */
    @Override
    protected void prepareGUI(Container container)
    {
        probabilitiesPanel = new ProbabilitiesPanel();
        probabilitiesPanel.setBorder(BorderFactory.createTitledBorder("Evolution Probabilities"));        
        JPanel controls = new JPanel(new BorderLayout());
        controls.add(createParametersPanel(), BorderLayout.NORTH);
        controls.add(probabilitiesPanel, BorderLayout.SOUTH);
        container.add(controls, BorderLayout.NORTH);

        Renderer<Node, JComponent> renderer = new SwingGPTreeRenderer();
        monitor = new EvolutionMonitor<Node>(renderer, false);
        container.add(monitor.getGUIComponent(), BorderLayout.CENTER);
    }


    private JComponent createParametersPanel()
    {
        Box parameters = Box.createHorizontalBox();
        parameters.add(Box.createHorizontalStrut(10));
        final JLabel populationLabel = new JLabel("Population Size: ");
        parameters.add(populationLabel);
        parameters.add(Box.createHorizontalStrut(10));
        populationSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 1000, 1));
        populationSpinner.setMaximumSize(populationSpinner.getMinimumSize());
        parameters.add(populationSpinner);
        parameters.add(Box.createHorizontalStrut(10));
        final JLabel elitismLabel = new JLabel("Elitism: ");
        parameters.add(elitismLabel);
        parameters.add(Box.createHorizontalStrut(10));
        elitismSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 1000, 1));
        elitismSpinner.setMaximumSize(elitismSpinner.getMinimumSize());
        parameters.add(elitismSpinner);
        parameters.add(Box.createHorizontalStrut(10));

        parameters.add(new JLabel("Selection Pressure: "));
        parameters.add(Box.createHorizontalStrut(10));
        selectionPressureControl = new ProbabilityParameterControl(Probability.EVENS,
                                                                   Probability.ONE,
                                                                   2,
                                                                   new Probability(0.7));
        parameters.add(selectionPressureControl.getControl());
        parameters.add(Box.createHorizontalStrut(10));

        startButton = new JButton("Start");
        abort = new AbortControl();        
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                abort.getControl().setEnabled(true);
                populationLabel.setEnabled(false);
                populationSpinner.setEnabled(false);
                elitismLabel.setEnabled(false);
                elitismSpinner.setEnabled(false);
                startButton.setEnabled(false);
                new EvolutionTask((Integer) populationSpinner.getValue(),
                                  (Integer) elitismSpinner.getValue(),
                                  abort.getTerminationCondition(),
                                  new Stagnation(1000, false)).execute();
            }
        });
        abort.getControl().setEnabled(false);
        parameters.add(startButton);
        parameters.add(abort.getControl());
        parameters.add(Box.createHorizontalStrut(10));

        parameters.setBorder(BorderFactory.createTitledBorder("Parameters"));
        return parameters;
    }



    /**
     * The task that acutally performs the evolution.
     */
    private class EvolutionTask extends SwingBackgroundTask<Node>
    {
        private final int populationSize;
        private final int eliteCount;
        private final TerminationCondition[] terminationConditions;


        EvolutionTask(int populationSize, int eliteCount, TerminationCondition... terminationConditions)
        {
            this.populationSize = populationSize;
            this.eliteCount = eliteCount;
            this.terminationConditions = terminationConditions;
        }


        @Override
        protected Node performTask() throws Exception
        {
        	/*
            TreeFactory factory = new TreeFactory(3, // Number of parameters passed into each program.
                    4, // Maximum depth of generated trees.
                    Probability.EVENS, // Probability that a node is a function node.
                    new Probability(0.6d)); // Probability that other nodes are params rather than constants.
            */
    		
    		ArrayTreeFactory<Node> factory = new ArrayTreeFactory<Node>(3, // Number of parameters passed into each program.
    				2, // Number of outputs
                    4, // Maximum depth of generated trees.
                    Probability.EVENS, // Probability that a node is a function node.
                    new Probability(0.6d),
                    MultipleNode.class); // Probability that other nodes are params rather than constants.
                    
			List<EvolutionaryOperator<Node>> operators = new ArrayList<EvolutionaryOperator<Node>>(3);
			operators.add(new TreeMutation(factory, new Probability(0.4d)));
			operators.add(new TreeCrossover());
			operators.add(new Simplification());
			TreeEvaluator evaluator = new TreeEvaluator(TEST_DATA);
			EvolutionEngine<Node> engine = new GenerationalEvolutionEngine<Node>(factory,
			                                                   new EvolutionPipeline<Node>(operators),
			                                                   evaluator,
			                                                   new RouletteWheelSelection(),
			                                                   new MersenneTwisterRNG());
			engine.addEvolutionObserver(new EvolutionLogger<Node>());
			engine.addEvolutionObserver(monitor);
			TerminationCondition[] termConds = new TerminationCondition[] {
					new TargetFitness(0d, evaluator.isNatural()),
					abort.getTerminationCondition()
			};		
			return engine.evolve(1000, 5, termConds);
        }


        @Override
        protected void postProcessing(Node result)
        {
            abort.reset();
            abort.getControl().setEnabled(false);
            populationSpinner.setEnabled(true);
            elitismSpinner.setEnabled(true);
            startButton.setEnabled(true);
        }


        @Override
        protected void onError(Throwable throwable)
        {
            super.onError(throwable);
            postProcessing(null);
        }
    }
    
    public static void main(String[] args)
    {
     
        GAApplet gui = new GAApplet();
      
        gui.displayInFrame("Watchmaker Framework - GA Example");
    }
}

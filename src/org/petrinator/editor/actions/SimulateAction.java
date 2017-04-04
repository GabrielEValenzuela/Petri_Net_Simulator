package org.petrinator.editor.actions;

/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * Copyright (C) 2016-2017 Joaqu�n Rodr�guez Felici <joaquinfelici at gmail.com>
 * Copyright (C) 2016-2017 Leandro Asson <leoasson at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.petrinator.editor.Root;
import org.petrinator.editor.filechooser.FileChooserDialog;
import org.petrinator.editor.filechooser.FileType;
import org.petrinator.editor.filechooser.FileTypeException;
import org.petrinator.monitor.ConcreteObserver;
import org.petrinator.petrinet.*;
import org.petrinator.util.GraphicsTools;
import org.petrinator.editor.commands.FireTransitionCommand;
import java.util.Arrays;
import org.unc.lac.javapetriconcurrencymonitor.errors.IllegalTransitionFiringError;
import org.unc.lac.javapetriconcurrencymonitor.exceptions.PetriNetException;
import org.unc.lac.javapetriconcurrencymonitor.monitor.PetriMonitor;
import org.unc.lac.javapetriconcurrencymonitor.monitor.policies.FirstInLinePolicy;
import org.unc.lac.javapetriconcurrencymonitor.monitor.policies.TransitionsPolicy;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.RootPetriNet;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.components.MTransition;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.factory.PetriNetFactory;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.factory.PetriNetFactory.petriNetType;
import rx.Observer;
import rx.Subscription;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joaquin Felici <joaquinfelici at gmail.com>
 * @brief Does N firings, one every Y seconds.
 * @detail Creates a monitor, subscribes to all transitions and creates
 * a thread for each one of them. Every thread will try to persistently fire
 * it's associated transition, until N firings have been executed.
 * Once's it's finished, a new thread is created, in charge of graphically
 * executing all these firings, one every Y seconds.
 */
public class SimulateAction extends AbstractAction
{
	private Root root;
    private List<FileType> fileTypes;
    ActionEvent e;

    public SimulateAction(Root root, List<FileType> fileTypes) {
        this.root = root;
        this.fileTypes = fileTypes;
        String name = "Simulation";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/play16.gif"));
        putValue(SHORT_DESCRIPTION, name);
    }

    public void actionPerformed(ActionEvent e) 
    {
        this.e = e;

        /*
         * Create tmp.pnml file
         */
        FileChooserDialog chooser = new FileChooserDialog();

        if (root.getCurrentFile() != null) 
        {
            chooser.setSelectedFile(root.getCurrentFile());
        }

        for (FileType fileType : fileTypes) 
        {
            chooser.addChoosableFileFilter(fileType);
        }
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(root.getCurrentDirectory());
        chooser.setDialogTitle("Save as...");

        File file = new File("temp/" + "temp" + "." + "pnml");
        FileType chosenFileType = (FileType) chooser.getFileFilter();
        try 
        {
        	chosenFileType.save(root.getDocument(), file);
        } 
        catch (FileTypeException e1) 
        {
        	e1.printStackTrace();
        }

        /*
         * Ask user to insert times
         */
        int numberOfTransitions = 1, timeBetweenTransitions = 1000;

        JTextField number = new JTextField(8);
        JTextField time = new JTextField(8);
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new MigLayout());
        myPanel.add(new JLabel("Number of transition:  "));
        myPanel.add(new JLabel ("    "));
        myPanel.add(number,"wrap");
        myPanel.add(new JLabel("Time between transition:  "));
        myPanel.add(new JLabel ("    "));
        myPanel.add(time,"wrap");

        time.setText("3");
        number.setText("10");

        int result = JOptionPane.showConfirmDialog(null, myPanel, "Simulation time", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION)
        {
            try
            {
                numberOfTransitions = Integer.valueOf(number.getText());
                timeBetweenTransitions = Integer.valueOf(time.getText()) * 1000;
            }
            catch(NumberFormatException e1)
            {
                 JOptionPane.showMessageDialog(null, "Invalid number.");
                 return; // Don't execute further code
            }
        }
        else
        {
            return; // Don't execute further code
        }

        /*
         * Run threads
         */
        runInMonitor(numberOfTransitions, timeBetweenTransitions);
    }

    /*
     * @brief Creates monitor, threads for transitions, observer, and runs all threads.
     * @detail After getting all the firings the user set, it creates a thread that
     * will "fire" the transitions within our editor every x millis.
     */
    public void runInMonitor(int numberOfTransitions, int timeBetweenTransitions)
    {
        /*
         * Create monitor, petri net, and all related variables.
         */
    	 PetriNetFactory factory = new PetriNetFactory("temp/temp.pnml");
		 RootPetriNet petri = factory.makePetriNet(petriNetType.PLACE_TRANSITION);
		 TransitionsPolicy policy = new FirstInLinePolicy();
		 PetriMonitor monitor = new PetriMonitor(petri, policy);
		 
		 petri.initializePetriNet();

		 /*
		  * Subscribe to all transitions
		  */
		 Observer<String> observer = new ConcreteObserver(root);
		 for(int i = 0; i < petri.getTransitions().length; i++)
		 {
			 MTransition t = petri.getTransitions()[i];
			 Subscription subscription = monitor.subscribeToTransition(t, observer); 
		 }
		 
		 /*
		  * Create one thread per transition, start them all to try and fire them.
		  */
		 List<Thread> threads = new ArrayList<Thread>();
		 for(int i = 0; i < petri.getTransitions().length; i++)
		 {
			Thread t = createThread(monitor, petri.getTransitions()[i].getId());
			threads.add(t);
			t.start();
		 }

        // JOptionPane.showMessageDialog(null, "I am happy.");

		 /*
		  * Wait for the number of events to occur
		  */
		 while(true)
         {
             if(((ConcreteObserver) observer).getEvents().size() >= numberOfTransitions)  // If there have been N events already
                 break;
             else
             {
                 try
                 {
                     Thread.currentThread().sleep(100);
                 } catch (InterruptedException e1) {
                     e1.printStackTrace();
                 }
                 //System.out.println(""); // Need at least one instruction in while, otherwise it will explode
                 if(checkAllAre(petri.getEnabledTransitions(),false))   // We need to check if the net is blocked and no more transitions can be fored
                 {
                     JOptionPane.showMessageDialog(null, "The net is blocked, " + ((ConcreteObserver) observer).getEvents().size() + " transitions were fired.");
                     break;
                 }
             }
         }

         /*
          * Stop all threads from firing
          */
         for(Thread t: threads)
         {
             t.stop();
         }

        /*
         * Run a single thread to fire the transitions graphically
         */
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                fireGraphically(((ConcreteObserver) observer).getEvents(), timeBetweenTransitions, numberOfTransitions);
            }
        });
        t.start();
    }

    /*
     * @brief Creates a thread that tries to fire one given transition
     * @param m the monitor that holds our petri net
     * @param id the id of the transition this thread will try to fire
     * @return t the created tread
     */
    Thread createThread(PetriMonitor m, String id)
    {
    	Thread t = new Thread(new Runnable() {
			  @Override
                    public void run()
                    {
                        while(true)
                        {
                            try
                            {
                                Thread.sleep(200);
                                m.fireTransition(id);
                            } catch (IllegalTransitionFiringError | IllegalArgumentException | PetriNetException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                    }
			  }
			});
			return t;
    }

    /*
     * @brief Takes the list of events, and performs one per one, every x millis (set by user)
     * @param list list of events
     * @param timeBetweenTransitions milliseconds to wait between events performed
     * @return
     */
    void fireGraphically(List<String> list, int timeBetweenTransitions, int numberOfTransitions)
    {
        for(String event : list)
        {
            /*
             * We simulate to press the EditTokens/EditTransition button so the enabled transitions
             * will be shown in green.
             */
            new TokenSelectToolAction(root).actionPerformed(e);

            List<String> transitionInfo = Arrays.asList(event.split(","));
            String transitionId = transitionInfo.get(2);
            transitionId = transitionId.replace("\"", "");
            transitionId = transitionId.replace("id:", "");
            transitionId = transitionId.replace("}", "");

            System.out.println(transitionId + " was fired!");

            Transition transition = root.getDocument().petriNet.getRootSubnet().getTransition(transitionId);
            Marking marking = root.getDocument().petriNet.getInitialMarking();

            FireTransitionCommand fire = new FireTransitionCommand(transition, marking);
            fire.execute();
            root.refreshAll();

            /*
             * Maybe, if some many threads executed many transitions concurrently,
             * there are more events than "numberOfTransitions" specified.
             * Let's make sure we won't fire more than "numberOfTransitions"
             */
            if(list.indexOf(event) >= numberOfTransitions - 1)
                return;

            try
            {
                Thread.currentThread().sleep(timeBetweenTransitions);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * @brief Checks if all booleans in array are either true or false
    * @param array that contains all booleans
    * @param value the value that we want all the booleans to have
    * @returns true if all match, false otherwise
    */
    static boolean checkAllAre(boolean[] array, boolean value)
    {
        for(int i = 0; i < array.length; i++)
        {
            if(array[i] != value)
                return false;
        }
        return true;
    }
}
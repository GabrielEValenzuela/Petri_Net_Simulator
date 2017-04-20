package org.petrinator.editor.actions.algorithms;

import org.petrinator.editor.Root;
import org.petrinator.editor.filechooser.*;
import org.petrinator.util.GraphicsTools;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.ResultsHTMLPane;
import java.util.Date;

import pipe.views.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import pipe.calculations.StateSpaceGenerator;
import pipe.calculations.myTree;
import pipe.exceptions.MarkingNotIntegerException;
import pipe.exceptions.TimelessTrapException;
import pipe.exceptions.TreeTooBigException;
import pipe.extensions.jpowergraph.PIPEInitialState;
import pipe.extensions.jpowergraph.PIPEInitialTangibleState;
import pipe.extensions.jpowergraph.PIPEInitialVanishingState;
import pipe.extensions.jpowergraph.PIPELoopWithTextEdge;
import pipe.extensions.jpowergraph.PIPEState;
import pipe.extensions.jpowergraph.PIPETangibleState;
import pipe.extensions.jpowergraph.PIPEVanishingState;
import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.GraphFrame;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.io.ImmediateAbortException;
import pipe.io.IncorrectFileFormatException;
import pipe.io.ReachabilityGraphFileHeader;
import pipe.io.StateRecord;
import pipe.io.TransitionRecord;
import pipe.modules.interfaces.IModule;
import pipe.utilities.Expander;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.PetriNetView;

/**
 * Created module to produce the reachability graph representation of a Petri
 * net. If the petri net is bounded, then the reachability and coverability
 * graphs are the same. If it's not bounded, it's reachability graph is not
 * finit, so we generate the coverability one instead.
 *
 * @author Matthew Worthington
 * @author Edwin Chung
 * @author Will Master
 * @author Joaquin Rodriguez Felici
 */
public class ReachabilityAction extends AbstractAction
{
    Root root;
    private ResultsHTMLPane results;

    public ReachabilityAction(Root root)
    {
        this.root = root;
        String name = "Reachabilty/Coverability graph";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        //putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/play16.png"));
    }

    public void actionPerformed(ActionEvent e) {
        /*
         * Create tmp.pnml file
         */
        FileChooserDialog chooser = new FileChooserDialog();

        if (root.getCurrentFile() != null) {
            chooser.setSelectedFile(root.getCurrentFile());
        }

        chooser.addChoosableFileFilter(new PipePnmlFileType());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(root.getCurrentDirectory());
        chooser.setDialogTitle("Save as...");

        File file = new File("tmp/" + "tmp" + "." + "pnml");
        FileType chosenFileType = (FileType) chooser.getFileFilter();
        try {
            chosenFileType.save(root.getDocument(), file);
        } catch (FileTypeException e1) {
            e1.printStackTrace();
        }

        /*
         * Show initial pane
         */
        EscapableDialog guiDialog = new EscapableDialog(root.getParentFrame(), "Reachabilty graph", true);
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        //sourceFilePanel = new PetriNetChooserPanel("Source net", null);
        results = new ResultsHTMLPane("");
        contentPane.add(results);
        contentPane.add(new ButtonBar("Generate graph", generateButtonClick, guiDialog.getRootPane()));
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(root.getParentFrame());
        guiDialog.setVisible(true);
    }

    private final ActionListener generateButtonClick = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
            /*
             * Read tmp file
             */
            PetriNetView sourcePetriNetView = new PetriNetView("tmp/tmp.pnml");
            String s = "";

            long start = new Date().getTime();
            long gfinished;
            long allfinished;
            double graphtime;
            double constructiontime;
            double totaltime;

            /*
             * Let's try to create the reachability graph
             */

            File reachabilityGraph = new File("results.rg");
            if(!root.getDocument().getPetriNet().getRootSubnet().hasPlaces() || !root.getDocument().getPetriNet().getRootSubnet().hasTransitions())
            {
                s += "Invalid net!";
            }
            else
            {
                try
                {
                    String graph = "Reachability graph";
                    boolean generateCoverability = false;

                    try
                    {
                        StateSpaceGenerator.generate(sourcePetriNetView, reachabilityGraph);
                    }
                    catch(OutOfMemoryError e) // If this error is captured, then the net seems to be bounded
                    {
                        generateCoverability = true;
                    }

                    /*
                     * If we found the net to be unbounded, then we need to create the coverability graph
                     */
                    if(generateCoverability)
                    {
                        LinkedList<MarkingView>[] markings = sourcePetriNetView.getCurrentMarkingVector();
                        int[] currentMarking = new int[markings.length];
                        for(int i = 0; i < markings.length; i++)
                        {
                            currentMarking[i] = markings[i].getFirst().getCurrentMarking();
                        }
                        myTree tree = new myTree(sourcePetriNetView, currentMarking, reachabilityGraph);
                        graph = "Coverability graph";
                    }

                    /*
                     * Let's show the results
                     */
                    gfinished = new Date().getTime();
                    System.gc();
                    //generateGraph(reachabilityGraph, sourcePetriNetView, generateCoverability);
                    allfinished = new Date().getTime();
                    graphtime = (gfinished - start) / 1000.0;
                    constructiontime = (allfinished - gfinished) / 1000.0;
                    totaltime = (allfinished - start) / 1000.0;
                    DecimalFormat f = new DecimalFormat();
                    f.setMaximumFractionDigits(5);
                    s += "<br>Generating " + graph + " took " +
                            f.format(graphtime) + "s";
                    s += "<br>Constructing it took " +
                            f.format(constructiontime) + "s";
                    s += "<br>Total time was " + f.format(totaltime) + "s";
                    results.setEnabled(true);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }


            }





        }
    };
}
package org.petrinator.editor.actions;

import org.petrinator.auxiliar.GraphPanel;
import org.petrinator.editor.Root;
import org.petrinator.petrinet.Element;
import org.petrinator.petrinet.Place;
import org.petrinator.petrinet.TransitionNode;
import org.petrinator.util.GraphicsTools;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;

public class GraphPlaceAction extends AbstractAction
{
    private Root root;

    public GraphPlaceAction(Root root)
    {
        this.root = root;
        String name = "Generate graph";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/GenerateGraph16.png"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("Graph"));
        setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Place clickedPlace = (Place) root.getClickedElement();

        if(clickedPlace.getValues().isEmpty() || SimulateAction.instants.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Simulation must be run before graph can be displayed.");
            return;
        }

        java.util.List<java.util.List<Double>> vectors = new ArrayList<java.util.List<Double>>();
        vectors.add(SimulateAction.instants);
        vectors.add(clickedPlace.getValues());

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("");
        labels.add(clickedPlace.getLabel());

        GraphPanel mainPanel = new GraphPanel(root, vectors, labels);
    }
}

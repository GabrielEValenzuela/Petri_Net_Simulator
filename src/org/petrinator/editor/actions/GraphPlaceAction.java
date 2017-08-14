package org.petrinator.editor.actions;

import org.petrinator.auxiliar.GraphPanel;
import org.petrinator.editor.Root;
import org.petrinator.petrinet.Place;
import org.petrinator.petrinet.TransitionNode;
import org.petrinator.util.GraphicsTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GraphPlaceAction extends AbstractAction {


    private Root root;

    public GraphPlaceAction(Root root) {
        this.root = root;
        String name = "Generate graph";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/GenerateGraph16.png"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("GRAPH"));
        setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {

        Place clickedPlace = (Place) root.getClickedElement();
        clickedPlace.printValues();

        if(clickedPlace.getValues().isEmpty() || SimulateAction.instants.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Simulation must be run before.");
            return;
        }

        GraphPanel mainPanel = new GraphPanel(clickedPlace.getValues(), SimulateAction.instants);
        mainPanel.setPreferredSize(new Dimension(600, 400));
        JFrame frame = new JFrame("History for place " + clickedPlace.getLabel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

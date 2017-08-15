package org.petrinator.editor.actions;

import net.miginfocom.swing.MigLayout;
import org.petrinator.auxiliar.GraphPanel;
import org.petrinator.editor.Root;
import org.petrinator.petrinet.Place;
import org.petrinator.util.GraphicsTools;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.TreeSet;

public class GraphMultiplePlacesAction extends AbstractAction
{
    private Root root;
    private ArrayList<String> resultPlaces = new ArrayList<String>();

    public GraphMultiplePlacesAction(Root root) {
        this.root = root;
        String name = "Graph places history";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/GenerateGraph16.png"));
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(true);
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(SimulateAction.instants.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Simulation must be run before graph can be displayed.");
            return;
        }

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new MigLayout());
        JComboBox<String> boxP1;
        JComboBox<String> boxP2;
        JComboBox<String> boxP3;
        JComboBox<String> boxP4;
        JComboBox<String> boxP5;

        String[] choice = getChoices(root);

        boxP1 = new JComboBox<String>(choice);
        boxP2 = new JComboBox<String>(choice);
        boxP3 = new JComboBox<String>(choice);
        boxP4 = new JComboBox<String>(choice);
        boxP5 = new JComboBox<String>(choice);

        myPanel.add(new JLabel("Select places to draw: "),"span, grow");
        myPanel.add(new JLabel(" "),"span, grow");
        myPanel.add(new JLabel("Place 1: "));
        myPanel.add(boxP1,"span, grow");
        myPanel.add(new JLabel("Place 2: "));
        myPanel.add(boxP2,"span, grow");
        myPanel.add(new JLabel("Place 3: "));
        myPanel.add(boxP3,"span, grow");
        myPanel.add(new JLabel("Place 4: "));
        myPanel.add(boxP4,"span, grow");
        myPanel.add(new JLabel("Place 5: "));
        myPanel.add(boxP5,"span, grow");

        int result = JOptionPane.showConfirmDialog(root.getParentFrame(), myPanel, "Graph multiple places", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, GraphicsTools.getIcon("pneditor/Behavior32.png"));
        if (result == JOptionPane.OK_OPTION)
        {
            String[] resultComboBox = {boxP1.getSelectedItem().toString(), boxP2.getSelectedItem().toString(), boxP3.getSelectedItem().toString(),
                    boxP4.getSelectedItem().toString(), boxP5.getSelectedItem().toString()};
            resultPlaces.clear();
            //resultPlaces.add("");

            for(int i=0; i < 5; i++)
            {
                if(!resultComboBox[i].equals("none"))
                {
                    resultPlaces.add(resultComboBox[i]);
                }
            }
        }
        else if(result == JOptionPane.CANCEL_OPTION)
        {
            return;
        }

        if(resultPlaces.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "No places were selected.");
            return;
        }

        java.util.List<java.util.List<Double>> vectors = new ArrayList<java.util.List<Double>>();
        vectors.add(SimulateAction.instants);
        for(String label : resultPlaces)
        {
            vectors.add((root.getDocument().petriNet.getCurrentSubnet().getPlace(label)).getValues());
        }

        resultPlaces.add(0, "");

        GraphPanel mainPanel = new GraphPanel(root, vectors, resultPlaces);
    }

    private String [] getChoices(Root root)
    {
        ArrayList<Place> places = new ArrayList(new TreeSet(root.getDocument().petriNet.getRootSubnet().getPlaces()));
        String[] labels = new String[places.size() + 1];
        labels[0] = "none";

        int count = 1;
        for(Place place : places)
        {
            labels[count] = place.getLabel();
            count++;
        }
        return labels;
    }

}
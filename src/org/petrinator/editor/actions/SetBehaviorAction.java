package org.petrinator.editor.actions;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import org.petrinator.editor.Root;
import org.petrinator.editor.commands.SetLabelCommand;;
import org.petrinator.petrinet.TransitionNode;
import org.petrinator.util.GraphicsTools;

/**
 * Set behavior to clicked transition
 *
 * @author Leandro Asson leoasson at gmail.com
 */
public class SetBehaviorAction extends AbstractAction{
	private Root root;

    public SetBehaviorAction(Root root) {
        this.root = root;
        String name = "Properties";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/Behavior16.gif"));
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (root.getClickedElement() != null && root.getClickedElement() instanceof TransitionNode)
        {
			TransitionNode clickedTransition = (TransitionNode) root.getClickedElement();
			String newBehavior;
			String guardValue;
			boolean automatic;
			boolean informed;
			boolean enablewhentrue;
			boolean timed;
                             
            JTextField field_guard = new JTextField(8);
            JTextField field_label = new JTextField(8);
			JTextField field_rate = new JTextField(8);
            JCheckBox checkBoxAutomatic = new JCheckBox();
            JCheckBox checkBoxInformed = new JCheckBox();
            JCheckBox checkBoxEnablewhentrue = new JCheckBox();
            JCheckBox checkBoxTimed = new JCheckBox();
            JPanel myPanel = new JPanel();

            myPanel.setLayout(new MigLayout());
			myPanel.add(new JLabel("Label:  "));
			myPanel.add(field_label,"span, grow");
			myPanel.add(new JLabel(""), "wrap");
			myPanel.add(new JLabel(""), "wrap");
			myPanel.add(new JSeparator(), "span, growx, wrap");
			myPanel.add(new JLabel(""), "wrap");
			myPanel.add(new JLabel(""), "wrap");
			myPanel.add(new JLabel("Automatic:"));
			myPanel.add(checkBoxAutomatic);
			myPanel.add(new JLabel("Informed:"));
			myPanel.add(checkBoxInformed, "wrap");
			myPanel.add(new JLabel("Timed:"));
			myPanel.add(checkBoxTimed);
			myPanel.add(new JLabel("Rate:  "));
			myPanel.add(field_rate, "wrap");
			myPanel.add(new JLabel(""), "wrap");
			myPanel.add(new JLabel(""), "wrap");
			myPanel.add(new JSeparator(), "span, growx, wrap");
			myPanel.add(new JLabel(""), "wrap");
			myPanel.add(new JLabel(""), "wrap");
			myPanel.add(new JLabel("Enable when true:"));
			myPanel.add(checkBoxEnablewhentrue);
			myPanel.add(new JLabel("Guard:  "));
			myPanel.add(field_guard, "wrap");

			//set in the panel the behavior of the transition.
			field_label.setText(clickedTransition.getLabel());
			field_guard.setText(clickedTransition.getGuard());
			field_rate.setText(Double.toString(clickedTransition.getRate()));
			checkBoxAutomatic.setSelected(clickedTransition.isAutomatic());
			checkBoxInformed.setSelected(clickedTransition.isInformed());
			checkBoxEnablewhentrue.setSelected(clickedTransition.isEnablewhentrue());
			checkBoxTimed.setSelected(clickedTransition.isTimed());

			if(clickedTransition.isTimed())
			{
				field_rate.setEnabled(true);
			}
			else
			{
				field_rate.setEnabled(false);
			}

			checkBoxTimed.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED){
						field_rate.setEnabled(true);
						field_rate.setText(Double.toString(clickedTransition.getRate()));
					}
					else if(e.getStateChange() == ItemEvent.DESELECTED){
						field_rate.setEnabled(false);
						field_rate.setText(Double.toString(clickedTransition.getRate()));
					}
					myPanel.validate();
					myPanel.repaint();
				}
			});

            int result = JOptionPane.showConfirmDialog(root.getParentFrame(), myPanel,
                    "Transition properties", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION)
            {
               guardValue = field_guard.getText();
               automatic = checkBoxAutomatic.isSelected();
               informed = checkBoxInformed.isSelected();
               timed = checkBoxTimed.isSelected();
               enablewhentrue = checkBoxEnablewhentrue.isSelected();
               clickedTransition.setGuard(field_guard.getText());
               root.getUndoManager().executeCommand(new SetLabelCommand(clickedTransition,field_label.getText()));
               clickedTransition.generateBehavior(automatic,informed,guardValue,enablewhentrue);
               clickedTransition.setAutomatic(automatic);
               clickedTransition.setInformed(informed);
               clickedTransition.setEnableWhenTrue(enablewhentrue);
               clickedTransition.setTime(timed);
               try
			   {
				   clickedTransition.setRate(Double.parseDouble(field_rate.getText()));
			   }
			   catch(NumberFormatException e1)
			   {
				   JOptionPane.showMessageDialog(null, "Invalid number");
				   return; // Don't execute further code
			   }
            }
         }
    }
}
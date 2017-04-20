package org.petrinator.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.petrinator.editor.Root;
import org.petrinator.editor.commands.SetBehaviorCommand;
import org.petrinator.exception.BehaviorException;
import org.petrinator.petrinet.Node;
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
        String name = "Set behavior";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/Behavior16.gif"));
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (root.getClickedElement() != null && root.getClickedElement() instanceof TransitionNode)
        {
            //Node clickedNode = (Node) root.getClickedElement();
			TransitionNode clickedTransition = (TransitionNode) root.getClickedElement();
                             
            JTextField guard = new JTextField(8);
            JCheckBox checkBoxAutomatic = new JCheckBox();
            JCheckBox checkBoxInformed = new JCheckBox();
            JCheckBox checkBoxEnablewhentrue = new JCheckBox();
            JPanel myPanel = new JPanel(); 
            
            String newBehavior;
            String guardValue;
            boolean automatic;
            boolean informed;
            boolean enablewhentrue;
            
            myPanel.setLayout(new MigLayout());
            myPanel.add(new JLabel("Automatic:"));
            myPanel.add(new JLabel (" "));
            myPanel.add(checkBoxAutomatic,"wrap");
            myPanel.add(new JLabel("Informed:"));
            myPanel.add(new JLabel (" "));
            myPanel.add(checkBoxInformed,"wrap");
            myPanel.add(new JLabel("Guard:  "));
            myPanel.add(new JLabel ("    "));
            myPanel.add(guard,"wrap");
            myPanel.add(new JLabel("Enable when true:"));
            myPanel.add(new JLabel (" "));
            myPanel.add(checkBoxEnablewhentrue);

			//set in the panel the behavior of the transition.
			checkBoxAutomatic.setSelected(clickedTransition.isAutomatic());
			checkBoxInformed.setSelected(clickedTransition.isInformed());
			guard.setText(clickedTransition.getGuard());
			checkBoxEnablewhentrue.setSelected(clickedTransition.isEnablewhentrue());
     
            int result = JOptionPane.showConfirmDialog(null, myPanel, 
                    "Set behavior", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) 
            {
               guardValue= guard.getText();
               automatic = checkBoxAutomatic.isSelected();
               informed = checkBoxInformed.isSelected();
               enablewhentrue = checkBoxEnablewhentrue.isSelected();
               
               newBehavior = generateBehavior(automatic,informed,guardValue,enablewhentrue);
               root.getUndoManager().executeCommand(new SetBehaviorCommand(clickedTransition, newBehavior, automatic, informed, guardValue, enablewhentrue));
            }
         }
    }

    /**
     * Generates behavior based on the selected configuration.
     * @param automatic value that determines if the transition is automatic.
     * @param informed value that determines if the transition is informed.
     * @param guardValue Name of the guard.
     * @param enablewhentrue Initial State of the guard.
     * @return behavior returns the behavior with the compatible format. The syntax is the following:
     * &lt;automatic,informed,(~guard_name)&gt;
     * where:
     * automatic can be A for the automatic transition or F for fired transition.
     * informed can be I for the informed transition or N for non-informed transition.
     * guard is the name of the guard associated on this transition.
     * Guards can be shared by any amount of transitions and can be negated using ! or ~ token before the guard name.
     * The default values are:
     * automatic: F 
     * informed: I
     * guard: none
     * initialState: false
     */
    public String generateBehavior(boolean automatic, boolean informed, String guardValue, boolean enablewhentrue) {
		String behavior;
		String statusAutomatic;
		String statusInformed;
		String statusEnablewhentrue;
		if (automatic) {
			statusAutomatic = "A";
		} else {
			statusAutomatic = "F";
		}

		if (informed) {
			statusInformed = "I";
		} else {
			statusInformed = "N";
		}

		if (enablewhentrue) {
			statusEnablewhentrue = "";
		} else {
			statusEnablewhentrue = "!";
		}
		behavior = "<" + statusAutomatic + "," + statusInformed + "," + "(" + statusEnablewhentrue + guardValue + ")" + ">";
		return behavior;
	}
}
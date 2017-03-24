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
        if (root.getClickedElement() != null
                && root.getClickedElement() instanceof Node)
        {
            Node clickedNode = (Node) root.getClickedElement();
                             
            JTextField guard = new JTextField(8);
            JCheckBox checkBoxAutomatic = new JCheckBox();
            JCheckBox checkBoxInformed = new JCheckBox();
            JCheckBox checkBoxsInitialState = new JCheckBox();            
            JPanel myPanel = new JPanel(); 
            
            String newBehavior;
            String guardValue;
            boolean automatic;
            boolean informed;
            boolean initialState;
            
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
            myPanel.add(new JLabel("Initial State:"));
            myPanel.add(new JLabel (" "));
            myPanel.add(checkBoxsInitialState);    
            
            
            
            String[] oldBehavior;
			try {
				oldBehavior = setPanel(clickedNode);
				checkBoxAutomatic.setSelected(Boolean.valueOf(oldBehavior[0]));
	            checkBoxInformed.setSelected(Boolean.valueOf(oldBehavior[1]));
	            guard.setText(oldBehavior[2]);
	            checkBoxsInitialState.setSelected(Boolean.valueOf(oldBehavior[3]));
			} catch (BehaviorException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
            
              
     
            int result = JOptionPane.showConfirmDialog(null, myPanel, 
                    "Set behavior", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) 
            {
               guardValue= guard.getText();
               automatic = checkBoxAutomatic.isSelected();
               informed = checkBoxInformed.isSelected();
               initialState = checkBoxsInitialState.isSelected();
               
               newBehavior = generateBehavior(automatic,informed,guardValue,initialState);
               root.getUndoManager().executeCommand(new SetBehaviorCommand(clickedNode, newBehavior));
            }
         }
    }
                 

    /**
     * Generates behavior based on the selected configuration.
     * @param automatic value that determines if the transition is automatic.
     * @param informed value that determines if the transition is informed.
     * @param guardValue Name of the guard.
     * @param initialState Initial State of the guard.
     * @return behavior returns the behavior with the compatible format. The syntax is the following:
     * &lt;automatic,informed,(~guard_name)&gt;
     * where:
     * automatic can be A for the automatic transition or F for fired transition.
     * informed can be I for the informed transition or N for non-informed transition.
     * guard is the name of the guard associated on this transition.
     * Guards can be shared by any amount of transitions and can be negated using ! or ~ token before the guard name.
     * The default values are:
     * \code
     * automatic: F 
     * informed: I
     * guard: none
     * initialState: false
     * \endcode
     */
    public String generateBehavior(boolean automatic, boolean informed, String guardValue, boolean initialState)
    {
    	String behavior;
    	String statusAutomatic;
    	String statusInformed;
    	String statusInitialState;
    	if (automatic){
    		statusAutomatic = "A";
    	}
    	else{
    		statusAutomatic = "F"; 
    	}
    	
    	if (informed){
    		statusInformed = "I";
    	}
    	else{
    		statusInformed = "N"; 
    	}
    	if (initialState){
    		statusInitialState = "";
    	}
    	else{
    		statusInitialState = "!"; 
    	}
    	
    	behavior="<"+statusAutomatic+","+statusInformed+","+"("+statusInitialState+guardValue+")"+">";
    
    	return behavior;
    }

    /**
     * set panel with the old values of the transition.
     *
     * @Param node clicked transition.
     * @return array with the behavior values of the transition.
     * @throws BehaviorException 
     */
    public String[] setPanel(Node node) throws BehaviorException
    {
    	
    	boolean isAutomatic = false;
		boolean isInformed = false;
		String guard = "";
		boolean isNegative = false;
    	final int AUTOMATIC_INDEX = 0;
		final int INFORMED_INDEX = 1;
		final int GUARD_INDEX = 2;
    	String[] labels = node.getBehavior().split(",");
    	for( int i = 0; i < labels.length; i++ )
    	{
			String label = labels[i];
			switch(i)
			{
			case AUTOMATIC_INDEX:
				if( !label.contains("A") && !label.contains("D") && !label.contains("F")){
					throw new BehaviorException("Wrong automatic label: " + label);
				}
				isAutomatic = label.contains("A");
				
				break;
			case INFORMED_INDEX:
				if( !label.contains("I") && !label.contains("N")){
					throw new BehaviorException("Wrong informed label: " + label);
				}
				isInformed = label.contains("I");
				break;
			case GUARD_INDEX:
				try{
					if(label.charAt(0) != '(' || label.charAt(label.length() - 2) != ')'){
					 //guard must be enclosed by brackets
					throw new BehaviorException("Bad formatted guard in " + label + "from label " + labels);
					}
					// trim the brackets
					String guardStr = label.substring(1, label.length() - 2).replaceAll("\\s", "");
					//check if it's for negative logic
					boolean negative = (guardStr.charAt(0) == '~' || guardStr.charAt(0) == '!');
					if (!negative){
						//discard first char "~"
						guardStr = guardStr.substring(0);
						isNegative = true;
					}
					else {
						guardStr = guardStr.substring(1);
						isNegative = false;					
					}
					guard = guardStr;
				} catch (IndexOutOfBoundsException e){
				  // nothing wrong, just empty guard
				}
			default:
				break;
		    }
    	}
        String [] behavior = {String.valueOf(isAutomatic),String.valueOf(isInformed), guard, String.valueOf(isNegative)};
        return behavior;
    }
}
	
	
	
	


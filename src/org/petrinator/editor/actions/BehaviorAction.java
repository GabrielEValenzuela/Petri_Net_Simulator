package org.petrinator.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.petrinator.editor.Root;
import org.petrinator.editor.commands.SeeBehaviorCommand;
import org.petrinator.util.GraphicsTools;

public class BehaviorAction extends AbstractAction {
	
	
	private Root root;

    public BehaviorAction(Root root) {
        this.root = root;
        String name = "Behavior";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/Preferences16.gif"));
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(true);
    }

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		root.getUndoManager().executeCommand(new SeeBehaviorCommand(root.getDocument().petriNet.getRootSubnet()));
	}

}

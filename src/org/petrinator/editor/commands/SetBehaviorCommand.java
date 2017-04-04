package org.petrinator.editor.commands;

import org.petrinator.petrinet.Node;
import org.petrinator.util.Command;

/**
 * Set behavior to clicked transition
 *
 * @author Leandro Asson leoasson at gmail.com
 */

public class SetBehaviorCommand implements Command{

	
	   private Node node;
	   private String newBehavior;
	   private String oldBehavior;

	    public SetBehaviorCommand(Node node, String newBehavior) {
	        this.node = node;
	        this.newBehavior = newBehavior;
	    }

	    public void execute() {
	        this.oldBehavior = node.getBehavior();
	        node.setBehavior(newBehavior);
	    }

	    public void undo() {
	        node.setBehavior(oldBehavior);
	    }

	    public void redo() {
	        execute();
	    }

	    @Override
	    public String toString() {
	        return "Set Behavior to " + newBehavior;
	    }
}

package org.petrinator.editor.commands;

import org.petrinator.petrinet.TransitionNode;
import org.petrinator.util.Command;

/**
 * Set behavior to clicked transition
 *
 * @author Leandro Asson leoasson at gmail.com
 */

public class SetBehaviorCommand implements Command
{
	   private TransitionNode transition;
	   private String newBehavior;
	   //private String oldBehavior;
	   private boolean automatic;
	   private boolean informed;
	   private String guard;
	   private boolean Enablewhentrue;


	    public SetBehaviorCommand(TransitionNode transition, String newBehavior, boolean automatic, boolean informed, String guard, boolean enable)
		{
	        this.transition = transition;
	        this.newBehavior = newBehavior;
	        this.automatic = automatic;
	        this.informed = informed;
	        this.guard = guard;
	        Enablewhentrue = enable;
	    }

	    public void execute()
		{
	    	//this.oldBehavior = transition.getBehavior();
	    	transition.setBehavior(newBehavior);
	    	transition.setAutomatic(automatic);
	    	transition.setInformed(informed);
			transition.setGuard(guard);
			transition.setEnableWhenTrue(Enablewhentrue);
	    }

	    public void undo()
		{
			//transition.setBehavior(oldBehavior);
	    }

	    public void redo()
		{
	    	//execute();
	    }

	    @Override
	    public String toString()
		{
	        return "Set Behavior to " + newBehavior;
	    }
}

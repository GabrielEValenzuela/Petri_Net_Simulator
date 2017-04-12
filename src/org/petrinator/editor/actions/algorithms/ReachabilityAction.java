package org.petrinator.editor.actions.algorithms;

import org.petrinator.editor.Root;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Joaquin Felici <joaquinfelici at gmail.com>
 * @brief
 */
public class ReachabilityAction extends AbstractAction
{
    public ReachabilityAction(Root root)
    {
        String name = "Reachability graph";
        putValue(NAME, name);
    }
    public void actionPerformed(ActionEvent e) {}
}

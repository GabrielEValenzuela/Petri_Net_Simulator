package org.petrinator.editor.actions.algorithms;

import org.petrinator.editor.Root;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Joaquin Felici <joaquinfelici at gmail.com>
 * @brief
 */
public class GSPNAction extends AbstractAction
{
    public GSPNAction(Root root)
    {
        String name = "GSPN analysis";
        putValue(NAME, name);
    }
    public void actionPerformed(ActionEvent e) {}
}

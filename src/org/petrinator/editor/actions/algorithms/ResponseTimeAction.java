package org.petrinator.editor.actions.algorithms;

import org.petrinator.editor.Root;
import org.petrinator.util.GraphicsTools;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Joaquin Felici <joaquinfelici at gmail.com>
 * @brief
 */
public class ResponseTimeAction extends AbstractAction
{
    public ResponseTimeAction(Root root)
    {
        String name = "Time response analysis";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/timeresponse16.png"));
        setEnabled(false);
    }
    public void actionPerformed(ActionEvent e) {}
}

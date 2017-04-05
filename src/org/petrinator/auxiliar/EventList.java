package org.petrinator.auxiliar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joaquin Rodriguez Felici <joaquinfelici at gmail.com>
 */
public class EventList
{
    List<String> events;
    JList<String> list;
    JScrollPane scroller;
    Color scrollerColor = new Color(220,220,220);

    public EventList()
    {
        events = new ArrayList<String>();
        list = new JList(events.toArray());
        list.setBackground(scrollerColor);
        scroller = new JScrollPane(list);
    }

    /*
     * @brief Adds event to the list and, therefore, the scrollPane
     * @param e The string that wants to be added. Ex: "T1 was fired!"
     */
    public void addEvent(String e)
    {
        events.add(e);
        list = new JList(events.toArray());
        list.setBackground(scrollerColor);
        scroller = new JScrollPane(list);
        scrollToBottom(scroller);
    }

    /*
     * @brief Provides with the scrollPane
     * @return scroller
     */
    public JScrollPane getScrollPane()
    {
        return scroller;
    }

    /*
     * @brief Scrolls down to the bottom of the list when a new event is aded
     * @param scrollPane the ScrollPane object we want to scroll
     */
    private void scrollToBottom(JScrollPane scrollPane)
    {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }
}

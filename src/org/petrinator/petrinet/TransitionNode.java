/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.petrinator.petrinet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Leandro Asson leoasson at gmail.com
 */
public abstract class TransitionNode extends Node implements Cloneable
{
    //initial state
    private String behavior = "<F,I,(!none)>";
    private boolean automatic = false;
    private boolean informed = true;
    private boolean enablewhentrue = false;
    private boolean timed = false;
    private String guard = "none";
    private double rate = 1.0;

    public Set<PlaceNode> getConnectedPlaceNodes()
    {
        Set<PlaceNode> connectedPlaceNodes = new HashSet<PlaceNode>();
        for (ArcEdge arc : getConnectedArcEdges()) {
            connectedPlaceNodes.add(arc.getPlaceNode());
        }
        return connectedPlaceNodes;
    }

    /**
     * Returns the behavior.
     *
     * @return the behavior.
     */
    public String getBehavior()
    {
        return behavior;
    }

    /**
     * Sets a new behavior.
     *
     * @param behavior - behavior to set.
     */
    public void setBehavior( String behavior)
    {
        this.behavior = behavior;
    }

    /**
     * If the transition is automatic return true, else return false.
     * @return automatic.
     */
    public boolean isAutomatic()
    {
        return automatic;
    }

    /**
     * If the transition is informed return true, else return false.
     * @return informed.
     */
    public boolean isInformed()
    {
        return informed;
    }

    /**
     * If the transition is enable when true return true, else return false.
     * @return Enablewhentrue.
     */
    public boolean isEnablewhentrue()
    {
        return enablewhentrue;
    }

    /**
     * If the transition is timed return true, else return false.
     * @return timed.
     */
    public boolean isTimed()
    {
        return timed;
    }

    /**
     * Return the name of the guard
     * @return guard.
     */
    public String getGuard()
    {
        return guard;
    }

    /**
     * Return the rate
     *
     * @return rate
     */
    public  double getRate()
    {
        return rate;
    }

    /**
     * Sets a new state.
     *
     * @param automatic - state to set.
     */
    public void setAutomatic(boolean automatic)
    {
        this.automatic = automatic;
    }

    /**
     * Sets a new state.
     *
     * @param informed - state to set.
     */
    public void setInformed(boolean informed)
    {
        this.informed = informed;
    }

    /**
     * Sets a new name of guard.
     *
     * @param guard - state to set.
     */
    public void setGuard(String guard)
    {
        this.guard = guard;
    }

    /**
     * Sets a new state.
     *
     * @param enablewhentrue - state to set.
     */
    public void setEnableWhenTrue(boolean enablewhentrue)
    {
        this.enablewhentrue = enablewhentrue;
    }

    /**
     * Sets a new rate.
     *
     * @param rate - state to set.
     */
    public void setRate(double rate)
    {
        this.rate = rate;
    }

    /**
     * Set time.
     *
     * @param timed - time to set.
     */
    public void setTime(boolean timed)
    {
        this.timed = timed;
    }


}

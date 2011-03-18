/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.curious.shoda.actors;

import org.curious.felidae.media.Input;
import org.curious.felidae.media.Renderer;
import org.curious.felidae.state.Actor;
import org.curious.felidae.state.State;
import org.jbox2d.dynamics.contacts.ContactPoint;

/**
 *
 * @author Jarl
 */
public class Destroyer implements Actor {
    public boolean live;

    public String getName() {
        return "Destroyer";
    }

    public void render(Renderer renderer) {
    }

    public boolean update(State state) {
        return live;
    }

    public void processInput(Input input) {
    }

    public void processContact(ContactPoint contactPoint) {
    }

}

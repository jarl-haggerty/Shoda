/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.curious.shoda.actors;

import java.awt.Color;
import org.curious.felidae.media.Input;
import org.curious.felidae.media.Renderer;
import org.curious.felidae.state.Actor;
import org.curious.felidae.state.State;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.contacts.ContactPoint;

/**
 *
 * @author Jarl
 */
public class Fighter implements Actor {
    public Body body;
    public Impulse impulse;
    public Vec2 position;
    public boolean live;

    public Fighter(Vec2 position, Impulse impulse){
        this.impulse = impulse;
        this.position = position;
        this.live = true;
    }

    public interface Impulse{
        public Vec2 getImpulse(Fighter fighter);
    }

    public String getName() {
        return "Fighter";
    }

    public void render(Renderer renderer) {
        renderer.setColor(Color.blue);
        renderer.drawBody(body);
    }

    public boolean update(State state) {
        if(body == null){
            BodyDef bodyDef = new BodyDef();
            bodyDef.position = position;
            bodyDef.userData = this;
            body = state.world.createBody(bodyDef);
            PolygonDef polygonDef = new PolygonDef();
            polygonDef.density = 16;
            polygonDef.isSensor = true;
            polygonDef.filter.groupIndex = -1;
            polygonDef.addVertex(new Vec2(0, -.25f));
            polygonDef.addVertex(new Vec2(.125f, .25f));
            polygonDef.addVertex(new Vec2(-.125f, .25f));
            body.createShape(polygonDef);
        }
        body.applyImpulse(impulse.getImpulse(this), body.getWorldCenter());
        return live;
    }

    public void processInput(Input input) {
    }

    public void processContact(ContactPoint contactPoint) {
        if(contactPoint.shape2.getBody().getUserData() instanceof Bullet){
            live = false;
        }
    }

}

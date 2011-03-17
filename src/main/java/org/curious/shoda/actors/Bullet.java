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
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.contacts.ContactPoint;

/**
 *
 * @author Jarl
 */
class Bullet implements Actor{
    public Vec2 position;
    public Body body;
    public Object level;
    public boolean live;

    public Bullet(float x, float y) {
        position = new Vec2(x, y);
        body = null;
        live = false;
    }

    public String getName() {
        return "Bullet";
    }

    public void render(Renderer renderer) {
        renderer.setTexture(null);
        renderer.setColor(Color.white);
        renderer.transform(new Vec3(0, 0, 1), 0);
        renderer.drawBody(body);
    }

    public boolean update(State state) {
        if(body == null){
            BodyDef bodyDef = new BodyDef();
            bodyDef.position = position;
            bodyDef.userData = this;
            body = state.world.createBody(bodyDef);
            CircleDef pointDef = new CircleDef();
            pointDef.isSensor = true;
            pointDef.localPosition = new Vec2(0, 0);
            pointDef.radius = 0.01f;
            pointDef.density = 1/(float)Math.PI/pointDef.radius/pointDef.radius;
            pointDef.filter.groupIndex = 1;
            body.createShape(pointDef);
            body.setMassFromShapes();
            body.applyImpulse(new Vec2(0, 3), body.getWorldCenter());
            level = state.getActor("Level").seq().first();
        }
        return live;
    }

    public void processInput(Input input) {
    }

    public void processContact(ContactPoint contactPoint) {
        if(contactPoint.shape2.getBody().getUserData() == level){
            live = false;
        }
    }

}

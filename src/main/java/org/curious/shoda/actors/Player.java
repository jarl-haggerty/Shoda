/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.curious.shoda.actors;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Map;
import org.curious.felidae.media.Input;
import org.curious.felidae.media.Renderer;
import org.curious.felidae.state.Actor;
import org.curious.felidae.state.State;
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.PointDef;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.contacts.ContactPoint;

/**
 *
 * @author Jarl
 */
public class Player implements Actor {
    public Body body;
    public boolean moveLeft, moveRight, moveUp, moveDown, shoot;
    public long gunTimer, gunPeriod;

    public Player(Map<String, String> data){
        body = null;
        moveLeft = false;
        moveDown = false;
        moveRight = false;
        moveUp = false;
        shoot = false;
        gunTimer = System.currentTimeMillis();
        gunPeriod = 100;
    }

    public String getName() {
        return "Player";
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
            bodyDef.fixedRotation = true;
            bodyDef.position = new Vec2(100f/32, 0.3f);
            body = state.world.createBody(bodyDef);
            CircleDef pointDef = new CircleDef();
            pointDef.localPosition = new Vec2(0, 0);
            pointDef.radius = 0.01f;
            pointDef.density = 1/(float)Math.PI/pointDef.radius/pointDef.radius;
            pointDef.filter.groupIndex = 1;
            body.createShape(pointDef);
            body.setMassFromShapes();
        }
        body.applyImpulse(body.getLinearVelocity().mul(-body.getMass()), body.getWorldCenter());
        if(moveLeft){
            body.applyImpulse(new Vec2(-3, 0), body.getWorldCenter());
        }
        if(moveDown){
            body.applyImpulse(new Vec2(0, -3), body.getWorldCenter());
        }
        if(moveRight){
            body.applyImpulse(new Vec2(3, 0), body.getWorldCenter());
        }
        if(moveUp){
            body.applyImpulse(new Vec2(0, 3), body.getWorldCenter());
        }
        if(shoot){
            if(System.currentTimeMillis() - gunTimer > gunPeriod){
                state.addActor(new Bullet(body.getWorldCenter().x - 0.05f, body.getWorldCenter().y));
                state.addActor(new Bullet(body.getWorldCenter().x + 0.05f, body.getWorldCenter().y));
                gunTimer = System.currentTimeMillis();
            }
        }else{
            gunTimer = System.currentTimeMillis();
        }
        return true;
    }

    public void processInput(Input input) {
        KeyEvent keyEvent = (KeyEvent)input.event;
        if(keyEvent.getKeyCode() == KeyEvent.VK_LEFT){
            moveLeft = input.value > .5;
        }else if(keyEvent.getKeyCode() == KeyEvent.VK_DOWN){
            moveDown = input.value > .5;
        }else if(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT){
            moveRight = input.value > .5;
        }else if(keyEvent.getKeyCode() == KeyEvent.VK_UP){
            moveUp = input.value > .5;
        }else if(keyEvent.getKeyCode() == KeyEvent.VK_SPACE){
            shoot = input.value > .5;
        }
    }

    public void processContact(ContactPoint contactPoint) {
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.curious.shoda.actors;

import com.jogamp.opengl.util.texture.Texture;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.curious.felidae.media.Input;
import org.curious.felidae.media.Renderer;
import org.curious.felidae.media.VBO;
import org.curious.felidae.state.Actor;
import org.curious.felidae.state.State;
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
public class LevelOne implements Actor {
    public Body walls;
    public VBO backgroundVBO, panelVBO;
    public Texture backgroundTexture, panelTexture;
    public boolean quit;
    public long startScroll;
    public Queue<Event> schedule;

    public class Event{
        public Actor actor;
        public long time;

        public Event(Actor actor, long time){
            this.actor = actor;
            this.time = time;
        }
    }

    public class SimpleImpulse implements Fighter.Impulse{
        public Vec2 direction;
        public boolean downward = true;
        public long coolOff;
        public SimpleImpulse(Vec2 direction){
            this.direction = direction;
        }
        public Vec2 getImpulse(Fighter fighter) {
        if(downward){
                if(fighter.body.getWorldCenter().y > 5){
                    return direction;
                }else{
                    coolOff = System.currentTimeMillis();
                    downward = false;
                    return fighter.body.getLinearVelocity().mul(-fighter.body.getMass());
                }
            }else{
                if(System.currentTimeMillis()-coolOff > 2000){
                    return new Vec2(0, -3);
                }else{
                    return new Vec2(0, 0);
                }
            }
        }
    }

    public LevelOne(Map<String, String> data){
        walls = null;
        quit = false;
        backgroundTexture = null;
        backgroundVBO = null;
        startScroll = Long.MAX_VALUE;
        schedule = new LinkedList<Event>();
        float fifth = 100f/32/5;
        schedule.add(new Event(new Fighter(new Vec2(-100f/32+.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));
        schedule.add(new Event(new Fighter(new Vec2(-100f/32+fifth+.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));
        schedule.add(new Event(new Fighter(new Vec2(-100f/32+2*fifth+.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));
        schedule.add(new Event(new Fighter(new Vec2(-100f/32+3*fifth+.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));
        schedule.add(new Event(new Fighter(new Vec2(-100f/32+4*.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));

        schedule.add(new Event(new Fighter(new Vec2(100f/16+100f/32-.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));
        schedule.add(new Event(new Fighter(new Vec2(100f/16+100f/32-fifth-.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));
        schedule.add(new Event(new Fighter(new Vec2(100f/16+100f/32-2*fifth-.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));
        schedule.add(new Event(new Fighter(new Vec2(100f/16+100f/32-3*fifth-.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));
        schedule.add(new Event(new Fighter(new Vec2(100f/16+100f/32-4*.125f, 10), new SimpleImpulse(new Vec2(-3, -3))), 5000));
    }

    public String getName() {
        return "Level";
    }

    public void render(Renderer renderer) {
        float screenWidth = 10*renderer.getViewRatio();
        float viewWidth = 100f/16, viewHeight = 10;
        if(backgroundTexture == null){
            List<Vec2> vertices = new LinkedList<Vec2>(), texels = new LinkedList<Vec2>();
            vertices.add(new Vec2(0, 0));
            vertices.add(new Vec2(viewWidth, 0));
            vertices.add(new Vec2(viewWidth, viewHeight));
            vertices.add(new Vec2(0, viewHeight));
            texels.add(new Vec2(0, 1));
            texels.add(new Vec2(1, 1));
            texels.add(new Vec2(1, 0));
            texels.add(new Vec2(0, 0));
            backgroundVBO = renderer.createVBO(vertices, texels);
            backgroundTexture = renderer.loadTexture("Textures/TitleBackground.png");

            vertices.clear();
            vertices.add(new Vec2(0, 0));
            vertices.add(new Vec2(screenWidth/2-viewWidth/2, 0));
            vertices.add(new Vec2(screenWidth/2-viewWidth/2, viewHeight));
            vertices.add(new Vec2(0, viewHeight));
            texels.clear();
            texels.add(new Vec2(0, 1));
            texels.add(new Vec2(1, 1));
            texels.add(new Vec2(1, 0));
            texels.add(new Vec2(0, 0));
            panelVBO = renderer.createVBO(vertices, texels);
            panelTexture = renderer.loadTexture("Textures/Panel.png");
            startScroll = System.currentTimeMillis();
        }
        renderer.setView(-(screenWidth/2-viewWidth/2), 0, screenWidth, 10);
        
        renderer.setColor(Color.white);

        renderer.transform(new Vec3(0, 0, 2), 0);
        renderer.translateTexture(new Vec2(0, (startScroll - System.currentTimeMillis())/10000f));
        renderer.setTexture(backgroundTexture);
        renderer.renderVBO(backgroundVBO);

        renderer.setTexture(panelTexture);
        renderer.transform(new Vec3(-(screenWidth/2-viewWidth/2), 0, 0), 0);
        renderer.renderVBO(panelVBO);
        renderer.transform(new Vec3(viewWidth, 0, 0), 0);
        renderer.renderVBO(panelVBO);
    }

    public boolean update(State state) {
        if(walls == null){
            float width = 100f/16;
            BodyDef bodyDef = new BodyDef();
            bodyDef.position = new Vec2(0, 0);
            bodyDef.userData = this;
            walls = state.world.createBody(bodyDef);
            PolygonDef polygonDef = new PolygonDef();
            //polygonDef.filter.groupIndex = -1;
            polygonDef.density = 0;
            polygonDef.addVertex(new Vec2(-1, -1));
            polygonDef.addVertex(new Vec2(0, -1));
            polygonDef.addVertex(new Vec2(0, 11));
            polygonDef.addVertex(new Vec2(-1, 11));
            walls.createShape(polygonDef);
            polygonDef.clearVertices();
            polygonDef.addVertex(new Vec2(-1, -1));
            polygonDef.addVertex(new Vec2(width+1, -1));
            polygonDef.addVertex(new Vec2(width+1, 0));
            polygonDef.addVertex(new Vec2(-1, 0));
            walls.createShape(polygonDef);
            polygonDef.clearVertices();
            polygonDef.addVertex(new Vec2(width, -1));
            polygonDef.addVertex(new Vec2(width+1, -1));
            polygonDef.addVertex(new Vec2(width+1, 11));
            polygonDef.addVertex(new Vec2(width, 11));
            walls.createShape(polygonDef);
            polygonDef.clearVertices();
            polygonDef.addVertex(new Vec2(-1, 10));
            polygonDef.addVertex(new Vec2(width+1, 10));
            polygonDef.addVertex(new Vec2(width+1, 11));
            polygonDef.addVertex(new Vec2(-1, 11));
            walls.createShape(polygonDef);
        }
        while(schedule.peek() != null && schedule.peek().time >= System.currentTimeMillis() - startScroll){
            state.addActor(schedule.remove().actor);
        }
        if(quit){
            state.game.stop();
        }
        return true;
    }

    public void processInput(Input input) {
        if(((KeyEvent)input.event).getKeyCode() == KeyEvent.VK_ESCAPE){
            quit = true;
        }
    }

    public void processContact(ContactPoint contactPoint) {
    }
}

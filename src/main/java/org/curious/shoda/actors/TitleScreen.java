/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.curious.shoda.actors;

import com.jogamp.opengl.util.texture.Texture;
import java.awt.DisplayMode;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.curious.felidae.media.Input;
import org.curious.felidae.media.Renderer;
import org.curious.felidae.media.VBO;
import org.curious.felidae.state.Actor;
import org.curious.felidae.state.State;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.contacts.ContactPoint;

/**
 *
 * @author Jarl
 */
public class TitleScreen implements Actor {
    public Texture backgroundTexture, pressSpaceTexture;
    public VBO backgroundVBO, pressSpaceVBO;
    public boolean startGame, quit;

    public TitleScreen(Map<String, String> data){
        backgroundTexture = null;
        pressSpaceTexture = null;
        startGame = false;
        quit = false;
    }

    public String getName() {
        return "TitleScreen";
    }

    public void render(Renderer renderer) {
        if(backgroundTexture == null){
            DisplayMode displayMode = renderer.getDisplayMode();
            backgroundTexture = renderer.loadTexture("Textures/TitleBackground.png");
            float border = 0.5f*displayMode.getWidth()/backgroundTexture.getImageWidth();

            List<Vec2> vertices = new LinkedList<Vec2>(), texels = new LinkedList<Vec2>();
            vertices.add(new Vec2(0, 0));
            vertices.add(new Vec2(displayMode.getWidth(), 0));
            vertices.add(new Vec2(displayMode.getWidth(), displayMode.getHeight()));
            vertices.add(new Vec2(0, displayMode.getHeight()));
            texels.add(new Vec2(0.5f-border, 1));
            texels.add(new Vec2(0.5f+border, 1));
            texels.add(new Vec2(0.5f+border, 0));
            texels.add(new Vec2(0.5f-border, 0));
            backgroundVBO = renderer.createVBO(vertices, texels);

            pressSpaceTexture = renderer.loadTexture("Textures/PressSpace.png");
            vertices.clear();texels.clear();
            vertices.add(new Vec2(displayMode.getWidth()/2-pressSpaceTexture.getImageWidth()/2, displayMode.getHeight()/4-pressSpaceTexture.getImageHeight()/2));
            vertices.add(new Vec2(displayMode.getWidth()/2+pressSpaceTexture.getImageWidth()/2, displayMode.getHeight()/4-pressSpaceTexture.getImageHeight()/2));
            vertices.add(new Vec2(displayMode.getWidth()/2+pressSpaceTexture.getImageWidth()/2, displayMode.getHeight()/4+pressSpaceTexture.getImageHeight()/2));
            vertices.add(new Vec2(displayMode.getWidth()/2-pressSpaceTexture.getImageWidth()/2, displayMode.getHeight()/4+pressSpaceTexture.getImageHeight()/2));
            texels.add(new Vec2(0, 1));
            texels.add(new Vec2(1, 1));
            texels.add(new Vec2(1, 0));
            texels.add(new Vec2(0, 0));
            pressSpaceVBO = renderer.createVBO(vertices, texels);
            renderer.setView(0, 0, renderer.getDisplayMode().getWidth(), renderer.getDisplayMode().getHeight());
            renderer.transform(new Vec2(0, 0), 0);
        }
        renderer.transform(new Vec3(0, 0, 2), 0);
        renderer.setTexture(backgroundTexture);
        renderer.renderVBO(backgroundVBO);
        if(System.currentTimeMillis() % 1000 < 500){
            renderer.transform(new Vec3(0, 0, 0), 0);
            renderer.setTexture(pressSpaceTexture);
            renderer.renderVBO(pressSpaceVBO);
        }
    }

    public boolean update(State state) {
        if(quit){
            state.game.stop();
            return true;
        }
        if(startGame){
            state.load("States/LevelOne.xml");
        }
        return true;
    }

    public void processInput(Input input) {
        if(((KeyEvent)input.event).getKeyCode() == KeyEvent.VK_SPACE){
            startGame = true;
        }else if(((KeyEvent)input.event).getKeyCode() == KeyEvent.VK_ESCAPE){
            quit = true;
        }
    }

    public void processContact(ContactPoint contactPoint) {
    }
}

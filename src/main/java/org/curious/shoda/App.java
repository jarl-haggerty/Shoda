package org.curious.shoda;

import org.curious.felidae.Game;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Game game = new Game("Shoda");
        game.start("States/TitleScreen.xml");
    }
}

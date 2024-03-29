/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw.saarland.gamedev.nap;

import de.htw.saarland.gamedev.nap.client.GameClient;
import de.htw.saarland.gamedev.nap.launcher.FrameConnect;
import de.htw.saarland.gamedev.nap.launcher.FrameLauncher;
import de.htw.saarland.gamedev.nap.launcher.IGameStart;
import de.htw.saarland.gamedev.nap.launcher.ILauncherStart;

import javax.swing.UIManager;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import sfs2x.client.SmartFox;

/**
 * This class is responsible for switching between the Launcher and the "real" game
 * 
 * @author Pascal
 */
public class GameStarter implements IGameStart, ILauncherStart {
    
    SmartFox sfClient;
    FrameLauncher launcher;
    FrameConnect frameConnect;
    
    public void run() {
        sfClient = new SmartFox(false);
        
        frameConnect = new FrameConnect(sfClient, this);
        frameConnect.setVisible(true);
    }

    @Override
    public void startGame() {
        sfClient.enableLagMonitor(false);
        sfClient.removeAllEventListeners(); //remove all Launcher event handlers
        
        launcher.dispose();
        
        new LwjglApplication(new GameClient(sfClient, sfClient.getLastJoinedRoom()), "Nap - Not another platformer", 480, 320, true);
    }

    @Override
    public void startLauncher() {
        sfClient.removeAllEventListeners();
        
        frameConnect.dispose();
        
        launcher = new FrameLauncher(sfClient, this);
        launcher.setVisible(true);
    }
    
        /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrameLauncher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrameLauncher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrameLauncher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrameLauncher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        new GameStarter().run();
    }
}

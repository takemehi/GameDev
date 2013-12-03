/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw.saarland.gamedev.nap.launcher;

import de.htw.saarland.gamedev.nap.launcher.handler.PacketHandler;
import javax.swing.UIManager;
import sfs2x.client.SmartFox;
import sfs2x.client.core.SFSEvent;

/**
 *
 * @author Pascal
 */
public class LauncherStarter {
    
    SmartFox sfClient;
    
    public void run() {
        sfClient = new SmartFox(true);
        
        FrameLauncher launcher = new FrameLauncher(sfClient);
        launcher.setVisible(true);
        
        PacketHandler handler = new PacketHandler(launcher);
        
        sfClient.addEventListener(SFSEvent.CONNECTION, handler);
        sfClient.addEventListener(SFSEvent.CONNECTION_LOST, handler);
        sfClient.addEventListener(SFSEvent.LOGIN, handler);
        sfClient.addEventListener(SFSEvent.LOGIN_ERROR, handler);
        sfClient.addEventListener(SFSEvent.ROOM_JOIN, handler);
        sfClient.addEventListener(SFSEvent.ROOM_JOIN_ERROR, handler);
        sfClient.addEventListener(SFSEvent.SOCKET_ERROR, handler);
        sfClient.addEventListener(SFSEvent.PUBLIC_MESSAGE, handler);
        sfClient.addEventListener(SFSEvent.ROOM_ADD, handler);
        sfClient.addEventListener(SFSEvent.ROOM_CREATION_ERROR, handler);
        sfClient.addEventListener(SFSEvent.USER_ENTER_ROOM, handler);
        sfClient.addEventListener(SFSEvent.USER_EXIT_ROOM, handler);
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
        
        new LauncherStarter().run();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw.saarland.gamedev.nap.launcher.handler;

import com.smartfoxserver.v2.exceptions.SFSException;
import de.htw.saarland.gamedev.nap.launcher.FrameLauncher;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.Room;
import sfs2x.client.entities.User;

/**
 *
 * @author Pascal
 */
public class PacketHandler implements IEventListener {

    private FrameLauncher frameLauncher;
    
    public PacketHandler(FrameLauncher frame) {
        this.frameLauncher = frame;
    }
    
    public void dispatch(BaseEvent be) throws SFSException {
        switch(be.getType()) {
            case SFSEvent.CONNECTION:
                connected(be);
                break;
            case SFSEvent.CONNECTION_LOST:
                connection_lost(be);
                break;
            case SFSEvent.LOGIN:
                login(be);
                break;
            case SFSEvent.LOGIN_ERROR:
                loginError(be);
                break;
            case SFSEvent.ROOM_JOIN:
                roomJoin(be);
                break;
            case SFSEvent.ROOM_JOIN_ERROR:
                roomJoinError(be);
                break;
            case SFSEvent.SOCKET_ERROR:
                socketError(be);
                break;
            case SFSEvent.PUBLIC_MESSAGE:
                publicMessage(be);
                break;
            case SFSEvent.ROOM_ADD:
                roomAdd(be);
                break;
            case SFSEvent.ROOM_CREATION_ERROR:
                roomCreationError(be);
                break;
            case SFSEvent.USER_ENTER_ROOM:
                userEnterRoom(be);
                break;
            case SFSEvent.USER_EXIT_ROOM:
                userExitRoom(be);
                break;
        }
    }

    private void connected(BaseEvent be) {
        if ((Boolean)be.getArguments().get("success")) {
            frameLauncher.messageReceived("System", "Connection established");
        }
        else {
            frameLauncher.messageReceived("System", "Connection failed");
        }
    }

    private void connection_lost(BaseEvent be) {
        frameLauncher.messageReceived("System", "Connection lost");
    }

    private void login(BaseEvent be) {
        User user = (User)be.getArguments().get("user");
        
        frameLauncher.messageReceived("System", "Login successfull. Your name is: " + user.getName());
    }

    private void loginError(BaseEvent be) {
        frameLauncher.messageReceived("System", "Login failed");
    }

    private void roomJoin(BaseEvent be) {
        Room room = (Room)be.getArguments().get("room");
        
        frameLauncher.messageReceived("System", "Successfully joined room " + room.getName());
        
        frameLauncher.roomJoined(room);
    }

    private void roomJoinError(BaseEvent be) {
        frameLauncher.messageReceived("System", "Room Join failed");
    }

    private void socketError(BaseEvent be) {
        String reason = (String)be.getArguments().get("reason");
        frameLauncher.messageReceived("System", "SOCKET ERROR!!!!! Reason: " + reason);
    }

    private void publicMessage(BaseEvent be) {
        User sender = (User)be.getArguments().get("sender");
        String message = (String)be.getArguments().get("message");
        
        frameLauncher.messageReceived(sender.getName(), message);
    }

    private void roomAdd(BaseEvent be) {
        // TODO
    }

    private void roomCreationError(BaseEvent be) {
        frameLauncher.messageReceived("System", "Error creating room! " + (String)be.getArguments().get("errorMessage"));
    }

    private void userEnterRoom(BaseEvent be) {
        User user = (User)be.getArguments().get("user");
        Room room = (Room)be.getArguments().get("room");
        
        frameLauncher.messageReceived("System", user.getName() + " joined room " + room.getName());
        frameLauncher.userEnteredRoom(user, room);
    }

    private void userExitRoom(BaseEvent be) {
        User user = (User)be.getArguments().get("user");
        Room room = (Room)be.getArguments().get("room");
        
        frameLauncher.messageReceived("System", user.getName() + " left room " + room.getName());
        frameLauncher.userLeftRoom(user, room);
    }
    
}

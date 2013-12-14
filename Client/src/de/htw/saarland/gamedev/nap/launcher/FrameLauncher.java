/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw.saarland.gamedev.nap.launcher;

import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

import de.htw.saarland.gamedev.nap.NetworkConstants;
import de.htw.saarland.gamedev.nap.launcher.panels.PanelCreateGame;
import de.htw.saarland.gamedev.nap.launcher.panels.PanelGameInfo;
import de.htw.saarland.gamedev.nap.server.extension.launcher.LauncherOpcodes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.Room;
import sfs2x.client.entities.User;
import sfs2x.client.entities.managers.IRoomManager;
import sfs2x.client.requests.JoinRoomRequest;
import sfs2x.client.requests.PublicMessageRequest;
import sfs2x.client.requests.RoomExtension;
import sfs2x.client.requests.game.CreateSFSGameRequest;
import sfs2x.client.requests.game.SFSGameSettings;

/**
 * The Launcher Frame. Receives also network events for launcher based tasks
 * 
 * @author Pascal
 */
public class FrameLauncher extends javax.swing.JFrame implements IEventListener, ICreateGame {
    
    private SmartFox sfClient;
    private Room joinedRoom;
    
    private DefaultListModel<String> availableUsers;
    private DefaultListModel<String> availableRooms;
    
    private PanelGameInfo gameInfo;
    
    /**
     * Creates new form FrameLauncher
     */
    public FrameLauncher(SmartFox sfClient) {
        this.sfClient = sfClient;
        this.gameInfo = null;
        
        sfClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
        sfClient.addEventListener(SFSEvent.ROOM_JOIN, this);
        sfClient.addEventListener(SFSEvent.ROOM_JOIN_ERROR, this);
        sfClient.addEventListener(SFSEvent.SOCKET_ERROR, this);
        sfClient.addEventListener(SFSEvent.PUBLIC_MESSAGE, this);
        sfClient.addEventListener(SFSEvent.ROOM_ADD, this);
        sfClient.addEventListener(SFSEvent.ROOM_REMOVE, this);
        sfClient.addEventListener(SFSEvent.ROOM_CREATION_ERROR, this);
        sfClient.addEventListener(SFSEvent.USER_ENTER_ROOM, this);
        sfClient.addEventListener(SFSEvent.USER_EXIT_ROOM, this);
        sfClient.addEventListener(SFSEvent.PING_PONG, this);
        sfClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);
        
        availableUsers = new DefaultListModel<>();
        availableRooms = new DefaultListModel<>();
        initComponents();
        
        if (sfClient.isConnected()) {
            messageReceived("System", "Successfully connected to Server. Your username is " + sfClient.getMySelf().getName());
        }
        
        //join lobby
        sfClient.enableLagMonitor(true);
        sfClient.send(new JoinRoomRequest(NetworkConstants.LOBBY_ROOM_NAME));
        
        updateAvailableRooms(sfClient.getRoomList());
    }
    
    private void sendMessage() {
        if (textFieldChat.getText().trim().length() > 0) {
            sfClient.send(new PublicMessageRequest(textFieldChat.getText()));
        }
    }
    
    
    @Override
    public void createGame(String gameName, int teamSize, String mapName) {
        SFSGameSettings settings = new SFSGameSettings(gameName);
        settings.setGame(true);
        settings.setMaxSpectators(0);
        settings.setMaxUsers(teamSize * 2);
        settings.setMinPlayersToStartGame(teamSize * 2);
        settings.setPublic(true);
        settings.setNotifyGameStarted(true);
        settings.setExtension(new RoomExtension("nap", "de.htw.saarland.gamedev.nap.server.extension.ServerExtension"));
        
        sfClient.send(new CreateSFSGameRequest(settings));
    }
    
    private void createGameRoom() {
        contentPanel.removeAll();
        
        contentPanel.add(new PanelCreateGame(this));
        contentPanel.revalidate();
    }
    
    public void messageReceived(String from, String text) {
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        
        textAreaChatLog.setText(textAreaChatLog.getText() + 
                "[" + formatter.format(new Date(System.currentTimeMillis())) + "] " +
                from + ": " + text + "\n");
    }
    
    private void updateAvailableUsers(final Room room) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                availableUsers.clear();
                
                for (User user: room.getUserList()) {
                    availableUsers.addElement(user.getName());
                }
            }
        });
    }
    
    /**
     * Updates the list of available rooms
     * 
     * @param currentRooms the current rooms as a list (note you can get the rooms or a filtered view of the rooms by
     *      the IRoomManager (room.getRoomManager))
     */
    private void updateAvailableRooms(final List<Room> currentRooms) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                availableRooms.clear();
                
                for (Room room: currentRooms) {
                    availableRooms.addElement(room.getName());
                }
            }
        });
    }
    
    @Override
    public void dispatch(BaseEvent be) throws SFSException {
        switch(be.getType()) {
            case SFSEvent.CONNECTION_LOST:
                JOptionPane.showMessageDialog(this,
                        "Connection to server lost!\n" + be.getArguments().get(NetworkConstants.ERROR_MESSAGE_KEY),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                
                System.exit(-1);
                break;
            case SFSEvent.ROOM_JOIN:
                Room roomJoin = (Room) be.getArguments().get(NetworkConstants.ROOM_KEY);
                
                joinedRoom = roomJoin;
                System.out.println(roomJoin);
                
                if (roomJoin.isGame()) {
                    gameInfo = new PanelGameInfo(sfClient, roomJoin);
                    
                    contentPanel.removeAll();
                    contentPanel.add(gameInfo);
                    contentPanel.revalidate();
                }
                
                updateAvailableUsers(roomJoin);
                break;
            case SFSEvent.ROOM_JOIN_ERROR:
                JOptionPane.showMessageDialog(this,
                        "Error joining a room!\n" + be.getArguments().get(NetworkConstants.ERROR_MESSAGE_KEY),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            case SFSEvent.SOCKET_ERROR:
                JOptionPane.showMessageDialog(this,
                        "There was a problem with your connection!\n" + be.getArguments().get(NetworkConstants.ERROR_MESSAGE_KEY),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            case SFSEvent.PUBLIC_MESSAGE:
                publicMessageReceived(be);
                break;
            case SFSEvent.ROOM_ADD:
                Room roomAdd = (Room) be.getArguments().get(NetworkConstants.ROOM_KEY);
                
                updateAvailableRooms(roomAdd.getRoomManager().getRoomList());
                break;
            case SFSEvent.ROOM_REMOVE:
                Room roomRemove = (Room) be.getArguments().get(NetworkConstants.ROOM_KEY);
                updateAvailableRooms(roomRemove.getRoomManager().getRoomList());
                break;
            case SFSEvent.ROOM_CREATION_ERROR:
                JOptionPane.showMessageDialog(this,
                        "Error creating room!\n" + be.getArguments().get(NetworkConstants.ERROR_MESSAGE_KEY),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            case SFSEvent.USER_ENTER_ROOM:
                Room roomNewUser = (Room) be.getArguments().get(NetworkConstants.ROOM_KEY);
                
                updateAvailableUsers(roomNewUser);
                break;
            case SFSEvent.USER_EXIT_ROOM:
                Room roomUserLeft = (Room) be.getArguments().get(NetworkConstants.ROOM_KEY);
                
                updateAvailableUsers(roomUserLeft);
                break;
            case SFSEvent.PING_PONG:
                pingReceived((int)be.getArguments().get(NetworkConstants.LAG_VALUE_KEY));
                break;
            case SFSEvent.EXTENSION_RESPONSE:
            	extensionResponse(be);
            	break;
            default:
                System.out.println("Server Packet not handled: " + be.getType());
                break;
        }
    }
    
    private void extensionResponse(BaseEvent be) {
    	String cmd = (String)be.getArguments().get(NetworkConstants.CMD_KEY);
    	SFSObject params = (SFSObject)be.getArguments().get(NetworkConstants.PARAMS_KEY);
    	
    	switch (cmd) {
			case LauncherOpcodes.LAUNCHER_CHANGE_CHARACTER_SUCCESS:
		    	SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						messageReceived("System", "Change character success");
					}
				});
		    	break;
		    case LauncherOpcodes.LAUNCHER_CHANGE_CHARACTER_ERROR:
		    	SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						messageReceived("System", "Change character error!");
					}
				});
		    	break;
		    case LauncherOpcodes.LAUNCHER_CHANGE_TEAM_ERROR:
		    	final String changeTeamErrorMsg = params.getUtfString(LauncherOpcodes.ERROR_MESSAGE_PARAMETER);
		    	SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						messageReceived("System", "Change Team error: " + changeTeamErrorMsg);
					}
				});
		    	break;
		    case LauncherOpcodes.LAUNCHER_TEAMS_CHANGED:
		    	if (gameInfo != null) {
		    		gameInfo.teamsChanged(params);
		    	}
		    	else {
		    		System.out.println("gameInfo null!");
		    	}
		    	break;
		    default:
		        System.out.println("Server Extension Packet not handled: " + cmd);
		        break;
    	}
    }
    
    private void pingReceived(final int lagValue) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblPing.setText(Integer.toString(lagValue) + " ms");
            }
        });
    }
    
    private void publicMessageReceived(BaseEvent be) {
        final User user = ((User)be.getArguments().get(NetworkConstants.SENDER_KEY));
        final String message = (String)be.getArguments().get(NetworkConstants.MESSAGE_KEY);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (user.isItMe()) {
                    textFieldChat.setText("");
                }
                
                messageReceived(user.getName(), message);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        infoPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblPing = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        matchSelectorPanel = new javax.swing.JPanel();
        matchSelectorButtonPanel = new javax.swing.JPanel();
        buttonCreateGame = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listRooms = new javax.swing.JList();
        chatAndSettingsPanel = new javax.swing.JPanel();
        settingsButtonPanel = new javax.swing.JPanel();
        chatPanel = new javax.swing.JPanel();
        textFieldChat = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAreaChatLog = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        listUsers = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Nap Launcher");
        setMaximumSize(new java.awt.Dimension(1280, 720));
        setMinimumSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainPanel.setLayout(new java.awt.BorderLayout());

        infoPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        infoPanel.setMinimumSize(new java.awt.Dimension(100, 30));
        infoPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Ping:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 8);
        infoPanel.add(jLabel1, gridBagConstraints);

        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 633, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        infoPanel.add(jPanel1, gridBagConstraints);

        lblPing.setText("0 ms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        infoPanel.add(lblPing, gridBagConstraints);

        mainPanel.add(infoPanel, java.awt.BorderLayout.NORTH);

        contentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        contentPanel.setLayout(new java.awt.CardLayout());
        mainPanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        matchSelectorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        matchSelectorPanel.setMinimumSize(new java.awt.Dimension(150, 450));
        matchSelectorPanel.setPreferredSize(new java.awt.Dimension(150, 450));
        matchSelectorPanel.setLayout(new java.awt.BorderLayout());

        matchSelectorButtonPanel.setPreferredSize(new java.awt.Dimension(150, 100));

        buttonCreateGame.setText("Create Game");
        buttonCreateGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCreateGameActionPerformed(evt);
            }
        });
        matchSelectorButtonPanel.add(buttonCreateGame);

        matchSelectorPanel.add(matchSelectorButtonPanel, java.awt.BorderLayout.SOUTH);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        listRooms.setModel(availableRooms);
        listRooms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listRoomsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(listRooms);

        matchSelectorPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(matchSelectorPanel, java.awt.BorderLayout.EAST);

        chatAndSettingsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        chatAndSettingsPanel.setMinimumSize(new java.awt.Dimension(800, 150));
        chatAndSettingsPanel.setPreferredSize(new java.awt.Dimension(800, 150));
        chatAndSettingsPanel.setLayout(new java.awt.BorderLayout());

        settingsButtonPanel.setMinimumSize(new java.awt.Dimension(150, 100));
        settingsButtonPanel.setPreferredSize(new java.awt.Dimension(150, 146));
        settingsButtonPanel.setLayout(new java.awt.BorderLayout());
        chatAndSettingsPanel.add(settingsButtonPanel, java.awt.BorderLayout.EAST);

        chatPanel.setLayout(new java.awt.BorderLayout());

        textFieldChat.setMinimumSize(new java.awt.Dimension(6, 25));
        textFieldChat.setPreferredSize(new java.awt.Dimension(59, 25));
        textFieldChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldChatActionPerformed(evt);
            }
        });
        chatPanel.add(textFieldChat, java.awt.BorderLayout.SOUTH);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        textAreaChatLog.setEditable(false);
        textAreaChatLog.setColumns(20);
        textAreaChatLog.setLineWrap(true);
        textAreaChatLog.setRows(5);
        jScrollPane2.setViewportView(textAreaChatLog);

        jPanel9.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setMinimumSize(new java.awt.Dimension(150, 24));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(150, 4));

        listUsers.setModel(availableUsers);
        jScrollPane3.setViewportView(listUsers);

        jPanel9.add(jScrollPane3, java.awt.BorderLayout.EAST);

        chatPanel.add(jPanel9, java.awt.BorderLayout.CENTER);

        chatAndSettingsPanel.add(chatPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(chatAndSettingsPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            sfClient.disconnect();
        }
        finally {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void textFieldChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldChatActionPerformed
        sendMessage();
    }//GEN-LAST:event_textFieldChatActionPerformed

    private void buttonCreateGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCreateGameActionPerformed
        createGameRoom();
    }//GEN-LAST:event_buttonCreateGameActionPerformed

    private void listRoomsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listRoomsMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            String roomName = (String)listRooms.getSelectedValue();
            
            if (roomName != null) {
                sfClient.send(new JoinRoomRequest(roomName, null, joinedRoom.getId()));
            }
        }
    }//GEN-LAST:event_listRoomsMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCreateGame;
    private javax.swing.JPanel chatAndSettingsPanel;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblPing;
    private javax.swing.JList listRooms;
    private javax.swing.JList listUsers;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel matchSelectorButtonPanel;
    private javax.swing.JPanel matchSelectorPanel;
    private javax.swing.JPanel settingsButtonPanel;
    private javax.swing.JTextArea textAreaChatLog;
    private javax.swing.JTextField textFieldChat;
    // End of variables declaration//GEN-END:variables
}

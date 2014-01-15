package de.htw.saarland.gamedev.nap.game;

import java.util.List;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public interface ISendPacket {
	/**
	 * Sends the Packet to the recipients.
	 * 
	 * @param opcode the opcode to send
	 * @param params the data to send
	 * @param recipients the recipients to whom to send
	 */
	public void sendServerPacket(String opcode, ISFSObject params, List<User> recipients);
	/**
	 * Sends the Packet to the recipients.
	 * 
	 * @param opcode the opcode to send
	 * @param params the data to send
	 * @param recipients the recipients to whom to send
	 */
	public void sendServerPacketUDP(String opcode, ISFSObject params);
	/**
	 * Send a Packet to the recipient
	 * 
	 * @param opcode the opcode to send
	 * @param params the data to send
	 * @param recipient the recipient to send to
	 */
	public void sendServerPacket(String opcode, ISFSObject params, User recipient);
	/**
	 * Send a packet to all players in the game
	 * 
	 * @param opcode the opcode to send
	 * @param params the data to send
	 */
	public void sendServerPacket(String opcode, ISFSObject params);
}

package network.connection;

import core.ServerConfiguration;
import game.player.Player;
import network.login.CodecFactory;
import network.packet.Packet;
import network.packet.PacketHandler;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import utility.Misc;

public class ConnectionHandler implements IoHandler {

	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {
	}

	@Override
	public void messageReceived(IoSession arg0, Object arg1) throws Exception {
		if (arg0.getAttachment() != null) {
			Player player = (Player) arg0.getAttachment();

			Packet packet = (Packet) arg1;
			if (ServerConfiguration.INSTANT_SWITCHING) {
				if (packet.getId() == 41) {
					player.packetType = packet.getId();
					player.packetSize = packet.getLength();
					player.inStream.currentOffset = 0;
					player.inStream.buffer = packet.getData();
					player.setTimeOutCounter(0);
					if (PacketHandler.showIndividualPackets) {
						Misc.print("Player is active: " + player.getPlayerName() + ", Packet type: " + player.packetType);
					}
					PacketHandler.processPacket(player, player.packetType, player.packetSize);
				} else {
					player.queueMessage((Packet) arg1);
				}
			} else {
				player.queueMessage((Packet) arg1);
			}
		}
	}



	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
	}

	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		if (arg0.getAttachment() != null) {
			Player plr = (Player) arg0.getAttachment();
			plr.setDisconnected(true, "sessionClosed");
		}
		HostList.getHostList().remove(arg0);
	}

	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
		if (!HostList.getHostList().add(arg0)) {
			arg0.close();
		} else {
			arg0.setAttribute("inList", Boolean.TRUE);
		}
	}

	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		arg0.close();
	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
		arg0.setIdleTime(IdleStatus.BOTH_IDLE, 60);
		arg0.getFilterChain().addLast("protocolFilter", new ProtocolCodecFilter(new CodecFactory()));
	}

}

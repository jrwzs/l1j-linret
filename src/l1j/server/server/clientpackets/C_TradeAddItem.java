/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be trading_partnerful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.server.Account;
import l1j.server.server.model.L1World;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.ClientThread;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_TradeAddItem extends ClientBasePacket {
	private static final String C_TRADE_ADD_ITEM = "[C] C_TradeAddItem";
	private static Logger _log = Logger.getLogger(C_TradeAddItem.class
			.getName());

	private void broadcastToAll(String s) {

		L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(s));

	}

	public C_TradeAddItem(byte abyte0[], ClientThread client)
			throws Exception {
		super(abyte0);

		int itemid = readD();
		int itemcount = readD();
		L1PcInstance pc = client.getActiveChar();
		if (itemcount < 0)
		{
			Account.ban(pc.getAccountName());
			IpTable.getInstance().banIp(pc.getNetConnection().getIp());

			pc.sendPackets(new S_Disconnect());
			System.out.println("* * * Banned " + pc.getName() + "for dupe exploit (C_TradeAddItem)* * *");
			broadcastToAll("Roses are red.  Violents are blue");
			broadcastToAll("Fok with my server and I KEEL U");
			broadcastToAll(pc.getName() + " is banned. Bye bye!");
			_log.info(pc.getName() + " attempted dupe exploit (C_TradeAddItem).");
			
			return;
		}
		L1Trade trade = new L1Trade();
		L1ItemInstance item = pc.getInventory().getItem(itemid);
		if (!item.getItem().isTradable()) {
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0??????????????????????????????????????l??????????????????????????????????????????????B
			return;
		}
		if (item.getBless() >= 128) { // ????????????????????????????
			// \f1%0??????????????????????????????????????l??????????????????????????????????????????????B
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
			return;
		}
		Object[] petlist = pc.getPetList().values().toArray();
		for (Object petObject : petlist) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				if (item.getId() == pet.getItemObjId()) {
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName()));
					return;
				}
			}
		}
		L1PcInstance tradingPartner = (L1PcInstance) L1World.getInstance()
				.findObject(pc.getTradeID());
		if (tradingPartner == null) {
			return;
		}
		if (pc.getTradeOk()) {
			return;
		}
		if (tradingPartner.getInventory().checkAddItem(item, itemcount)
				!= L1Inventory.OK) { // 
			tradingPartner.sendPackets(new S_ServerMessage(270)); // 
			pc.sendPackets(new S_ServerMessage(271)); // 
			return;
		}

		trade.TradeAddItem(pc, itemid, itemcount);
	}

	@Override
	public String getType() {
		return C_TRADE_ADD_ITEM;
	}
}

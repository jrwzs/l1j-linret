/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
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
package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import static l1j.server.server.model.skill.L1SkillId.*;

public class S_SPMR extends ServerBasePacket {

	private static final String S_SPMR = "[S] S_S_SPMR";

	private byte[] _byte = null;

	public S_SPMR(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_SPMR);
		// EBY_|[VÌSPÍS_SkillBraveMÉXV³êé½ß·µø¢Ä¨­
		if (pc.hasSkillEffect(STATUS_WISDOM_POTION)) {
			writeC(pc.getSp() - pc.getTrueSp() - 2); // õÁµ½SP
		} else {
			writeC(pc.getSp() - pc.getTrueSp()); // SP increase in equipment
		}
		writeC(pc.getTrueMr() - pc.getBaseMr()); // MR equipment and an increase of magic
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_SPMR;
	}
}

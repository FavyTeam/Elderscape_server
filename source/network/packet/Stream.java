package network.packet;

import core.ServerConstants;
import network.sql.batch.impl.LongStringBatchUpdateEvent;
import network.sql.table.impl.LongStringSQLTableEntry;
import utility.ISAACRandomGen;
import utility.Misc;

public class Stream {

	/**
	 * If this member is true, Strings cannot be written with a length greater than or
	 * equal to 250 (maybe upwards of 253 potentially) to ensure it can be read as an
	 * unsigned byte.
	 */
	private static final boolean STRING_LIMITED = false;

	public Stream() {
	}

	public Stream(byte abyte0[]) {
		buffer = abyte0;
		currentOffset = 0;
	}

	public void createPacket(int id) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) (id + packetEncryption.getNextKey());
	}

	public void createPacketReservedWord(int id) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) (id + packetEncryption.getNextKey());
		writeWord(0);
		if (frameStackPtr >= frameStackSize - 1) {
		} else {
			frameStack[++frameStackPtr] = currentOffset;
		}
	}

	public void endPacketReservedWord() {
		if (frameStackPtr < 0) {
		} else {
			writeFrameSizeWord(currentOffset - frameStack[frameStackPtr--]);
		}
	}

	public byte readSignedByteA() {
		return (byte) (buffer[currentOffset++] - 128);
	}

	public byte readSignedByteC() {
		return (byte) (-buffer[currentOffset++]);
	}

	public byte readSignedByteS() {
		return (byte) (128 - buffer[currentOffset++]);
	}

	public int readUnsignedByteA() {
		return buffer[currentOffset++] - 128 & 0xff;
	}

	public int readUnsignedByteC() {
		return -buffer[currentOffset++] & 0xff;
	}

	public int readUnsignedByteS() {
		try {
			int amount = currentOffset++;
			if (amount > buffer.length - 1 || amount < 0) {
				return 0;
			}
			return 128 - buffer[amount] & 0xff;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void writeByteA(int i) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) (i + 128);
	}

	public void writeByteS(int i) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) (128 - i);
	}

	public void writeByteC(int i) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) (-i);
	}

	public int readSignedWordBigEndian() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readSignedWordA() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] - 128 & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readSignedWordBigEndianA() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] - 128 & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readUnsignedWordBigEndian() {
		currentOffset += 2;
		return ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] & 0xff);
	}

	public int readUnsignedWordA() {
		currentOffset += 2;
		return ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] - 128 & 0xff);
	}

	public int readUnsignedWordBigEndianA() {
		currentOffset += 2;
		return ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] - 128 & 0xff);
	}

	public void writeWordBigEndianA(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) (i + 128);
		buffer[currentOffset++] = (byte) (i >> 8);
	}

	public void writeWordA(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) (i + 128);
	}

	public void writeWordBigEndian_dup(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 8);
	}

	public int readDWord_v1() {
		currentOffset += 4;
		return ((buffer[currentOffset - 2] & 0xff) << 24) + ((buffer[currentOffset - 1] & 0xff) << 16) + ((buffer[currentOffset - 4] & 0xff) << 8) + (buffer[currentOffset - 3]
		                                                                                                                                              & 0xff);
	}

	public int readDWord_v2() {
		currentOffset += 4;
		return ((buffer[currentOffset - 3] & 0xff) << 24) + ((buffer[currentOffset - 4] & 0xff) << 16) + ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2]
		                                                                                                                                              & 0xff);
	}

	public void writeDWord_v1(int i) {
		ensureCapacity(4);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 24);
		buffer[currentOffset++] = (byte) (i >> 16);
	}

	public void writeDWord_v2(int i) {
		ensureCapacity(4);
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 24);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 8);
	}

	public void readBytes_reverse(byte abyte0[], int i, int j) {
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = buffer[currentOffset++];

	}

	public void writeBytes_reverse(byte abyte0[], int i, int j) {
		ensureCapacity(i);
		for (int k = (j + i) - 1; k >= j; k--)
			buffer[currentOffset++] = abyte0[k];

	}

	public void readBytes_reverseA(byte abyte0[], int i, int j) {
		ensureCapacity(i);
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = (byte) (buffer[currentOffset++] - 128);

	}

	public void writeBytes_reverseA(byte abyte0[], int i, int j) {
		ensureCapacity(i);
		for (int k = (j + i) - 1; k >= j; k--)
			buffer[currentOffset++] = (byte) (abyte0[k] + 128);

	}

	public void createFrame(int id) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) (id + packetEncryption.getNextKey());
	}

	private static final int frameStackSize = 10;

	private int frameStackPtr = -1;

	private int frameStack[] = new int[frameStackSize];

	public void createFrameVarSize(int id) {
		ensureCapacity(3);
		buffer[currentOffset++] = (byte) (id + packetEncryption.getNextKey());
		buffer[currentOffset++] = 0;
		if (frameStackPtr >= frameStackSize - 1) {
		} else {
			frameStack[++frameStackPtr] = currentOffset;
		}
	}

	public void createFrameVarSizeWord(int id) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) (id + packetEncryption.getNextKey());
		writeWord(0);
		if (frameStackPtr >= frameStackSize - 1) {
		} else {
			frameStack[++frameStackPtr] = currentOffset;
		}
	}

	public void endFrameVarSize() {
		if (frameStackPtr < 0) {

		} else {
			writeFrameSize(currentOffset - frameStack[frameStackPtr--]);
		}
	}

	public void endFrameVarSizeWord() {
		if (frameStackPtr < 0) {

		} else {
			writeFrameSizeWord(currentOffset - frameStack[frameStackPtr--]);
		}
	}

	public void writeByte(int i) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) i;
	}

	public void writeWord(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
	}

	public void writeWordBigEndian(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 8);
	}

	public void write3Byte(int i) {
		ensureCapacity(3);
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
	}

	public void writeDWord(int i) {
		ensureCapacity(4);
		buffer[currentOffset++] = (byte) (i >> 24);
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
	}

	public void writeDWordBigEndian(int i) {
		ensureCapacity(4);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 24);
	}

	public void writeQWord(long l) {
		ensureCapacity(8);
		buffer[currentOffset++] = (byte) (int) (l >> 56);
		buffer[currentOffset++] = (byte) (int) (l >> 48);
		buffer[currentOffset++] = (byte) (int) (l >> 40);
		buffer[currentOffset++] = (byte) (int) (l >> 32);
		buffer[currentOffset++] = (byte) (int) (l >> 24);
		buffer[currentOffset++] = (byte) (int) (l >> 16);
		buffer[currentOffset++] = (byte) (int) (l >> 8);
		buffer[currentOffset++] = (byte) (int) l;
	}

	public void writeString(java.lang.String s) {
		if (STRING_LIMITED && s.length() >= 250) {
			s = s.substring(0, 250);
			LongStringBatchUpdateEvent.getInstance().submit(new LongStringSQLTableEntry(s.length(), s));
		}
		ensureCapacity(s.length());
		System.arraycopy(s.getBytes(), 0, buffer, currentOffset, s.length());
		currentOffset += s.length();
		buffer[currentOffset++] = 10;
	}

	public void writeBytes(byte abyte0[], int i, int j) {
		ensureCapacity(i);
		for (int k = j; k < j + i; k++)
			buffer[currentOffset++] = abyte0[k];
	}

	public void writeFrameSize(int i) {
		buffer[currentOffset - i - 1] = (byte) i;
	}

	public void writeFrameSizeWord(int i) {
		buffer[currentOffset - i - 2] = (byte) (i >> 8);
		buffer[currentOffset - i - 1] = (byte) i;
	}

	public int readUnsignedByte() {
		return buffer[currentOffset++] & 0xff;
	}

	public byte readSignedByte() {
		return buffer[currentOffset++];
	}

	public int readUnsignedWord() {
		currentOffset += 2;
		return ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
	}

	public int readSignedWord() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readDWord() {
		currentOffset += 4;
		int value1 = currentOffset - 4;
		int value2 = currentOffset - 3;
		int value3 = currentOffset - 2;
		int value4 = currentOffset - 1;
		if (isNulledInteger(value1, buffer.length - 1)) {
			return 0;
		}
		if (isNulledInteger(value2, buffer.length - 1)) {
			return 0;
		}
		if (isNulledInteger(value3, buffer.length - 1)) {
			return 0;
		}
		if (isNulledInteger(value4, buffer.length - 1)) {
			return 0;
		}
		return ((buffer[value1] & 0xff) << 24) + ((buffer[value2] & 0xff) << 16) + ((buffer[value3] & 0xff) << 8) + (buffer[value4] & 0xff);
	}
	public long readQWord() {
		try {
			long l = (long) readDWord() & 0xffffffffL;
			long l1 = (long) readDWord() & 0xffffffffL;
			return (l << 32) + l1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public java.lang.String readString() {
		try {
			int i = currentOffset;
			while (buffer[currentOffset++] != 10)
				;
			return new String(buffer, i, currentOffset - i - 1);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private boolean isNulledInteger(int value, int size) {
		if (value < 0 || value > size) {
			return true;
		}
		return false;
	}

	public void readBytes(byte abyte0[], int i, int j) {
		try {
			for (int k = j; k < j + i; k++) {
				int value = currentOffset++;
				if (isNulledInteger(value, buffer.length - 1)) {
					continue;
				}
				abyte0[k] = buffer[value];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void initBitAccess() {
		bitPosition = currentOffset * 8;
	}

	public void writeBits(int numBits, int value) {
		ensureCapacity(((int) Math.ceil(numBits * 8)) * 4);
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;

		for (; numBits > bitOffset; bitOffset = 8) {
			buffer[bytePos] &= ~bitMaskOut[bitOffset];
			buffer[bytePos++] |= (value >> (numBits - bitOffset)) & bitMaskOut[bitOffset];

			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			buffer[bytePos] &= ~bitMaskOut[bitOffset];
			buffer[bytePos] |= value & bitMaskOut[bitOffset];
		} else {
			buffer[bytePos] &= ~(bitMaskOut[numBits] << (bitOffset - numBits));
			buffer[bytePos] |= (value & bitMaskOut[numBits]) << (bitOffset - numBits);
		}
	}

	public void finishBitAccess() {
		currentOffset = (bitPosition + 7) / 8;
	}

	public byte buffer[] = null;

	public int currentOffset = 0;

	public int bitPosition = 0;

	public static int bitMaskOut[] = new int[32];

	static {
		for (int i = 0; i < 32; i++)
			bitMaskOut[i] = (1 << i) - 1;
	}

	public void ensureCapacity(int len) {
		try {
			int minimumLength = currentOffset + len + 1;

			if ((minimumLength) >= buffer.length) {
				byte[] oldBuffer = buffer;

				int newLength = (buffer.length * 2);

				if (newLength < minimumLength) {
					newLength = minimumLength + (buffer.length / 2);
				}
				buffer = new byte[newLength];

				System.arraycopy(oldBuffer, 0, buffer, 0, oldBuffer.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		if (!(currentOffset > ServerConstants.BUFFER_SIZE)) {
			byte[] oldBuffer = buffer;
			buffer = new byte[ServerConstants.BUFFER_SIZE];
			System.arraycopy(oldBuffer, 0, buffer, 0, currentOffset);
		}
	}

	public ISAACRandomGen packetEncryption = null;

}

package com.aizi.yingerbao.utility;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Little-Endian DataOutputStream.
 */
public class LittleEndianDataOutputStream {

    /** 封装的 DataOutputStream。 */
    private DataOutputStream mDos;
    /** util buffer. */
    byte[] buff;

    /**
     * constructor.
     * 
     * @param out
     *            OutputStream
     */
    public LittleEndianDataOutputStream(OutputStream out) {
        mDos = new DataOutputStream(out);
        buff = new byte[8]; // SUPPRESS CHECKSTYLE
    }

    /**
     * Writes the specified 16-bit short to the target stream. Only the lower
     * two bytes of the integer {@code val} are written, with the little one
     * written first.
     * 
     * @param val
     *            the short to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readShort()
     * @see DataInputStream#readUnsignedShort()
     */
    public final void writeShort(int val) throws IOException {
        buff[1] = (byte) (val >> 8); // SUPPRESS CHECKSTYLE
        buff[0] = (byte) val;
        mDos.write(buff, 0, 2);
    }

    /**
     * Writes a 32-bit int to the target stream. The resulting output is the
     * four bytes, lowest order first, of {@code val}.
     * 
     * @param val
     *            the int to write to the target stream.
     * @throws IOException
     *             if an error occurs while writing to the target stream.
     * @see DataInputStream#readInt()
     */
    public final void writeInt(int val) throws IOException {
        buff[3] = (byte) (val >> 24); // SUPPRESS CHECKSTYLE
        buff[2] = (byte) (val >> 16); // SUPPRESS CHECKSTYLE
        buff[1] = (byte) (val >> 8); // SUPPRESS CHECKSTYLE
        buff[0] = (byte) val;
        mDos.write(buff, 0, 4); // SUPPRESS CHECKSTYLE
    }

    /**
     * CLOSE 对应的输出流。
     * 
     * @throws IOException
     *             IOException
     */
    public void close() throws IOException {
        mDos.close();
    }

    /**
     * 输出字节数组。
     * 
     * @param buffer
     *            buffer
     * @throws IOException
     *             IOException
     */
    public void write(byte[] buffer) throws IOException {
        mDos.write(buffer);
    }

    /**
     * 输出一个little endian 的 long
     * 
     * @param val
     *            value
     * @throws IOException
     *             IOException
     */
    public final void writeLong(long val) throws IOException {
        buff[7] = (byte) (val >> 56); // SUPPRESS CHECKSTYLE
        buff[6] = (byte) (val >> 48); // SUPPRESS CHECKSTYLE
        buff[5] = (byte) (val >> 40); // SUPPRESS CHECKSTYLE
        buff[4] = (byte) (val >> 32); // SUPPRESS CHECKSTYLE
        buff[3] = (byte) (val >> 24); // SUPPRESS CHECKSTYLE
        buff[2] = (byte) (val >> 16); // SUPPRESS CHECKSTYLE
        buff[1] = (byte) (val >> 8); // SUPPRESS CHECKSTYLE
        buff[0] = (byte) val;
        mDos.write(buff, 0, 8); // SUPPRESS CHECKSTYLE
    }
}

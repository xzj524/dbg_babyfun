package com.aizi.yingerbao.utility;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Little-Endian DataIutputStream
 */
public class LittleEndianDataIutputStream {

    /** 封装的 DataInputStream 。 */
    private DataInputStream mDis;

    /** util buffer. */
    byte[] buff;

    /**
     * constructor。
     * 
     * @param in
     *            InputStream
     */
    public LittleEndianDataIutputStream(InputStream in) {
        mDis = new DataInputStream(in);
        buff = new byte[8]; // SUPPRESS CHECKSTYLE
    }

    /**
     * 关闭对应的输入流。
     * 
     * @throws IOException
     *             IOException
     */
    public void close() throws IOException {
        mDis.close();
    }

    /**
     * 读取多少个字节到 缓存 buff
     * 
     * @param count
     *            要读取的大小
     * @return 返回读取的字节个数
     * @throws IOException
     *             IOException
     */
    private int readToBuff(int count) throws IOException {
        int offset = 0;

        while (offset < count) {
            int bytesRead = mDis.read(buff, offset, count - offset);
            if (bytesRead == -1) {
                return bytesRead;
            }
            offset += bytesRead;
        }
        return offset;
    }

    /**
     * 从 little endian 字节序中读取 int。
     * 
     * @return int
     * @throws IOException
     *             IOException
     */
    public final int readInt() throws IOException {
        if (readToBuff(4) < 0) { // SUPPRESS CHECKSTYLE
            throw new EOFException();
        }
        return ((buff[3] & 0xff) << 24) | ((buff[2] & 0xff) << 16)
                | ((buff[1] & 0xff) << 8) | (buff[0] & 0xff); // SUPPRESS
                                                              // CHECKSTYLE
    }

    /**
     * 从 little endian 字节序中读取 Short。
     * 
     * @return Short
     * @throws IOException
     *             IOException
     */
    public final short readShort() throws IOException {
        if (readToBuff(2) < 0) {
            throw new EOFException();
        }
        return (short) (((buff[1] & 0xff) << 8) | (buff[0] & 0xff)); // SUPPRESS
                                                                     // CHECKSTYLE
    }

    /**
     * 从 little endian 字节序中读取 long。
     * 
     * @return long
     * @throws IOException
     *             IOException
     */
    public final long readLong() throws IOException {
        if (readToBuff(8) < 0) { // SUPPRESS CHECKSTYLE
            throw new EOFException();
        }
        int i1 = ((buff[7] & 0xff) << 24) | ((buff[6] & 0xff) << 16)
                | ((buff[5] & 0xff) << 8) | (buff[4] & 0xff); // SUPPRESS
                                                              // CHECKSTYLE
        int i2 = ((buff[3] & 0xff) << 24) | ((buff[2] & 0xff) << 16)
                | ((buff[1] & 0xff) << 8) | (buff[0] & 0xff); // SUPPRESS
                                                              // CHECKSTYLE

        return ((i1 & 0xffffffffL) << 32) | (i2 & 0xffffffffL); // SUPPRESS
                                                                // CHECKSTYLE
    }


    /**
     * Reads bytes from this stream into the byte array {@code buffer}. This
     * method will block until {@code buffer.length} number of bytes have been
     * read.
     * 
     * @param buffer
     *            to read bytes into.
     * @throws IOException
     *             if a problem occurs while reading from this stream.
     * @see DataOutput#write(byte[])
     * @see DataOutput#write(byte[], int, int)
     */
    public final void readFully(byte[] buffer) throws IOException {
        mDis.readFully(buffer, 0, buffer.length);
    }


}

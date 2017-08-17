package com.visionvera.bmob.activity.plan.camera.opengl;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

/**
 * A data input stream lets an application read primitive Java data types from
 * an underlying input stream in a machine-independent way. An application uses
 * a data output stream to write data that can later be read by a data input
 * stream.
 * <p>
 * LittleEndianDataInputStream is not necessarily safe for multithreaded access.
 * Thread safety is optional and is the responsibility of users of methods in
 * this class.
 * <p>
 * This stream works in a little-endian manner. Java is by design big-endian.
 * This can be inconvenient if working on a little-endian platform (like on a
 * PC), and working with binary files that need to be compatible with a non-Java
 * application.
 *
 * @author Eric Olson, eaolson@alumni.rice.edu
 * @version 1.0
 * @see DataInput
 * @see DataInputStream
 */
public class LittleEndianDataInputStream extends FilterInputStream implements DataInput {
    public static boolean _D = false;

    /**
     * Creates a LittleEndianDataInputStream that uses the specified underlying
     * InputStream.
     *
     * @param in the specified input stream
     */
    public LittleEndianDataInputStream(InputStream in) {
        super(in);
    }

    /**
     * See the general contract of the <code>readBoolean</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the <code>boolean</code> value read.
     * @throws EOFException if this input stream has reached the end.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public boolean readBoolean() throws IOException {
        int b = read();
        // if (b == -1) {
        // throw new EOFException();
        // }
        return (b != 0);
    }

    /**
     * See the general contract of the <code>readByte</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next byte of this input stream as a signed 8-bit
     * <code>byte</code>.
     * @throws EOFException if this input stream has reached the end.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public byte readByte() throws IOException {
        int b = read();
        // if (b == -1) {
        // throw new EOFException();
        // }
        return (byte) b;
    }

    /**
     * See the general contract of the <code>readChar</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next two bytes of this input stream, interpreted as a
     * <code>char</code>.
     * @throws EOFException if this input stream reaches the end before reading two
     *                      bytes.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public char readChar() throws IOException {
        int b0, b1;
        b0 = read();
        b1 = read();
        // if ((b0 == -1) || (b1 == -1)) {
        // throw new EOFException();
        // }
        return (char) (b0 | (b1 << 8));
    }

    /**
     * See the general contract of the <code>readFully</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @param b the buffer into which the data is read.
     * @throws EOFException if this input stream reaches the end before reading all
     *                      the bytes.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public void readFully(byte[] b) throws IOException {

        readFully(b, 0, b.length);

    }

    public byte[] readFully(int len) throws IOException {
        byte[] buffer = new byte[len];
        int lr_len = 0;
        while (true) {
            int cr_len = this.read(buffer, lr_len, len - lr_len);// 接收数据
            if (cr_len + lr_len == len) {// 是否已经接收到指定的长度
                break;
            } else {
                lr_len = lr_len + cr_len;// 如果未接收到找度的长度则设置偏移量
            }
        }
        return buffer;
    }

    /**
     * See the general contract of the <code>readFully</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the number of bytes to read.
     * @throws EOFException if this input stream reaches the end before reading all
     *                      the bytes.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        if (_D) {
            if (len < 0)
                throw new IndexOutOfBoundsException();
            int n = 0;
            while (n < len) {
                int count = in.read(b, off + n, len - n);
                if (count < 0)
                    throw new EOFException();
                n += count;
            }
        } else {

            byte[] buffer = b;
            int lr_len = off;
            int cr_len = 0;
            while (true) {
                cr_len = this.read(buffer, lr_len, len - lr_len);// 接收数据
                if (cr_len == -1) {
                    b = null;
                }
                if (cr_len + lr_len == len) {// 是否已经接收到指定的长度
                    break;
                } else {
                    lr_len = lr_len + cr_len;// 如果未接收到找度的长度则设置偏移量
                }
            }
        }

        // int tmp;
        // for (int i=0; i < len; i++) {
        // tmp = read();
        // if (tmp == -1) {
        // throw new EOFException();
        // }
        // b[off + i] = (byte) tmp;
        // }
    }

    /**
     * See the general contract of the <code>readInt</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next four bytes of this input stream, interpreted as an
     * <code>int</code>.
     * @throws EOFException if this input stream reaches the end before reading four
     *                      bytes.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public int readInt() throws IOException {
        int b0, b1, b2, b3;
        b0 = read();
        b1 = read();
        b2 = read();
        b3 = read();
        // if ((b0 == -1) || (b1 == -1) || (b2 == -1) || (b3 == -1)) {
        // throw new EOFException();
        // }
        return (b0 | (b1 << 8) | (b2 << 16) | (b3 << 24));
    }

    /**
     * Reads the next line of text from the input stream. It reads successive
     * bytes, converting each byte separately into a character, until it
     * encounters a line terminator or end of file; the characters read are then
     * returned as a <code>String</code>. Note that because this method
     * processes bytes, it does not support input of the full Unicode character
     * set.
     * <p>
     * If end of file is encountered before even one byte can be read, then
     * <code>null</code> is returned. Otherwise, each byte that is read is
     * converted to type <code>char</code> by zero-extension. If the character
     * <code>'\n'</code> is encountered, it is discarded and reading ceases. If
     * the character <code>'\r'</code> is encountered, it is discarded and, if
     * the following byte converts &#32;to the character <code>'\n'</code>, then
     * that is discarded also; reading then ceases. If end of file is
     * encountered before either of the characters <code>'\n'</code> and
     * <code>'\r'</code> is encountered, reading ceases. Once reading has
     * ceased, a <code>String</code> is returned that contains all the
     * characters read and not discarded, taken in order. Note that every
     * character in this string will have a value less than
     * <code>&#92;u0100</code>, that is, <code>(char)256</code>.
     *
     * @return the next line of text from the input stream, or <CODE>null</CODE>
     * if the end of file is encountered before a byte can be read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public String readLine() throws IOException {
        StringBuilder strb = new StringBuilder();
        int b = 0;
        while (b != -1) {
            b = read();
            if ((b != -1) && (b != '\n') && (b != '\r')) {
                strb.append((char) b);
            } else if ((b != '\n')) {
                b = -1;
            }
        }
        if (strb.length() == 0) {
            return null;
        } else {
            return strb.toString();
        }
    }

    /**
     * See the general contract of the <code>readLong</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next eight bytes of this input stream, interpreted as a
     * <code>long</code>.
     * @throws EOFException if this input stream reaches the end before reading eight
     *                      bytes.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public long readLong() throws IOException {
        long b0, b1, b2, b3, b4, b5, b6, b7;
        b0 = read();
        b1 = read();
        b2 = read();
        b3 = read();
        b4 = read();
        b5 = read();
        b6 = read();
        b7 = read();
        // if ((b0 == -1) || (b1 == -1) || (b2 == -1) || (b3 == -1) || (b4 ==
        // -1) || (b5 == -1) || (b6 == -1) || (b7 == -1)) {
        // throw new EOFException();
        // }
        return (b0 | (b1 << 8) | (b2 << 16) | (b3 << 24) | (b4 << 32) | (b5 << 40) | (b6 << 48) | (b7 << 56));
    }

    /**
     * See the general contract of the <code>readShort</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next two bytes of this input stream, interpreted as a signed
     * 16-bit number.
     * @throws EOFException if this input stream reaches the end before reading two
     *                      bytes.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public short readShort() throws IOException {
        int b0, b1;
        b0 = read();
        b1 = read();
        // if ((b0 == -1) || (b1 == -1)) {
        // throw new EOFException();
        // }
        return (short) (b0 | (b1 << 8));
    }

    /**
     * See the general contract of the <code>skipBytes</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @throws IOException if the stream has been closed and the contained input
     *                     stream does not support reading after close, or another
     *                     I/O error occurs.
     */
    @Override
    public int skipBytes(int n) throws IOException {
        int i = 0;
        int b = 0;
        while ((i < n) && (b != -1)) {
            b = read();
            i++;
        }
        return i;
    }

    /**
     * See the general contract of the <code>readUnsignedByte</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next byte of this input stream, interpreted as an unsigned
     * 8-bit number.
     * @throws EOFException if this input stream has reached the end.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public int readUnsignedByte() throws IOException {
        int b = read();
        // if (b == -1) {
        // throw new EOFException();
        // }
        return b;
    }

    /**
     * See the general contract of the <code>readUnsignedShort</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next two bytes of this input stream, interpreted as an
     * unsigned 16-bit integer.
     * @throws EOFException if this input stream reaches the end before reading two
     *                      bytes.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see FilterInputStream#in
     */
    @Override
    public int readUnsignedShort() throws IOException {
        int b0, b1;
        b0 = read();
        b1 = read();
        // if ((b0 == -1) || (b1 == -1)) {
        // throw new EOFException();
        // }
        return (b0 | b1 << 8);
    }

    /**
     * See the general contract of the <code>readDouble</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next eight bytes of this input stream, interpreted as a
     * <code>double</code>.
     * @throws EOFException if this input stream reaches the end before reading eight
     *                      bytes.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see LittleEndianDataInputStream#readLong()
     * @see Double#longBitsToDouble(long)
     */
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    /**
     * See the general contract of the <code>readFloat</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next four bytes of this input stream, interpreted as a
     * <code>float</code>.
     * @throws EOFException if this input stream reaches the end before reading four
     *                      bytes.
     * @throws IOException  the stream has been closed and the contained input stream
     *                      does not support reading after close, or another I/O error
     *                      occurs.
     * @see LittleEndianDataInputStream#readInt()
     * @see Float#intBitsToFloat(int)
     */
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * Not implemented as the modified UTF-8 scheme defined by
     * <code>DataInput</code> does not make sense in a little-endian format.
     * <p>
     * This method always throws a <code>UTFDataFormatException</code>.
     *
     * @return always returns a null <code>String</code>
     * @throws UTFDataFormatException always
     * @see DataInput#readUTF()
     */
    @Override
    public String readUTF() throws IOException {
        throw new UTFDataFormatException();
    }

    @Override
    public void close() {

    }
} // end class LittleEndianDataInputStream

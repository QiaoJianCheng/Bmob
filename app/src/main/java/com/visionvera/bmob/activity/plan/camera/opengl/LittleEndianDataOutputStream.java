package com.visionvera.bmob.activity.plan.camera.opengl;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/**
 * A data output stream lets an application write primitive Java data types to
 * an output stream in a portable way. An application can then use a data input
 * stream to read the data back in.
 * <p>
 * This stream works in a little-endian manner. Java is by design big-endian.
 * This can be inconvenient if working on a little-endian platform (like on a
 * PC), and working with binary files that need to be compatible with a
 * non-Java application.
 *
 * @author Eric Olson, eaolson@alumni.rice.edu
 * @version 1.0
 * @see DataOutput
 * @see DataOutputStream
 */
public class LittleEndianDataOutputStream extends FilterOutputStream
        implements DataOutput {
    /**
     * The number of bytes written to the data output stream so far.
     * If this counter overflows, it will be wrapped to Integer.MAX_VALUE.
     */
    protected int written = 0;

    /**
     * Creates a new little-endian data output stream to write data to the
     * specified underlying output stream. The counter <code>written</code> is
     * set to zero.
     *
     * @param out the underlying output stream, to be saved for later
     *            use.
     * @see FilterOutputStream#out
     */
    public LittleEndianDataOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Returns the current value of the counter written, the number of bytes
     * written to this data output stream so far. If the counter overflows,
     * it will be wrapped to Integer.MAX_VALUE.
     *
     * @return the value of the <code>written</code> field.
     */
    public int size() {
        return written;
    }

    /**
     * Writes the specified byte (the low eight bits of the argument
     * <code>b</code>) to the underlying output stream. If no exception
     * is thrown, the counter <code>written</code> is incremented by
     * <code>1</code>.
     * <p>
     * Implements the <code>write</code> method of <code>OutputStream</code>.
     *
     * @param b the <code>byte</code> to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     */
    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        incCount(b.length);
        return;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to the underlying output stream.
     * If no exception is thrown, the counter <code>written</code> is
     * incremented by <code>len</code>.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        incCount(len);
        return;
    }

    /**
     * Writes the specified byte (the low eight bits of the argument b) to the
     * underlying output stream. If no exception is thrown, the counter written
     * is incremented by 1.
     *
     * @param b
     * @throws IOException
     */
    @Override
    public void write(int b) throws IOException {
        out.write(b);
        incCount(1);
        return;
    }

    public void write(byte b) throws IOException {
        writeByte(b);
    }

    public void write(short b) throws IOException {
        writeShort(b);
    }

    /**
     * Writes a <code>boolean</code> to the underlying output stream as
     * a 1-byte value. The value <code>true</code> is written out as the
     * value <code>(byte)1</code>; the value <code>false</code> is
     * written out as the value <code>(byte)0</code>. If no exception is
     * thrown, the counter <code>written</code> is incremented by
     * <code>1</code>.
     *
     * @param v a <code>boolean</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     */
    @Override
    public void writeBoolean(boolean v) throws IOException {
        if (v) {
            out.write(1);
        } else {
            out.write(0);
        }
        incCount(1);
    }

    /**
     * Writes out a <code>byte</code> to the underlying output stream as
     * a 1-byte value. If no exception is thrown, the counter
     * <code>written</code> is incremented by <code>1</code>.
     *
     * @param v a <code>byte</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     */
    @Override
    public void writeByte(int v) throws IOException {
        out.write(v);
        incCount(1);
    }

    /**
     * Writes out the string to the underlying output stream as a
     * sequence of bytes. Each character in the string is written out, in
     * sequence, by discarding its high eight bits. If no exception is
     * thrown, the counter <code>written</code> is incremented by the
     * length of <code>s</code>.
     *
     * @param s a string of bytes to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     */
    @Override
    public void writeBytes(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            write(s.charAt(i) & 0xFF);
        }
        incCount(s.length());
        return;
    }

    /**
     * Writes a <code>char</code> to the underlying output stream as a
     * 2-byte value, low byte first. If no exception is thrown, the
     * counter <code>written</code> is incremented by <code>2</code>.
     *
     * @param v a <code>char</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     */
    @Override
    public void writeChar(int v) throws IOException {
        // written will be incremented inside writeInt
        out.write(v >>> 0);
        out.write(v >>> 8);
        incCount(2);
    }

    /**
     * Writes a string to the underlying output stream as a sequence of
     * characters. Each character is written to the data output stream as
     * if by the <code>writeChar</code> method. If no exception is
     * thrown, the counter <code>written</code> is incremented by twice
     * the length of <code>s</code>.
     *
     * @param s a <code>String</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see DataOutputStream#writeChar(int)
     * @see FilterOutputStream#out
     */
    @Override
    public void writeChars(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            // This is a call to this, so don't increment written
            write(s.charAt(i));
        }
    }

    /**
     * Converts the double argument to a <code>long</code> using the
     * <code>doubleToLongBits</code> method in class <code>Double</code>,
     * and then writes that <code>long</code> value to the underlying
     * output stream as an 8-byte quantity, low byte first. If no
     * exception is thrown, the counter <code>written</code> is
     * incremented by <code>8</code>.
     *
     * @param v a <code>double</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     * @see Double#doubleToLongBits(double)
     */
    @Override
    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToRawLongBits(v));
        return;
    }

    /**
     * Writes an <code>int</code> to the underlying output stream as four
     * bytes, low byte first. If no exception is thrown, the counter
     * <code>written</code> is incremented by <code>4</code>.
     *
     * @param v an <code>int</code> to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     */
    @Override
    public void writeInt(int v) throws IOException {
        out.write(v >>> 0);
        out.write(v >>> 8);
        out.write(v >>> 16);
        out.write(v >>> 24);
        incCount(4);
        return;
    }

    /**
     * Converts the float argument to an <code>int</code> using the
     * <code>floatToIntBits</code> method in class <code>Float</code>,
     * and then writes that <code>int</code> value to the underlying
     * output stream as a 4-byte quantity, low byte first. If no
     * exception is thrown, the counter <code>written</code> is
     * incremented by <code>4</code>.
     *
     * @param v a <code>float</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     * @see Float#floatToIntBits(float)
     */
    @Override
    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToRawIntBits(v));
        return;
    }

    /**
     * Writes a <code>long</code> to the underlying output stream as eight
     * bytes, low byte first. In no exception is thrown, the counter
     * <code>written</code> is incremented by <code>8</code>.
     *
     * @param v a <code>long</code> to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     */
    @Override
    public void writeLong(long v) throws IOException {
        out.write((int) (v >>> 0));
        out.write((int) (v >>> 8));
        out.write((int) (v >>> 16));
        out.write((int) (v >>> 24));
        out.write((int) (v >>> 32));
        out.write((int) (v >>> 40));
        out.write((int) (v >>> 48));
        out.write((int) (v >>> 56));
        incCount(8);
    }

    /**
     * Writes a <code>short</code> to the underlying output stream as two
     * bytes, low byte first. If no exception is thrown, the counter
     * <code>written</code> is incremented by <code>2</code>.
     *
     * @param v a <code>short</code> to be written.
     * @throws IOException if an I/O error occurs.
     * @see FilterOutputStream#out
     */
    @Override
    public void writeShort(int v) throws IOException {
        out.write(v >>> 0);
        out.write(v >>> 8);
        incCount(2);
    }

    /**
     * Not implemented as the modified UTF-8 scheme defined by
     * <code>DataOutput</code> does not make sense in a little-endian format.
     * <p>
     * This method always throws a <code>UTFDataFormatException</code>.
     *
     * @throws UTFDataFormatException always
     * @see DataOutput#writeUTF
     */
    @Override
    public void writeUTF(String s) throws IOException {
        throw new UTFDataFormatException();
    }

    /* Increments written, the count of bytes that have been written to the
     * stream. If written overflows, it is set to Integer.MAX_VALUE;
     */
    private void incCount(int length) {
        written += length;
        if (written < 0)
            written = Integer.MAX_VALUE;
        return;
    }

} // end of class LittleEndianDataOutputStream

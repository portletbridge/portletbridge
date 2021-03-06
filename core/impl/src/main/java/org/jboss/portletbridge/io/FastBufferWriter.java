/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.ServletOutputStream;

/**
 * Class for writing to chain of char arrays extending Writer.
 *
 * @author glory
 */
public class FastBufferWriter extends Writer {
    /**
     * The beginning of the chain of char arrays.
     */
    CharBuffer firstBuffer;

    /**
     * Currently filled link of the chain of char arrays.
     */
    CharBuffer lastBuffer;

    /**
     * Total number of written chars.
     */
    int length;

    /**
     * Creates instance of default initial capacity.
     */
    public FastBufferWriter() {
        this(256);
    }

    /**
     * Creates instance with required initial capacity.
     *
     * @param initialSize
     */
    public FastBufferWriter(int initialSize) {
        this(new CharBuffer(initialSize));
    }

    /**
     * Creates instance for an already existing chain of char arrays.
     *
     * @param firstBuffer
     */
    public FastBufferWriter(CharBuffer firstBuffer) {
        this.firstBuffer = firstBuffer;
        lastBuffer = firstBuffer;
    }

    /**
     * @see java.io.Writer.write(int c)
     */
    public void write(int c) throws IOException {
        lastBuffer = lastBuffer.append((char) c);
        length++;
    }

    /**
     * @see java.io.Writer.write(char[] cbuf)
     */
    public void write(char[] cbuf) throws IOException {
        if (cbuf == null) {
            throw new IllegalArgumentException();
        }

        lastBuffer = lastBuffer.append(cbuf, 0, cbuf.length);
        length += cbuf.length;

    }

    /**
     * @see java.io.Writer.write(char cbuf[], int off, int len)
     */
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (cbuf == null) {
            throw new IllegalArgumentException();
        }
        if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        lastBuffer = lastBuffer.append(cbuf, off, len);
        length += len;

    }

    /**
     * Returns the total number of written chars.
     *
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the first link of the chain of char arrays.
     *
     * @return
     */
    public CharBuffer getFirstBuffer() {
        return firstBuffer;
    }

    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }

    /**
     * Writes all data written up to the moment to string buffer.
     *
     * @param out
     * @throws IOException
     */
    public char[] toCharArray() {
        CharBuffer b = firstBuffer;
        if (b == null) {
            return new char[0];
        }
        CharBuffer l = b;
        while (l.getNext() != null) {
            l = l.getNext();
        }
        char[] result = new char[l.getTotalSize()];
        int index = 0;
        while (b != null) {
            int s = b.getUsedSize();
            System.arraycopy(b.getChars(), 0, result, index, s);
            index += s;
            b = b.getNext();
        }
        return result;
    }

    @Override
    public String toString() {
        return new String(toCharArray());
    }

    /**
     * Writes all data written up to the moment to out.
     *
     * @param out
     * @throws IOException
     */
    public void writeTo(Writer writer) throws IOException {
        CharBuffer b = firstBuffer;
        while (b != null) {
            writer.write(b.getChars(), 0, b.getUsedSize());
            b = b.getNext();
        }
    }

    public void printTo(ServletOutputStream outputStream) throws IOException {
        CharBuffer b = firstBuffer;
        while (b != null) {
            outputStream.print(new String(b.getChars()));
            b = b.getNext();
        }
    }

    /**
     * Returns instance of FastBufferOutputStream containing all data written to this writer.
     *
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     */
    public org.jboss.portletbridge.io.FastBufferOutputStream convertToOutputStream(String encoding)
            throws UnsupportedEncodingException {
        CharBuffer c = firstBuffer;
        ByteBuffer first = c.toByteBuffer(encoding);
        ByteBuffer b = first;
        while (c != null) {
            c = c.getNext();
            if (c == null) {
                break;
            }
            ByteBuffer n = c.toByteBuffer(encoding);
            b.setNext(n);
            b = n;
        }
        return new FastBufferOutputStream(first);
    }

    /**
     * Returns instance of FastBufferOutputStream containing all data written to this writer.
     *
     * @return
     */
    public FastBufferOutputStream convertToOutputStream() {
        CharBuffer c = firstBuffer;
        ByteBuffer first = c.toByteBuffer();
        ByteBuffer b = first;
        while (c != null) {
            c = c.getNext();
            if (c == null) {
                break;
            }
            ByteBuffer n = c.toByteBuffer();
            b.setNext(n);
            b = n;
        }
        return new FastBufferOutputStream(first);
    }

    /**
     * Resets writer to empty state
     *
     * @since 3.3.0
     */
    public void reset() {
        this.firstBuffer.reset();
        this.lastBuffer = this.firstBuffer;

        this.length = 0;
    }
}

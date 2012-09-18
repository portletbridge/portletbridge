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
package org.jboss.portletbridge.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UploadedFileImpl implements Serializable, UploadedFile {

    private static final long serialVersionUID = -8722331321934272862L;

    private String absolutePath;
    private Map<String, Object> attributeMap;
    private String charSet;
    private String contentType;
    private String id;
    private Map<String, List<String>> headersMap;
    private String message;
    private String name;
    private long size;

    public UploadedFileImpl(String absolutePath, Map<String, Object> attributeMap, String charSet, String contentType,
            Map<String, List<String>> headersMap, String id, String message, String name, long size) {
        this.absolutePath = absolutePath;
        this.attributeMap = attributeMap;
        this.charSet = charSet;
        this.contentType = contentType;
        this.id = id;
        this.headersMap = headersMap;
        this.message = message;
        this.name = name;
        this.size = size;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public byte[] getData() throws IOException {
        byte[] bytes = null;

        try {
            File file = new File(absolutePath);

            if (file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                bytes = new byte[(int) randomAccessFile.length()];
                randomAccessFile.readFully(bytes);
                randomAccessFile.close();
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(absolutePath);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void delete() throws IOException {
        File file = new File(absolutePath);
        file.delete();
    }

    @Override
    public void write(String fileName) throws IOException {
        OutputStream outputStream = new FileOutputStream(fileName);
        outputStream.write(getData());
        outputStream.close();
    }

    @Override
    public String getHeader(String name) {
        String header = null;
        List<String> headers = headersMap.get(name);

        if ((headers != null) && (headers.size() > 0)) {
            header = headers.get(0);
        }

        return header;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headersMap.keySet();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return headersMap.get(name);
    }

    @Override
    public String getParameterName() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.absolutePath;
    }

}

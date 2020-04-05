package com.ptby.dynamicreturntypeplugin;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestVirtualFile extends VirtualFile {

    private final String json;
    private TestVirtualFile parentTestVirtualFile;
    private String canonicalFilePath;


    public TestVirtualFile() {
        this( "" );
    }


    public TestVirtualFile( String json ) {
        this.json = json;
    }


    public void setCanonicalPath( String canonicalFilePath ) {
        this.canonicalFilePath = canonicalFilePath;
    }


    @Nullable
    @Override
    public String getCanonicalPath() {
        return canonicalFilePath;
    }


    @NotNull
    @Override
    public String getName() {
        return null;
    }


    @NotNull
    @Override
    public VirtualFileSystem getFileSystem() {
        return null;
    }


    @Override
    public String getPath() {
        return null;
    }


    @Override
    public boolean isWritable() {
        return false;
    }


    @Override
    public boolean isDirectory() {
        return false;
    }


    @Override
    public boolean isValid() {
        return false;
    }


    @Override
    public VirtualFile getParent() {
        return parentTestVirtualFile;
    }


    public void setParent( TestVirtualFile parentTestVirtualFile ) {
        this.parentTestVirtualFile = parentTestVirtualFile;
    }


    @Override
    public VirtualFile[] getChildren() {
        return new VirtualFile[ 0 ];
    }


    @NotNull
    @Override
    public OutputStream getOutputStream( Object o, long l, long l2 ) throws IOException {
        return null;
    }


    @NotNull
    @Override
    public byte[] contentsToByteArray() throws IOException {
        return json.getBytes();
    }


    @Override
    public long getTimeStamp() {
        return 0;
    }


    @Override
    public long getLength() {
        return 0;
    }


    @Override
    public void refresh( boolean b, boolean b2, @Nullable Runnable runnable ) {

    }


    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
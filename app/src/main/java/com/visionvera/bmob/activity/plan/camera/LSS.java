package com.visionvera.bmob.activity.plan.camera;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class LSS {
    private String name;
    private LocalSocket receiver, sender;

    private DataInputStream dataInputStream;
    public LocalServerSocket lss;

    public LSS(String name) throws IOException {
        this.name = name;
        initLocalSocket();
    }

    private void initLocalSocket() throws IOException {

        lss = new LocalServerSocket(name);
        receiver = new LocalSocket();
        receiver.connect(new LocalSocketAddress(name));
        int buf_size = 500 * 1024;
        receiver.setReceiveBufferSize(buf_size);
        receiver.setSendBufferSize(buf_size);
        sender = lss.accept();
        sender.setReceiveBufferSize(buf_size);
        sender.setSendBufferSize(buf_size);
        dataInputStream = new DataInputStream(receiver.getInputStream());

    }

    public FileDescriptor getFileDescriptor() {
        return sender.getFileDescriptor();
    }

    public DataInputStream getReceiverStream() {
        return dataInputStream;
    }

    public void dispose() {
        try {
            lss.close();
            receiver.close();
            sender.close();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

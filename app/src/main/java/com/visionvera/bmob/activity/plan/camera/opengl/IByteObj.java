package com.visionvera.bmob.activity.plan.camera.opengl;

public interface IByteObj {

    int getSize();

    byte[] getBytes();

    void setBytes(byte[] bs);

    interface IEnum {

    }

    interface IByteObjEnum extends IEnum {
        int getValue();

        IByteObjEnum byValue(int value);

        void setValue(int value);
    }

}

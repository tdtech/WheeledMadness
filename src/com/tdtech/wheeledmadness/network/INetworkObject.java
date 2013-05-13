package com.tdtech.wheeledmadness.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface INetworkObject {
    
    void readFromStream(final DataInputStream stream);
    void writeToStream(final DataOutputStream stream);
}
package org.rmj.peepgcard;

import org.rmj.appdriver.agent.MsgBox;
import org.rmj.webcamfx.ui.Webcam;

public class ClientInfo{
    public static void main(String [] args){
        if (args.length != 2)
            MsgBox.showOk("Invalid parameter detected.");
        else {
            Webcam.displayClientInfo(args[0], args[1]);
        }
    }
}

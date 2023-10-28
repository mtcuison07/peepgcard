package org.rmj.peepgcard;

import org.rmj.appdriver.agent.MsgBox;
import org.rmj.webcamfx.ui.Webcam;

public class NewGCard{
    public static void main(String [] args){
        if (args.length == 0)
            MsgBox.showOk("No G-Card value has been passed.");
        else {
            Application.main(args);
            Webcam.displayNewGCard(args[0]);
        }
            
        
        //Webcam.displayNewGCard("4150504C49434154494F4EBBBBBBBB30333031393030303136343036BBBBBBBBBBBB");
    }
}

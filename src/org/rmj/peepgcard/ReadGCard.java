package org.rmj.peepgcard;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ui.ValidateOTPx;
import org.rmj.gcard.device.ui.GCardDevice;
import org.rmj.gcard.device.ui.GCardDeviceFactory;
import org.rmj.replication.utility.LogWrapper;

public class ReadGCard {
    public static void main(String [] args){
        String lcCardType = args[0];//args[0]; //0 - card; 1 = digita; 2 - non-chip; 3-mobileno
        String lsCardNmbr = args[1];
        String lsSource = args[2];
        String lsUserID = args[3];
        String lsMobileNo = args[4];
        
        System.out.println("CARD TYPE: " + lcCardType);
        System.out.println("CARD NUMBER: " + lsCardNmbr);
        System.out.println("USER ID: " + lsUserID);
        
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Java_Systems";
        }
        else{
            path = "/srv/GGC_Java_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        
        GRider instance = new GRider("IntegSys");
        
        if (!lsUserID.equals("")){
            if (!instance.loadUser("IntegSys", lsUserID)){
                System.err.println(instance.getMessage() + instance.getErrMsg());
                System.exit(1);
            }
        }
        
        switch(lcCardType){
            case "0":
                poGCDevice = GCardDeviceFactory.make(GCardDeviceFactory.DeviceType.SMARTCARD);
                break;
            case "1":
                poGCDevice = GCardDeviceFactory.make(GCardDeviceFactory.DeviceType.QRCODE);
                poGCDevice.setGRider(instance);
                break;
        }
        
        if (lcCardType.equals("0") || lcCardType.equals("1")){
            poGCDevice.isLoadInfo(false);
        
            if(!poGCDevice.read()){
                logwrapr.severe("readCard: Can't connect card. " + poGCDevice.getMessage());
                System.exit(1);

                if (!poGCDevice.read()){
                    logwrapr.severe("readCard: Can't connect card. " + poGCDevice.getMessage());
                    System.exit(1);
                }
            }

            lsCardNmbr = (String) poGCDevice.getCardInfo("sCardNmbr");

            //disconnect card
            poGCDevice.release();
        }else {
            ValidateOTPx loOTP = new ValidateOTPx();
            loOTP.setGRider(instance);
            loOTP.setCardNumber(lsCardNmbr);
            loOTP.setSourceNo(lsSource);
            loOTP.setMobileNo(lsMobileNo);
            
            javafx.application.Application.launch(loOTP.getClass());
            
            if (!loOTP.isOkay()){
                logwrapr.severe("OTP validation failed.");
                System.exit(1);
            }
        }
        
        PrintWriter writer;
        try {
            lsCardNmbr = lsCardNmbr.replace("Â", "").trim();
            System.out.println(lsCardNmbr);
            writer = new PrintWriter("D:/GGC_Java_Systems/temp/LGCC001.TMP", "UTF-8");
            writer.println(lsCardNmbr);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            logwrapr.severe("writeData: Exception detected. " + ex.getMessage());
            System.exit(1);
        }
        
        System.exit(0);
    }
    
    private static GCardDevice poGCDevice;
    private static LogWrapper logwrapr = new LogWrapper("PeepGCard", "D:/GGC_Java_Systems/temp/PeepGCard.log");
}

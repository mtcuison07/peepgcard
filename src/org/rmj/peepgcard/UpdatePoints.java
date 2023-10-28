package org.rmj.peepgcard;

import org.rmj.appdriver.GProperty;
import org.rmj.appdriver.GRider;
import org.rmj.peepgcard.point.update.GCPUFactory;
import org.rmj.replication.utility.LogWrapper;

public class UpdatePoints {
    public static void main (String [] args){
        LogWrapper logwrapr = new LogWrapper("UpdatePoints", "D:/GGC_Java_Systems/temp/UpdatePoints.log");
        
        String lsProdctID = "IntegSys";
        String lsUserIDxx = "M001111122";

        GRider poGRider = new GRider(lsProdctID);
        GProperty loProp = new GProperty("GhostRiderXP");

        if (!poGRider.loadEnv(lsProdctID)) System.exit(0);
        if (!poGRider.logUser(lsProdctID, lsUserIDxx)) System.exit(0);
        
        GCPUFactory instance = new GCPUFactory(poGRider);
        instance.Sendpdate();
        
        System.out.println(instance.getMessage());
    }
}
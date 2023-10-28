package org.rmj.peepgcard;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.rmj.appdriver.GProperty;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.gcard.service.GCRestAPI;
import org.rmj.gcard.trans.GCApplication;
import org.rmj.integsys.pojo.UnitGCApplication;
import org.rmj.replication.utility.LogWrapper;

public class Application {
    public static void main(String [] args){
        LogWrapper logwrapr = new LogWrapper("XMGCApplication", "D:/GGC_Java_Systems/temp/XMGCApplication.log");
        
        String lsProdctID = "IntegSys";
        String lsUserIDxx = "M001111122";

        GRider poGRider = new GRider(lsProdctID);
        GProperty loProp = new GProperty("GhostRiderXP");

        if (!poGRider.loadEnv(lsProdctID)){
            logwrapr.severe(poGRider.getMessage() + "; " + poGRider.getErrMsg());
            System.exit(1);
        }
        if (!poGRider.logUser(lsProdctID, lsUserIDxx)){
            logwrapr.severe(poGRider.getMessage() + "; " + poGRider.getErrMsg());
            System.exit(1);
        }
        
        GCApplication instance = new GCApplication();
        instance.setGRider(poGRider);
        
        UnitGCApplication gcApp;
        JSONObject loJSON;
        
        String lsSQL = "SELECT" + 
                            "  a.sTransNox" +
                            ", IFNULL(b.sMobileNo, '') sMobileNo" + 
                            ", b.sCompnyNm" +
                        " FROM G_Card_Application a" + 
                            ", Client_Master b" + 
                        " WHERE a.cTranStat = '0'" + 
                            " AND a.sClientID = b.sClientID" +
                            " AND a.cDigitalx = '1'" +
                            " AND a.sTransNox LIKE " + SQLUtil.toSQL(poGRider.getBranchCode() + "%");
        
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            while (loRS.next()){
                gcApp = (UnitGCApplication) instance.loadTransaction(loRS.getString("sTransNox"));
                
                if (gcApp != null){
                    if (loRS.getString("sMobileNo").isEmpty()){
                        System.err.println("UNSET mobile number for customer " + loRS.getString("sCompnyNm") + 
                                            "\n\nPlease update it immediately to process his G-Card Application.");
                        MsgBox.showOk("UNSET mobile number for customer " + loRS.getString("sCompnyNm") + 
                                        "\n\nPlease update it immediately to process his G-Card Application.");
                    } else {
                        loJSON = GCRestAPI.ApproveApplication(poGRider, gcApp, loRS.getString("sMobileNo"));
                    
                        if ("success".equalsIgnoreCase((String) loJSON.get("result"))){
                            logwrapr.info(loJSON.toJSONString());

                            lsSQL = "UPDATE G_Card_Application" +
                                        " SET cTranStat = '1'" +
                                    " WHERE sTransNox = " + SQLUtil.toSQL(loRS.getString("sTransNox"));

                            if (poGRider.executeQuery(lsSQL, "G_Card_Application", poGRider.getBranchCode(), "") < 1){
                                logwrapr.severe(poGRider.getMessage() + "; " + poGRider.getErrMsg());
                            }
                        } else {
                            logwrapr.severe(loJSON.toJSONString());
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            logwrapr.severe(ex.getMessage());
        }
    }
}
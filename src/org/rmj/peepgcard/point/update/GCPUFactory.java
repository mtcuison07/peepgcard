/**
 * Send G-Card Point Update to Server
 * 
 * @author Michael Cuison
 * @since 2019.07.18
 */
package org.rmj.peepgcard.point.update;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;
import org.rmj.replication.utility.LogWrapper;

public class GCPUFactory {
    public enum Source {
        PREORDER, REDEMPTION, ONLINE;
    }
    
    private GRider poGRider;
    private String psMessage;
    
    LogWrapper logwrapr = new LogWrapper("GCPUFactory", "D:/GGC_Java_Systems/temp/GCPUFactory.log");
    
    public GCPUFactory(GRider foGRider){
        poGRider = foGRider;
    }
    
    public String getMessage(){
        return psMessage;
    }

    public boolean Sendpdate(){
        psMessage = "";
        
        String lsSQL = "SELECT" + 
                            "  a.sTransNox sTransNox" +
                            ", b.sCardNmbr sCardNmbr" +
                            ", a.nPointsxx nPointsxx" +
                            ", b.sGCardNox sGCardNox" +
                            ", 'PREORDER' xSourceNm" +
                            ", a.dPickupxx xTransact" +
                        " FROM G_Card_Order_Master a" +
                            ", G_Card_Master b" +
                        " WHERE a.sGCardNox = b.sGCardNox" +
                            " AND a.cTranStat = '2'" +
                            " AND a.cPointSnt = '0'" +
                            " AND a.sTransNox LIKE " + SQLUtil.toSQL(poGRider.getBranchCode() + "%") +
                        " UNION SELECT" +
                            "  a.sTransNox sTransNox" +
                            ", b.sCardNmbr sCardNmbr" +
                            ", a.nPointsxx nPointsxx" +
                            ", b.sGCardNox sGCardNox" +
                            ", 'ONLINE' xSourceNm" +
                            ", a.dTransact xTransact" +
                        " FROM G_Card_Detail a" +
                            ", G_Card_Master b" +
                        " WHERE a.sGCardNox = b.sGCardNox" + 
                            " AND a.cPointSnt = '0'" +
                            " AND a.sTransNox LIKE " + SQLUtil.toSQL(poGRider.getBranchCode() + "%") +
                        " UNION SELECT" +
                            "  a.sTransNox sTransNox" +
                            ", b.sCardNmbr sCardNmbr" +
                            ", a.nPointsxx nPointsxx" +
                            ", b.sGCardNox sGCardNox" +
                            ", 'REDEMPTION' xSourceNm" +
                            ", a.dTransact xTransact" +
                        " FROM G_Card_Redemption a" +
                            ", G_Card_Master b" +
                        " WHERE a.sGCardNox = b.sGCardNox" +
                            " AND a.cPointSnt = '0'" +
                            " AND a.sTransNox LIKE " + SQLUtil.toSQL(poGRider.getBranchCode() + "%") +
                        " ORDER BY xTransact, sTransNox";
        
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            while (loRS.next()){
                SendPoint loSend;
                
                switch (loRS.getString("xSourceNm")){
                    case "PREORDER":
                        loSend = (SendPoint) new GCPUPreOrder(); break;
                    case "REDEMPTION":
                        loSend = (SendPoint) new GCPURedemption(); break;
                    case "ONLINE":
                        loSend = (SendPoint) new GCPUOnline(); break;
                    default:
                        logwrapr.severe("Tranasction source was not registered.");
                        psMessage = "Tranasction source was not registered.";
                        return false;
                }
                
                loSend.setGRider(poGRider);
                loSend.setParam(loRS.getString("sTransNox"), 
                                loRS.getString("sCardNmbr"), 
                                loRS.getDouble("nPointsxx"), 
                                loRS.getString("sGCardNox"));
                
                if (!loSend.send()){
                    logwrapr.severe(loSend.getMessage());
                    psMessage = loSend.getMessage();
                }
            }
        } catch (SQLException ex) {
            logwrapr.severe(ex.getMessage());
            psMessage = ex.getMessage();
            return false;
        }
        
        if (psMessage.equals(""))
            psMessage ="All points update has been sent to main server...";
        else
            psMessage ="All points update has been sent to main server(with WARNINGS)...";
        
        return true;
    }
}

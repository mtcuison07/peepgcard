package org.rmj.peepgcard.point.update;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;
import org.rmj.gcard.service.GCRestAPI;

public class GCPUPreOrder implements SendPoint{
    private GRider poGRider;
    private String psTransNox;
    private String psCardNmbr;
    private Double pnPointsxx;
    
    private boolean pbParmOkay;
    private String psMessage;
    
    @Override
    public void setGRider(GRider foGRider) {
        poGRider = foGRider;
    }

    @Override
    public boolean setParam(String fsTransNox, String fsCardNmbr, Double fnPointsxx, String fsGCardNox) {
        if (poGRider == null){
            psMessage = "Application driver is not set.";
            return false;
        }
        
        psTransNox = fsTransNox;
        psCardNmbr = fsCardNmbr;
        pnPointsxx = fnPointsxx;
        pbParmOkay = true;
        
        return true;
    }

    @Override
    public boolean send() {
        if (!pbParmOkay) {
            psMessage = "Parameters are not set.";
            return false;
        }
        
        JSONObject response = GCRestAPI.UpdatePoint(poGRider, 
                                                    psCardNmbr,
                                                    "PREORDER",
                                                    psTransNox,
                                                    pnPointsxx.longValue());
        
        String result = (String) response.get("result");
        
        if(result.equalsIgnoreCase("success")){
            String sql = "UPDATE G_Card_Order_Master" + 
                        " SET cPointSnt = '1'" + 
                        " WHERE sTransNox = " + SQLUtil.toSQL(psTransNox);
            
            poGRider.executeQuery(sql, "G_Card_Order_Master", "", "");
            if(!poGRider.getErrMsg().isEmpty()){
                psMessage = poGRider.getErrMsg();
                return false;
            }
            return true;
        } 
        
        JSONParser parser = new JSONParser();
        try {
            response = (JSONObject) parser.parse((String) response.get("error"));
            
            psMessage = (String) response.get("code") + ": " + (String) response.get("message");
        } catch (ParseException ex) {
            psMessage = ex.getMessage();
            Logger.getLogger(GCPUPreOrder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    @Override
    public String getMessage() {
        return psMessage;
    }
}

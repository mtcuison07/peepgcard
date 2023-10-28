package org.rmj.peepgcard.point.update;

import org.json.simple.JSONObject;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;
import org.rmj.gcard.service.GCRestAPI;

public class GCPUOnline implements SendPoint{
    private GRider poGRider;
    private String psTransNox;
    private String psCardNmbr;
    private Double pnPointsxx;
    private String psGCardNox;
    
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
        psGCardNox = fsGCardNox;
        
        if (psTransNox.equals("")){
            psMessage = "Source transaction number is not set.";
            return false;
        }
        
        if (psCardNmbr.equals("")){
            psMessage = "G-Card number is not set.";
            return false;
        }
        
        if (psGCardNox.equals("")){
            psMessage = "G-Card numberX is not set.";
            return false;
        }
        
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
                                                    "ONLINE",
                                                    psTransNox,
                                                    pnPointsxx.longValue());
        
        String result = (String) response.get("result");
        
        if(result.equalsIgnoreCase("success")){
            poGRider.beginTrans();
            
            String sql = "UPDATE G_Card_Detail" + 
                                " SET cPointSnt = '1'" + 
                                " WHERE sTransNox = " + SQLUtil.toSQL(psTransNox);
            
            poGRider.executeQuery(sql, "G_Card_Detail", "", "");
            if(!poGRider.getErrMsg().isEmpty()){
                poGRider.rollbackTrans();
                psMessage = poGRider.getErrMsg();
                return false;
            }
            
                    
            sql = "UPDATE G_Card_Master" + 
                    " SET sLastLine = " + SQLUtil.toSQL(psTransNox) + 
                        ", sModified = " + SQLUtil.toSQL(poGRider.getUserID()) + 
                        ", dModified = " + SQLUtil.toSQL(poGRider.getServerDate()) + 
                    " WHERE sGCardNox = " + SQLUtil.toSQL(psGCardNox);
            
            poGRider.executeQuery(sql, "G_Card_Master", "", "");
            if(!poGRider.getErrMsg().isEmpty()){
                poGRider.rollbackTrans();
                psMessage = poGRider.getErrMsg();
                return false;
            }
            
            poGRider.commitTrans();
            return true;
        } 
        
        //JSONParser parser = new JSONParser();
        //try {
            //response = (JSONObject) parser.parse((String) response.get("error"));
            
            //psMessage = (String) response.get("code") + ": " + (String) response.get("message");
            
            psMessage = response.toJSONString();
        //} catch (ParseException ex) {
            //psMessage = ex.getMessage();
            //Logger.getLogger(GCPUOnline.class.getName()).log(Level.SEVERE, null, ex);
        //}
        
        return false;
    }

    @Override
    public String getMessage() {
        return psMessage;
    }
}

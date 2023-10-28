package org.rmj.peepgcard.point.update;

import org.rmj.appdriver.GRider;

public interface SendPoint {
    void setGRider(GRider foGRider);
    boolean setParam(String fsTransNox, String fsCardNmbr, Double fnPointsxx, String fsGCardNox);
    String getMessage();
    boolean send();
}

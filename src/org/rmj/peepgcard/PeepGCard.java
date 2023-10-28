package org.rmj.peepgcard;
/**
 *
 * @author kalyptus
 */
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.rmj.replication.utility.LogWrapper;
import org.rmj.gcard.base.misc.GCEncoder;

public class PeepGCard {
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
      String lsCardNmbr;
      if(!connectCard()){
         lsCardNmbr = "";
      }
      else 
         lsCardNmbr = (String) GCEncoder.read(GCEncoder.CARD_NUMBER);
      
      PrintWriter writer = new PrintWriter("D:/GGC_Java_Systems/temp/LGCC001.TMP", "UTF-8");
      writer.println(lsCardNmbr);
      writer.close();
   }
   
   public static boolean connectCard(){
      if(!GCEncoder.init()){
         logwrapr.severe("connectCard: Can't Initialize card. " + GCEncoder.getErrMessage());
         return false;
      }

      if(!GCEncoder.connect()){
         logwrapr.severe("connectCard: Can't Connect card. " + GCEncoder.getErrMessage());
         return false;
      }

      return true;
   }

   public static boolean releaseCard(){
      if(!GCEncoder.disconnect()){
         return false;
      }
      return true;
   }
   
   private static LogWrapper logwrapr = new LogWrapper("PeepGCard", "D:/GGC_Java_Systems/temp/PeepGCard.log");
}

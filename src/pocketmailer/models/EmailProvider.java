/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pocketmailer.models;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 *
 * @author marcmartinez
 */
public class EmailProvider {
    public int id;
    public String host;
    public String dateAdded;
    public int enabled;
    public int amountPerTransmitter;
    public String seedAccount;
    public int maxRecipientsPerSession;
    public int sleepEvery;
    public int sleepDurationSeconds;

    
    public EmailProvider(){
        
    }
    
    public int getId(){
        return this.id;
    }
    
    public String getHost(){
        return this.host;
    }
    
    public String getSeedAccount(){
        return this.seedAccount;
    }
    
    public boolean isEnabled(){
        if(this.enabled > 0){
            return true;
        }
        return false;
    }
    
    public String getMXHost() throws TextParseException{
        //System.out.println("Looking up MX Host for : " + this.host);
        Record[] records = new Lookup(this.host, Type.MX).run();
        int lowestPriority = 9999;
        String mxHost = "";
        for(int i = 0; i < records.length; i++) {
                MXRecord mx = (MXRecord) records[i];
                if(mx.getPriority() < lowestPriority){
                    lowestPriority = mx.getPriority();
                    mxHost = mx.getTarget().toString();
                }
        }
        //System.out.println("MX Host Found: " + mxHost);
        return mxHost;
    }
}

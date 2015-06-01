/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pocketmailer.models;

/**
 *
 * @author marcmartinez
 */
public class MessengerSetting {
    public int currentCampaignId;
    public int enabled;
    public int lastBitlyAccountId;
    public String imageHost;
    public String ftpUser;
    public String ftpPass;
    public int ftpPort;
    public int currentSmtpAccountId;
    public String confirmationSubject;
    public String confirmationMessage;
    
    public MessengerSetting(){
        
    }
    
    public int getCurrentCampaignId(){
        return this.currentCampaignId;
    }
    
    public boolean isEnabled(){
        if(this.enabled > 0){
            return true;
        }
        return false;
    }
    
    public String getHostname(){
        return this.imageHost ;
    }
    
    public int getCurrentSmtpAccountId(){
        return this.currentSmtpAccountId;
    }
    
    public String getConfirmationSubject(){
        return this.confirmationSubject;
    }
    
    public String getConfirmationMessage(){
        return this.confirmationMessage;
    }
    
    public String getFriendlyFrom(){
        return "Member Alerts";
    }
}

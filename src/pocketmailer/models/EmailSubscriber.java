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
public class EmailSubscriber {
    public int id;
    public String emailAddress;
    public String dateAdded;
    public String lastSendDate;
    public int emailProviderId;
    public int isOptOut;
    public int isValid;
    public int enabled;
    public String invalidDate;
    public String optOutComments;
    public String optOutDate;
    public int suppressed;
    public String emailAddressMD5;
    public String password;
    public int confirmed;
    public int lastCampaignId;
    public String confirmationDate;
    public String lastValidationDate;
    public String personalName;
    
    
    public int getId(){
        return this.id;
    }
    
    
    public String getEmailAddress(){
        return this.emailAddress;
    }
}

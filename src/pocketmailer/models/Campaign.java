/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pocketmailer.models;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author marcmartinez
 */
public class Campaign {
    public int id;
    public String name;
    public String offerUrl;
    public String unsubscribeUrl;
    public int enabled;
    public String subject;
    public String friendlyFrom;
    public String md5;

    public int getId(){
        return this.id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getOfferUrl(){
        return this.offerUrl;
    }
    
    public String getUnsubscribeUrl(){
        return this.unsubscribeUrl;
    }
    
    public boolean isEnabled(){
        if(this.enabled > 0){
            return true;
        }
        return false;
    }
    
    public String getSubject(){
        return this.subject;
    }
    
    public String getFriendlyFrom(){
        return this.friendlyFrom;
    }
    
    public String generateMaskedOfferUrl(String host, int transmitterId, int smtpAccountId, int emailSubscriberId){
        return "http://" + host + "/warpdrive.php?cid=" + this.getId() + "&tid=" + transmitterId + "&said=" + smtpAccountId + "&esid=" + emailSubscriberId + "&" + RandomStringUtils.randomAlphabetic(20).toLowerCase() + "=" + RandomStringUtils.randomAlphabetic(20).toLowerCase();
    }
    
    public String generateMaskedUnsubscribeUrl(String host, int transmitterId, int smtpAccountId, int emailSubscriberId){
        return "http://" + host + "/disengage.php?cid=" + this.getId() + "&tid=" + transmitterId + "&said=" + smtpAccountId + "&esid=" + emailSubscriberId + "&" + RandomStringUtils.randomAlphabetic(20).toLowerCase() + "=" + RandomStringUtils.randomAlphabetic(20).toLowerCase();
    }
    
    public String generateMaskedOfferImageUrl(String host, int transmitterId, int smtpAccountId, int emailSubscriberId){
        return "http://" + host + "/offer_image.php?cid=" + this.getId() + "&tid=" + transmitterId + "&said=" + smtpAccountId + "&esid=" + emailSubscriberId + "&" + RandomStringUtils.randomAlphabetic(20).toLowerCase() + "=" + RandomStringUtils.randomAlphabetic(20).toLowerCase();
    }
    
    public String generateMaskedUnsubscribeImageUrl(String host, int transmitterId, int smtpAccountId, int emailSubscriberId){
        return "http://" + host + "/unsub_image.php?cid=" + this.getId() + "&tid=" + transmitterId + "&said=" + smtpAccountId + "&esid=" + emailSubscriberId + "&" + RandomStringUtils.randomAlphabetic(20).toLowerCase() + "=" + RandomStringUtils.randomAlphabetic(20).toLowerCase();
    }
    
    public String generateMaskedOptOutImageUrl(String host, int transmitterId, int smtpAccountId, int emailSubscriberId){
        return "http://" + host + "/oo_image.php?cid=" + this.getId() + "&tid=" + transmitterId + "&said=" + smtpAccountId + "&esid=" + emailSubscriberId + "&" + RandomStringUtils.randomAlphabetic(20).toLowerCase() + "=" + RandomStringUtils.randomAlphabetic(20).toLowerCase();
    }
    
    public String generateMaskedOptOutUrl(String host, int transmitterId, int smtpAccountId, int emailSubscriberId){
        return "http://" + host + "/oo.php?cid=" + this.getId() + "&tid=" + transmitterId + "&said=" + smtpAccountId + "&esid=" + emailSubscriberId + "&" + RandomStringUtils.randomAlphabetic(20).toLowerCase() + "=" + RandomStringUtils.randomAlphabetic(20).toLowerCase();
    }
    
    public String generateMaskedListUnsubscribeUrl(String host, int emailSubscriberId, String emailAddress){
        return "http://" + host + "/unsub.php?ea=" + emailAddress + "&esid=" + emailSubscriberId + "&" + RandomStringUtils.randomAlphabetic(20).toLowerCase() + "=" + RandomStringUtils.randomAlphabetic(20).toLowerCase();
    }
    
    public String generateEmailCreative(String host, int transmitterId, int smtpAccountId, int emailSubscriberId) throws Exception{
        String creativeHtml = "<center>";
        creativeHtml += "<a href=\"" + this.generateMaskedOfferUrl(host, transmitterId, smtpAccountId, emailSubscriberId) + "\" id=\"" + RandomStringUtils.randomAlphabetic(50).toLowerCase() + "\"><img src=\"" + this.generateMaskedOfferImageUrl(host, transmitterId, smtpAccountId, emailSubscriberId) + "\" id=\"" + RandomStringUtils.randomAlphabetic(50).toLowerCase() + "\"/></a><br>";
        creativeHtml += "<a href=\"" + this.generateMaskedUnsubscribeUrl(host, transmitterId, smtpAccountId, emailSubscriberId)+ "\" id=\"" + RandomStringUtils.randomAlphabetic(50).toLowerCase() + "\"><img src=\"" + this.generateMaskedUnsubscribeImageUrl(host, transmitterId, smtpAccountId, emailSubscriberId) + "\" id=\"" + RandomStringUtils.randomAlphabetic(50).toLowerCase() + "\"/></a><br>";
        creativeHtml += "<a href=\"" + this.generateMaskedOptOutUrl(host, transmitterId, smtpAccountId, emailSubscriberId) + "\" id=\"" + RandomStringUtils.randomAlphabetic(50).toLowerCase() + "\"><img src=\"" + this.generateMaskedOptOutImageUrl(host, transmitterId, smtpAccountId, emailSubscriberId) + "\" id=\"" + RandomStringUtils.randomAlphabetic(50).toLowerCase() + "\"/></a><br>";
        creativeHtml += "</center>";
        return creativeHtml;
    }
  
    
}
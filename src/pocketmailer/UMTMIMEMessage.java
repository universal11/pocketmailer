/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pocketmailer;

import de.agitos.dkim.DKIMSigner;
import de.agitos.dkim.SMTPDKIMMessage;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author marcmartinez
 */

public class UMTMIMEMessage extends SMTPDKIMMessage{
    private int emailSubscriberId = 0;
    
    public UMTMIMEMessage(Session session, DKIMSigner dkimSigner, int emailSubscriberId) {
        super(session, dkimSigner);
    }
    
    /*
    public UMTMIMEMessage(Session session, DKIMSigner dkimSigner, int emailSubscriberId) {
        super(session, dkimSigner);
        this.emailSubscriberId = emailSubscriberId;
    }
    */
    protected void updateMessageID() throws MessagingException {
	//setHeader("Message-ID", UUID.randomUUID().toString() + "-" + getCurrentUnixTimestamp() + "---umes." + this.emailSubscriberId + "---umes." + UUID.randomUUID().toString());
        removeHeader("Message-Id");
    }
    
    public static long getCurrentUnixTimestamp(){
        return (System.currentTimeMillis() / 1000L );
    }
}


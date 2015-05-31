/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pocketmailer;
import com.google.gson.Gson;
import com.jcraft.jsch.*;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import de.agitos.dkim.Canonicalization;
import de.agitos.dkim.DKIMSigner;
import de.agitos.dkim.SigningAlgorithm;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import pocketmailer.models.EmailSubscriber;

/**
 *
 * @author marcmartinez
 */
public class PocketMailSession {
    private String remoteHost = "";
    private String remoteUser = "";
    private String remotePassword = "";
    private String remoteHostIPAddress = "";
    private Session sshSession = null;
    private boolean doTest = false;
    public static final String KEY = "jcsMjAsNjY4LDgzNCIgc2hhcGU9InJlY3QiI";
    public static final String HOST = "universalmessenger.net";
    
    
    
    public PocketMailSession(){
        
    }
    
    public void init(String remoteHost, String remoteUser, String remotePassword, boolean doTest) throws UnknownHostException{
        this.remoteHost = remoteHost;
        this.remoteUser = remoteUser;
        this.remotePassword = remotePassword;
        this.remoteHostIPAddress = InetAddress.getByName(this.remoteHost).getHostAddress();
        this.doTest = doTest;
    }
    
    public void showSettings(){
        System.out.println("Remote Host: " + this.remoteHost);
        System.out.println("Remote Host IP: " + this.remoteHostIPAddress);
        System.out.println("Remote User:" + this.remoteUser);
        System.out.println("Remote Password:" + this.remotePassword);
        System.out.println("Mode: " + ( (this.doTest) ? "Test" : "Normal" ));
    }
    
    public void start() throws JSchException, IOException, Exception{
        //initialization
        this.showSettings();
        this.openSSHSession();
        this.openSSHTunnel();
        
        
        //process
        this.installPostfix();
        this.configurePostfix();
       
        
        this.installOpenDKIM();
        this.configureOpenDKIM();
        
        this.pauseQueue();
        this.prepareMessage();
        this.resumeQueue();
        
        
        
        
        
        
        
        //cleanup
        this.closeSSHSession();
        
    }
    
    public String readFile(String filePath) throws FileNotFoundException, IOException{
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line = null;
        String contents = "";
        while((line = bufferedReader.readLine()) != null){
            contents += (line + "\n");
        }
        return contents;
    }
    
    public void closeSSHSession(){
        this.sshSession.disconnect();
    }
    
    public void openSSHSession() throws JSchException{
        JSch jsch=new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        this.sshSession = jsch.getSession(this.remoteUser, this.remoteHost, 22);
        this.sshSession.setConfig(config);
        this.sshSession.setPassword(this.remotePassword);
        this.sshSession.connect();
    }
    
    public int openSSHTunnel() throws JSchException{
        return this.sshSession.setPortForwardingL(4444, "localhost", 25);
    }
    
    public String sendSSHCommand(String command) throws IOException, JSchException{
        //System.out.println("Executing: " + command);
        ChannelExec channel=(ChannelExec) this.sshSession.openChannel("exec");
        ((ChannelExec)channel).setCommand(command);
        BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        
        channel.connect();
        String responseLine = null;
        String response = "";
        while( (responseLine = input.readLine()) != null ){
            response += responseLine;
        }
        
        input.close();
        channel.disconnect();
        return response;
        //System.out.println("Response: " + response);

    }
    
    public String getDKIMPrivateKey() throws IOException, JSchException{
        String dkimKey = this.sendSSHCommand("cat /etc/opendkim/keys/mail.private.der");
        //System.out.println(dkimKey);
        return dkimKey;
    }
    
    public void configureOpenDKIM() throws IOException, JSchException{
        this.sendSSHCommand("cd /etc/opendkim/keys;opendkim-genkey -s mail -d " + this.remoteHost + ";chown opendkim:opendkim mail.txt; chown opendkim:opendkim mail.private;openssl pkcs8 -topk8 -inform PEM -outform DER -in /etc/opendkim/keys/mail.private -nocrypt | base64 -w 0 > /etc/opendkim/keys/mail.private.der;sed -i 's/mail._domainkey/mail._domainkey." + this.remoteHost + "./g' /etc/opendkim/keys/mail.txt;echo \"*@" + this.remoteHost + " mail._domainkey." + this.remoteHost + "\" > /etc/opendkim/SigningTable; echo \"mail._domainkey." + this.remoteHost + " " + this.remoteHost + ":mail:/etc/opendkim/keys/mail.private\" > /etc/opendkim/KeyTable;echo -e \"127.0.0.1\nlocalhost\n" + this.remoteHostIPAddress + "\n" + this.remoteHost + "\n\" > /etc/opendkim/TrustedHosts;echo \"" + this.readFile("opendkim.conf") + "\" > /etc/opendkim.conf;service opendkim restart;");
    }
    
    public void installOpenDKIM() throws IOException, JSchException{
        this.sendSSHCommand("apt-get update;apt-get dist-upgrade;apt-get install opendkim opendkim-tools;mkdir -p /etc/opendkim/keys;");
    }
    
    public void configurePostfix() throws IOException, JSchException{
        this.sendSSHCommand("hostname " + this.remoteHost + "; postconf -e smtp_bind_address=\"" + this.remoteHostIPAddress + "\"; postconf -e myhostname=\"" + this.remoteHost + "\";postconf -e mydestination=\"" + this.remoteHost + "\";postconf -e mydomain=\"" + this.remoteHost + "\"; postconf -e smtpd_banner=\"" + this.remoteHost + " ESMTP $mail_name\"; postconf -e milter_protocol=2; postconf -e milter_default_action=accept; postconf -e smtpd_milters=inet:localhost:8891; postconf -e non_smtpd_milters=inet:localhost:8891; postfix reload;");
    }
   
    
    public void installPostfix() throws IOException, JSchException{
        this.sendSSHCommand("apt-get -y update;apt-get -y dist-upgrade;apt-get -y install postfix postfix-pcre;");
    }
    
    public void pauseQueue() throws IOException, JSchException{
        this.sendSSHCommand("postconf -e defer_transports=smtp; postfix reload;");
    }
    
    public void resumeQueue() throws IOException, JSchException{
        this.sendSSHCommand("postconf -e defer_transports=; postfix reload; postqueue -f;");
    }
    
    public void prepareMessage() throws Exception{
        //MessengerSetting messengerSetting = UMTController.getMessengerSetting();
        //Campaign campaign = UMTController.getCampaign(messengerSetting.getCurrentCampaignId());
        //EmailProvider emailProvider = UMTController.getEmailProvider(emailProviderId);
        ArrayList<EmailSubscriber> emailSubscribers = new ArrayList<EmailSubscriber>();
        
        this.sendMessage();
    }
    
    public static EmailSubscriber getEmailSubscriber(int emailSubscriberId) throws Exception{
        Gson gson = new Gson();
        return gson.fromJson(sendPost("http://universalmessenger.net/", "controller=EmailSubscriberController&action=getById&api_key=" + KEY + "&id=" + emailSubscriberId), EmailSubscriber.class);
    }
    
    public static String sendPost(String urlString, String parameterString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        //add request header
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
        httpConnection.setRequestProperty("charset", "utf-8");
        httpConnection.setRequestProperty("Content-Length", "" + Integer.toString(parameterString.getBytes().length));

        // Send post request
        httpConnection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(httpConnection.getOutputStream());
        outputStream.writeBytes(parameterString);
        outputStream.flush();
        outputStream.close();

        int responseCode = httpConnection.getResponseCode();
        BufferedReader input = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = input.readLine()) != null) {
                response.append(inputLine);
        }
        input.close();
        httpConnection.disconnect();
        return response.toString();

    }
    
    
    public void sendMessage() throws InvalidKeySpecException, Exception{
        Properties props = new Properties();
        props.put("mail.smtp.localhost", "localhost");
        //props.put("mail.smtp.localaddress", transmitter.getAddress());
        props.put("mail.smtp.host", "localhost");
        //props.put("mail.smtp.socketFactory.port", "465");
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.port", "4444");
        props.put("mail.smtp.from", "mail@" + this.remoteHost);


        javax.mail.Session session = javax.mail.Session.getInstance(props);
        
        String sender = "mail@" + this.remoteHost;
        String senderFirstName = "Admin";
        String senderLastName = "Test";
        
        String recipient = "turtle.rawrior11@yahoo.com";


        try {
             // Base64 encoded private key in PKCS8 format
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            String dkimPrivateKey = this.getDKIMPrivateKey();
            byte[] privateKeyPKCS8 = Base64.decode(dkimPrivateKey);
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKeyPKCS8);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateSpec);
            DKIMSigner dkimSigner = new DKIMSigner(this.remoteHost, "mail", privateKey);
            //dkimSigner.setHeaderCanonicalization(Canonicalization.SIMPLE);
            //dkimSigner.setBodyCanonicalization(Canonicalization.RELAXED);
            //dkimSigner.setLengthParam(true);
            //dkimSigner.setSigningAlgorithm(SigningAlgorithm.SHA1withRSA);
            //dkimSigner.setZParam(true);
            UMTMIMEMessage message = new UMTMIMEMessage(session, dkimSigner, 0);
            //MimeMessage message = new UMTMIMEMessage(session, 0);

            //add list unsubscribe header
            //message.setHeader("List-Unsubscribe", "<" + campaign.generateMaskedListUnsubscribeUrl(messengerSetting.getHostname(), emailSubscriberId, recipient) + ">");
            
            //sets from: header
            InternetAddress[] fromAddresses = new InternetAddress[1];
            fromAddresses[0] = new InternetAddress(sender, senderFirstName + " " + senderLastName);
            message.addFrom(fromAddresses);
            
            //sets reply-to: header
            InternetAddress[] replyToAddresses = new InternetAddress[1];
            replyToAddresses[0] = new InternetAddress("mail@" + this.remoteHost);
            message.setReplyTo(replyToAddresses);

            message.addRecipient(Message.RecipientType.TO, new InternetAddress( recipient ));
            message.setSubject("test subject");
            message.setContent("just a test", "text/html");

            Transport.send(message);

            System.out.println("Response: Confirmation | Status: Success | Sender: " + sender + " | Recipient: " + recipient + " | Address: " + this.remoteHostIPAddress);

        } 
        catch(SendFailedException e){
            
            if(e.getInvalidAddresses() != null){
                for(Address emailAddress :  e.getInvalidAddresses()){
                    //UMTController.markAsInvalid(emailSubscriberId);
                    System.out.println("Response: Confirmation | Status: Invalid | Sender: " + sender + " | Recipient: " + recipient + " | Address: " + this.remoteHostIPAddress);
                }
            }
            
            if(e.getValidUnsentAddresses() != null){
                for(Address emailAddress : e.getValidUnsentAddresses()){
                    String errorMessageToLower = e.getMessage().toLowerCase();
                    if(errorMessageToLower.contains("account") || errorMessageToLower.contains("discontinued") || errorMessageToLower.contains("discontinued") || errorMessageToLower.contains("disabled") || errorMessageToLower.contains("invalid")){
                        //UMTController.markAsInvalid(emailSubscriberId);
                        System.out.println("Response: Confirmation | Status: Invalid | Sender: " + sender + " | Recipient: " + recipient + " | Address: " + this.remoteHostIPAddress);
                    }
                    else{
                        //UMTController.requeueForConfirmation(emailSubscriberId);
                        System.out.println("Response: Confirmation | Status: Fail | Sender: " + sender + " | Recipient: " + recipient + " | Address: " + this.remoteHostIPAddress);
                    }

                }
            }
            
            
            System.out.println("Error: " + e.getMessage());
        }
        catch (MessagingException e) {
            //UMTController.requeueForConfirmation(emailSubscriberId);
            System.out.println("Response: Confirmation | Status: Fail | Sender: " + sender + " | Recipient: " + recipient + " | Address: " + this.remoteHostIPAddress);
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    
    
    
    
}
 
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pocketmailer;

import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcmartinez
 */
public class PocketMailer {

    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        String remoteHost = "";
        String remoteUser = "";
        String remotePassword = "";
        int seedEvery = 0;
        int limit = 0;
        boolean doTest = false;
        
        for(int i=0; i < args.length; i++){
            String arg = args[i];
            switch(arg){
                case "--remote_host":
                    remoteHost = args[(i + 1)];
                    break;
                case "--remote_user":
                    remoteUser = args[(i + 1)];
                    break;
                case "--remote_password":
                    remotePassword = args[(i + 1)];
                    break;
                case "--limit":
                    limit = Integer.parseInt(args[(i + 1)]);
                    break;
                case "--seed_every":
                    seedEvery = Integer.parseInt(args[(i + 1)]);
                    break;
                case "--test":
                    doTest = true;
                    break;
                default:
                    break;
            }
            
        }
        
        if(!remoteHost.isEmpty()){
            PocketMailSession pocketMailSession = new PocketMailSession();
            
            try {
                pocketMailSession.init(remoteHost, remoteUser, remotePassword, limit, seedEvery, doTest);
                pocketMailSession.start();
            } catch (JSchException | IOException ex) {
                Logger.getLogger(PocketMailer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(PocketMailer.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        }

    }
    
}

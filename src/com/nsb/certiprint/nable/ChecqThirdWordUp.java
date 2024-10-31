package com.nsb.certiprint.nable;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class ChecqThirdWordUp extends RecordLifecycle {
    
    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);
    
    public static void Info(String line) {

        // String filePath = "D:\\test.txt";
        String filePath = "/nsbt24/debug/logzDaham.txt";
        line = String.valueOf(line) + "\n";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try {
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                FileWriter fw = new FileWriter(filePath);
                fw.write("---" + line);
                fw.close();
            } else {
                Files.write(Paths.get(filePath, new String[0]), line.getBytes(),
                        new OpenOption[] { StandardOpenOption.APPEND });
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
    

    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        
        String rtnValue="";
        String toReady="";
        
        try {

                double amount = Double.parseDouble(data);
                String vl= CurrencyToWordsNew.convertToWords(amount);
                String output = vl.toUpperCase();
                
                    toReady = output.trim().replaceAll(" +", " ")+" ONLY";
             
                Info("toReady3="+toReady);  
                if(toReady.length()>88 && toReady.length()<132){
                    
                    if(toReady.substring(87,88).equals("")){
                        rtnValue= toReady.substring(88,toReady.length());
                        Info("if-1-rtnValue3="+rtnValue);  
                    }else{
                        rtnValue= "-"+toReady.substring(88,toReady.length());
                        Info("else-1-rtnValue3="+rtnValue);  
                    }
                 
                 }else if(toReady.length()>=132){
                     
                     if(toReady.substring(87,88).equals("")){
                         rtnValue= toReady.substring(88,132);
                         Info("if-2-rtnValue3="+rtnValue);  
                     }else{
                         rtnValue= "-"+toReady.substring(88,132);
                         Info("else-2-rtnValue3="+rtnValue);  
                     }
                     
                 }

        } catch (Exception e) {
            rtnValue = "-";
        }
        
        return rtnValue;
    }

}

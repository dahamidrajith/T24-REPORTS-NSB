package com.nsb.certiprint.nable;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class ChecqFirstWordUp extends RecordLifecycle {
    
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
               
                
                Info("toReady1="+toReady);                
                if(toReady.length()>=44){
                    
                   rtnValue= toReady.substring(0,44);
                   Info("if-rtnValue1="+rtnValue);    
                }else if(toReady.length()<44){
                    
                    rtnValue= toReady.substring(0,toReady.length());
                    Info("else-rtnValue1="+rtnValue);    
                }

        } catch (Exception e) {
            rtnValue = "-";
        }
        
        return rtnValue;
    }
    
    

}

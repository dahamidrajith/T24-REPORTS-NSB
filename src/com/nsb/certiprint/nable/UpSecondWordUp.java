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
public class UpSecondWordUp extends RecordLifecycle {
    
    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);
    

    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        DecimalFormat df = new DecimalFormat("0.00");
        Contract cnt = new Contract((T24Context) this);
        cnt.setContractId(data);
        
        String rtnValue="";
        String toReady="";
        
        
        List<BalanceMovement> PrincipleCurrentList1 = null;
        
        try {
            
            PrincipleCurrentList1 = cnt.getBalanceMovementsForPeriod("CURACCOUNT", "",tdate,tdate);
            for (BalanceMovement bl : PrincipleCurrentList1) {
                String balance = bl.getBalance().toString();
                
                double outstandingBalIntSub = Double.parseDouble(balance);
                String vl= CurrencyToWordsNew.convertToWords(outstandingBalIntSub);
                String output = vl.toUpperCase();
                
                    toReady = output.trim().replaceAll(" +", " ")+" ONLY";
               
                
                StringBuilder stringBuilder = new StringBuilder();
                String space="                       ";
                
                if(toReady.length()>44 && toReady.length()<88){
                    
                    if(toReady.substring(43,44).equals("")){
                        rtnValue= toReady.substring(44,toReady.length());
                    }else{
                        rtnValue= "-"+toReady.substring(44,toReady.length());
                    }
                 
                 }else if(toReady.length()>=88){
                     
                     if(toReady.substring(43,44).equals("")){
                         rtnValue= toReady.substring(44,88);
                     }else{
                         rtnValue= "-"+toReady.substring(44,88);
                     }
                     
                 }
            }
            
        } catch (Exception e) {
            rtnValue = "-";
        }
        
        return rtnValue;
    }
    
    

}

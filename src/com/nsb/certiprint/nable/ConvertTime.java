package com.nsb.certiprint.nable;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
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
public class ConvertTime extends RecordLifecycle {
    
    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);
    

    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        Date date = new Date();
        String rtnValue="";
        
        try{
            SimpleDateFormat formatter = new SimpleDateFormat(data);  
            
            formatter = new SimpleDateFormat("yyyy MMM dd");  
            rtnValue= formatter.format(date); 
            
            }catch(Exception e){
                rtnValue=data;
            }
        
        return rtnValue;
    }
    
    

}

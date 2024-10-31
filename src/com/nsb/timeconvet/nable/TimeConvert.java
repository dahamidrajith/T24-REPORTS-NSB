package com.nsb.timeconvet.nable;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;    

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class TimeConvert extends RecordLifecycle {


    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        String rtnValue="";
        
        try{
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");  
            LocalDateTime now = LocalDateTime.now();
            rtnValue=(dtf.format(now));
            
        }catch(Exception e){
            rtnValue="0.00";
        }
        
        return rtnValue;
    }
    
    

}

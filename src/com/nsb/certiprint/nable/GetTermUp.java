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
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarraccount.AaArrAccountRecord;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class GetTermUp extends RecordLifecycle {
    
    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);
    

    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        String rtnValue="";
        Contract cnt = new Contract((T24Context) this);
        cnt.setContractId(data);
        
        try {
            
            AaArrAccountRecord AaAccdet2 = new AaArrAccountRecord(cnt.getConditionForPropertyEffectiveDate("ACCOUNT", tdate));
            String befValue = AaAccdet2.getAccountTitle1().toString();
            int length=befValue.length();
            
            rtnValue=befValue.substring(1, length-1);
            
        }catch(Exception e){
            rtnValue="M";
        }
        
        return rtnValue;
    }
    
    

}

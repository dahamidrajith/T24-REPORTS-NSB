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
import com.temenos.t24.api.records.aaprddescustomer.AaPrdDesCustomerRecord;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class GetPsbkOperatingInstructions99 extends RecordLifecycle {
    
    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);

    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        Contract cnt = new Contract((T24Context) this);
        cnt.setContractId(data);
       
        String rtnValue="";
        
        try{
            
            AaPrdDesCustomerRecord obj= new AaPrdDesCustomerRecord(cnt.getConditionForPropertyEffectiveDate("CUSTOMER", tdate));
            rtnValue=obj.getLocalRefField("L.OPE.INST").toString();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return rtnValue;
    }



    

}

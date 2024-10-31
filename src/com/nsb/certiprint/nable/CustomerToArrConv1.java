package com.nsb.certiprint.nable;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebcustomprintnsb.EbCustomPrintNsbRecord;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class CustomerToArrConv1 extends RecordLifecycle {
    
    DataAccess da = new DataAccess((T24Context) this);
    

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        EbCustomPrintNsbRecord custPrintRec = new EbCustomPrintNsbRecord(currentRecord);
        
        try{
            
            String getCustNo=custPrintRec.getArrId().toString();
            AccountRecord acont = new AccountRecord(this.da.getRecord("ACCOUNT", getCustNo));
            String arrId=acont.getArrangementId().toString();
            custPrintRec.setArrId(acont.getArrangementId());
            currentRecord.set(custPrintRec.toStructure());
            
            
        }catch(Exception e){
        }
    }

}

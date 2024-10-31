package com.nsb.certiprint.nable;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarraccount.AaArrAccountRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.LinkedApplClass;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddeslimit.AaPrdDesLimitRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebcustomprintnsb.EbCustomPrintNsbRecord;
import com.temenos.tafj.api.client.impl.T24Context;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class SavingOverRide16 extends RecordLifecycle {
    
    DataAccess da = new DataAccess((T24Context) this);
    

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);
        EbCustomPrintNsbRecord obj= new EbCustomPrintNsbRecord(currentRecord);
        try{
            String inputArrangeId=obj.getArrId().toString();
            String referanceId=currentRecordId;
            String lonAccNo="";
            String getEnterdCertiNo="";

            Contract cnt = new Contract((T24Context) this);
            cnt.setContractId(inputArrangeId);

            AaPrdDesAccountRecord acc = cnt.getAccountCondition("");
            
            String certno = acc.getLocalRefField("L.CERTIFICATE.N0").getValue() ;
            
            if (!(certno.equalsIgnoreCase(currentRecordId))) {
                obj.getArrId().setError("certificate Number Mismatch");
            }
            
        }catch(Exception e){
        }
        
        return obj.getValidationResponse();
    }
    
    

}

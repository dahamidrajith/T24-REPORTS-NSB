package com.mcbc.nsb.pssbkserial;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.temenos.api.TDate;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class PassbookSerialValidatz extends ActivityLifecycle {

    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);

    @Override
    public TValidationResponse validateRecord(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record) {

       
        
        String arrid = arrangementActivityRecord.getArrangement().toString();
        Contract contract = new Contract((T24Context) this);
        contract.setContractId(arrid);
        String rtnValue = "";

        AaPrdDesAccountRecord term = new AaPrdDesAccountRecord(record);

        try {
//            AaPrdDesAccountRecord term = new AaPrdDesAccountRecord(contract.getConditionForPropertyEffectiveDate("ACCOUNT", tdate));
            
            rtnValue = term.getLocalRefField("L.PASSBOOK.SERIAL.NO").getValue();
           
            if(!rtnValue.equals("") && rtnValue.contains("-")){
                
                term.getLocalRefField("L.PASSBOOK.SERIAL.NO").setError("Passbook Serial Cant't Have Minus Value");
                
            }
            
            
        } catch (Exception e) {
           
        }

        record.set(term.toStructure());
        return term.getValidationResponse();
    }

}

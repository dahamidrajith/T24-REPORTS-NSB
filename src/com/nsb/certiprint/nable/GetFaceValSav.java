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

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class GetFaceValSav extends RecordLifecycle {
    
    DataAccess da = new DataAccess((T24Context)this);
    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);
    

    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        String rtnValue="";
        DecimalFormat df = new DecimalFormat("0.00");
        Contract cnt = new Contract((T24Context) this);
        cnt.setContractId(data);
        try{
        
        AaPrdDesTermAmountRecord prRec= new AaPrdDesTermAmountRecord(cnt.getConditionForPropertyEffectiveDate("COMMITMENT", tdate));
        String faceValBef = prRec.getLocalRefField("L.FACE.VAL.NSB").getValue();
        
        double outstandingBalIntSub = Double.parseDouble(faceValBef);
        BigDecimal bigDecimal3 = new BigDecimal(outstandingBalIntSub);
        String befrtnValue = df.format(bigDecimal3);
        
        String[] parts = befrtnValue.split("\\.");
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedIntegerPart = decimalFormat.format(Long.parseLong(parts[0]));
        
        rtnValue = "LKR "+formattedIntegerPart + (parts.length > 1 ? "." + parts[1] : "");
        
        
        }catch(Exception e){
            rtnValue=""; 
        }
        
        return rtnValue;
    }
    
    

}

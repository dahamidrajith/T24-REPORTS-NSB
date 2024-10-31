package com.mcbc.nsb.Collateral;

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
import com.temenos.t24.api.records.collateral.CollateralRecord;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class ValidateCollDetailzNable extends RecordLifecycle {
    
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
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
       
        Info("currentRecord="+currentRecord);
        int finalValue=0;
        
        CollateralRecord colRec = new CollateralRecord(currentRecord);
        
        try{
            
            String period = colRec.getLocalRefField("L.WAR.P.NSB").getValue();
            String value = colRec.getLocalRefField("L.VALUE.NSB").getValue();
            String marketValue = colRec.getLocalRefField("L.MRKT.VAL.NSB").getValue();
            
//            MinusValueCheckD obj= new MinusValueCheckD();
//            finalValue=obj.CheckMinusValuez(period, value, marketValue);
//            Info("period="+period);
//            Info("finalValue="+finalValue);
            
            if (!period.equals("") && period.contains("-")) {

                colRec.getLocalRefField("L.WAR.P.NSB").setError("Warranty Peroid Cant't Have Minus Values");
                
            }
     
            if (!value.equals("") && value.contains("-")) {

                colRec.getLocalRefField("L.VALUE.NSB").setError("Value Cant't Have Minus");
                
            }
            
            if (!marketValue.equals("") && marketValue.contains("-")) {

                colRec.getLocalRefField("L.MRKT.VAL.NSB").setError("Market Value Cant't Have Minus");
                
            }
            
            
        }catch(Exception e){
            
           Info("Exception="+e);
        }
        
        
        
        return colRec.getValidationResponse();
    }

}

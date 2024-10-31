package com.nsb.nable.reports.v9;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.temenos.api.LocalRefList;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarraccount.AaArrAccountRecord;




public class VInpPassbook extends RecordLifecycle {
    public static void log(String line) {
        String filePath = "/nsbt24/debug/logNBIM.txt";
        line = String.valueOf(String.valueOf(line)) + "\n";
        try {
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                FileWriter fw = new FileWriter(filePath);
                fw.write("---" + line);
                fw.close();
            } else {
                Files.write(Paths.get(filePath, new String[0]), line.getBytes(), new OpenOption[] { StandardOpenOption.APPEND });
            } 
        } catch (Exception e) {
            e.getStackTrace();
        } 
    }

  String nomVal;
  
  public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord, TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
      log(currentRecordId);
      AaArrAccountRecord accRec = new AaArrAccountRecord(currentRecord);
      log(accRec.toString());
      try{
      }catch(Exception e){
      }
      return accRec.getValidationResponse();
    }
}
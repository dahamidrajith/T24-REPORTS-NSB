package com.nsb.printcertific.nable;

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
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class GetTerm extends RecordLifecycle {
    
    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);
    
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
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        String rtnValue="";
        Contract cnt = new Contract((T24Context) this);
        cnt.setContractId(data);
        
        Info("SEP GET TERM");
        
        try {
            
            //AaPrdDesTermAmountRecord aaPrdDesTermAmountRecord = new AaPrdDesTermAmountRecord
            
          AaArrTermAmountRecord AaAccdet2 = new AaArrTermAmountRecord(cnt.getConditionForPropertyEffectiveDate("TERM.AMOUNT", tdate));
          rtnValue = AaAccdet2.getTerm().toString();
          
          Info(rtnValue);
          
        }catch(Exception e){
            Info("SEP GET TERM EXCEPTION-"+e);
            rtnValue="M";
        }
        
        return rtnValue;
    }
    
    

}

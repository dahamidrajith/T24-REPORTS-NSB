package com.nsb.nable.reports.v9;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class testmaneth11 extends Enquiry {
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

    public String setValue(String value, String currentId, TStructure currentRecord, List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        log("testManeth11 Started");
        String termAmount = "";
        try{
            Contract cnt = new Contract(this);
            cnt.setContractId(value);
            AaPrdDesTermAmountRecord termRecord = new AaPrdDesTermAmountRecord(cnt.getConditionForProperty("COMMITMENT"));
            termAmount = termRecord.getAmount().toString();
        }catch(Exception e){
            log(e.toString());
        }
        return termAmount;
    }
}



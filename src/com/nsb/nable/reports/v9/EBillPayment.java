package com.nsb.nable.reports.v9;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class EBillPayment extends Enquiry {

    private DataAccess da = new DataAccess((T24Context)this);
    String tillId;



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
        log("EBillPayment Started");
        try{
            log(value);
            String[] nameList = new String[4];
            nameList = value.split("_", 4);
            log(nameList[0] + nameList[1] + nameList[2]);
            String name = nameList[1];
            log(name);
            List<String> tellerIds = this.da.selectRecords("", "TELLER.ID","", "WITH USER EQ " + name);
            log(String.valueOf(tellerIds.size()));
            tillId = tellerIds.get(0).toString();
        }catch(Exception e){
            log(e.toString());
            return tillId;
        }
        return tillId;
    }
}

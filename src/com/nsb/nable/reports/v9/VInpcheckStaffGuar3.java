package com.nsb.nable.reports.v9;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class VInpcheckStaffGuar3 extends RecordLifecycle {

    private DataAccess da = new DataAccess((T24Context)this);
    private String cusId = "";
    private String collatRight = "";

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

    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord, TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        log("Start");
        CollateralRecord collRec = new CollateralRecord(currentRecord);
        cusId = collRec.getCustomerId().getValue();
        log(cusId);
        CustomerRecord cusRec = new CustomerRecord(this.da.getRecord("CUSTOMER", cusId));
        if(!cusRec.getTarget().toString().equals("1003")){
            return collRec.getValidationResponse();
        }
        log("cusIddone");
        log(cusRec.getTarget().toString());
        try {
            LocalRefList testForLoop = collRec.getLocalRefGroups("L.P.G.CIF.1.NSB");
            log("loop start");
            for (LocalRefGroup personalGuarantorCifGrp : testForLoop) {
                String personalGuarantorCif = personalGuarantorCifGrp.getLocalRefField("L.P.G.CIF.1.NSB").getValue();
                log("personalGuarantorCif : " + personalGuarantorCif );
                CustomerRecord guarRec = new CustomerRecord(this.da.getRecord("CUSTOMER", personalGuarantorCif));
                log(guarRec.getTarget().toString());
                if (!guarRec.getTarget().getValue().equals("1003")){
                    log("found non staff guarantor");
                    personalGuarantorCifGrp.getLocalRefField("L.P.G.CIF.1.NSB").setError("Guarantor is not staff");
                }
            }
        } catch (Exception exception) {}
        currentRecord.set(collRec.toStructure());
        return collRec.getValidationResponse();
    }

}

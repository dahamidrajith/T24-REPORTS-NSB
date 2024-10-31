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
import com.temenos.t24.api.records.acchargerequest.AcChargeRequestRecord;
import com.temenos.t24.api.records.acchargerequest.ChargeCodeClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;
/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class multivalueFixSC extends Enquiry {
    private DataAccess da = new DataAccess((T24Context)this);
    private String output = "";
    private String Date = "";
    private String Debit = "";
    private String Amount = "";
    private String ChargeCode = "";
    private String Cif = "";
    private String Paymentdetails = "";

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

    @Override
    public String setValue(String value, String currentId, TStructure currentRecord,
            List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        log(value);
        try{
        String[] s1 = new String[2];
        s1 = value.split("\\*");
        AcChargeRequestRecord chargeRecord = new AcChargeRequestRecord(da.getRecord("AC.CHARGE.REQUEST", s1[0]));
        List<ChargeCodeClass> codeList = chargeRecord.getChargeCode();
        Cif = chargeRecord.getCustomerNo().toString();
        Debit = chargeRecord.getDebitAccount().toString();
        Paymentdetails = chargeRecord.getExtraDetails(0).toString();
        Date = chargeRecord.getChargeDate().toString();
        for(ChargeCodeClass code:codeList){
            if (code.getChargeCode().toString().startsWith(s1[1])){
                Amount = code.getChargeAmount().toString();
                ChargeCode = code.getChargeCode().toString();
            }
        }
        output = Debit.concat("*").concat(Date).concat("*").concat(Amount).concat("*")
                .concat(ChargeCode).concat("*").concat(Paymentdetails).concat("*").concat(Cif);
        log(output);
        }catch (Exception e){
            log(e.toString());
        }
        return output;
    }
}



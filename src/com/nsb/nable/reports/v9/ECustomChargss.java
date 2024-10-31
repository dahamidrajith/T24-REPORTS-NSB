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
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddesaccounting.AaPrdDesAccountingRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.eblosnewloanscreationnsb.EbLosNewLoansCreationNsbRecord;
import com.temenos.tafj.api.client.impl.T24Context;

/*
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class ECustomChargss extends Enquiry {

    private DataAccess da = new DataAccess((T24Context)this);
    private AaArrangementRecord arrangementRecord;


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
        arrangementRecord = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", currentId));
        log(arrangementRecord.toString());
        Contract cnt = new Contract(this);
        cnt.setContractId(currentId);
        try{
            String prop = cnt.getPropertyIdsForPropertyClass("ACCOUNT").get(0);
            AaPrdDesAccountRecord aaPrdDesAccountRecord = new AaPrdDesAccountRecord(cnt.getConditionForProperty(prop));
            log("Account");
            log(aaPrdDesAccountRecord.toString());
            log(cnt.getPropertyIdsForPropertyClass("ACCOUNT").toString());
        }catch(Exception e){
        }
        try{
            String prop = cnt.getPropertyIdsForPropertyClass("CHARGEOFF").get(0);
            AaPrdDesAccountRecord aaPrdDesAccountRecord = new AaPrdDesAccountRecord(cnt.getConditionForProperty(prop));
            log("Account");
            log(aaPrdDesAccountRecord.toString());
            log(cnt.getPropertyIdsForPropertyClass("ACCOUNT").toString());
        }catch(Exception e){
        }

        try{
            String prop = cnt.getPropertyIdsForPropertyClass("ACCOUNTING").get(0);
            AaPrdDesAccountingRecord aaPrdDesAccountRecord = new AaPrdDesAccountingRecord(cnt.getConditionForProperty(prop));
            log("Accounting");
            log(aaPrdDesAccountRecord.toString());
            log(cnt.getPropertyIdsForPropertyClass("ACCOUNT").toString());
            for(int i = 0;i<cnt.getPropertyIdsForPropertyClass("ACCOUNTING").size();i++){
                AaPrdDesAccountingRecord aaPrdDesAccountRecord1 = new AaPrdDesAccountingRecord(cnt.getConditionForProperty(cnt.getPropertyIdsForPropertyClass("ACCOUNTING").get(i)));
                log("Accounting");
                log(aaPrdDesAccountRecord1.toString());
            }
        }catch(Exception e){
        }
        try{
            String prop = cnt.getPropertyIdsForPropertyClass("TERM.AMOUNT").get(0);
            AaPrdDesTermAmountRecord aaPrdDesTermAmountRecord = new AaPrdDesTermAmountRecord(cnt.getConditionForProperty(prop));
            log("TERM AMOUNT");
            log(aaPrdDesTermAmountRecord.toString());
        }catch(Exception e){
        }
        try{
            String prop = cnt.getContract().toString();
            log("Contract");
            log(prop);
        }catch(Exception e){
        }
        String charge = "";
        String[] s1 =  new String[2];
        s1[0] = "";
        s1[1] = "";
        s1 = value.split("\\*",2);
        log("Id = " + s1[0] + "ChargeType" + s1[1]);
        try{
            List<com.temenos.t24.api.tables.eblosnewloanscreationnsb.ChargeTypeClass> chargeType = null;     
            log("OBJECT CREATION");
            EbLosNewLoansCreationNsbRecord losRec = new EbLosNewLoansCreationNsbRecord(this.da.getRecord("EB.LOS.NEW.LOANS.CREATION.NSB", s1[0]));
            log(losRec.toString());
            log("OBJECT END");
            log("Charge Type");  
            chargeType = losRec.getChargeType();
            log(chargeType.toString());
            log("Charge Type End");
            try{
                for (com.temenos.t24.api.tables.eblosnewloanscreationnsb.ChargeTypeClass chargeTypeClass : chargeType) {
                    if(chargeTypeClass.getChargeType().getValue().toString().equals(s1[1])){
                        log("CRIBCHG FOUND : " + s1[1]);
                        charge = chargeTypeClass.getChargeAmount().getValue().toString();
                    }
                }
            }catch(Exception e){
                log(e.toString());
            }
            log(charge);
        }catch(Exception e){
            log(e.toString());
            return "";
        }
        return charge;
    }
}
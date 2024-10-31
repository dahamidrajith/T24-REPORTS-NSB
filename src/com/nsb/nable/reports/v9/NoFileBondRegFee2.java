package com.nsb.nable.reports.v9;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.pwactivitytxn.PwActivityTxnRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.eblosnewloanscreationnsb.ChargeTypeClass;
import com.temenos.t24.api.tables.eblosnewloanscreationnsb.EbLosNewLoansCreationNsbRecord;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class NoFileBondRegFee2 extends Enquiry {

    private DataAccess da = new DataAccess((T24Context)this);

    private List<String> output = new ArrayList<>();

    private List<String> processActivityIds = new ArrayList<>();

    private String productGroup = "''";

    private String product = "''";

    private String branch = "''";

    private String date = "''";

    private String customer = "''";

    private String dateOperand = "''";

    private String Losid = "''";

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

    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        log("Start Bon Reg Fee");
        setFilterCriteria(filterCriteria);
        processActivityIds = this.da.selectRecords("", "PW.ACTIVITY.TXN", "","");
        for(String activityId:processActivityIds){
            log("PA ID : " + activityId);
            PwActivityTxnRecord activityRecord;
            List<ChargeTypeClass> chargeType;
            String date1;
            try{
                activityRecord = new PwActivityTxnRecord(this.da.getRecord("PW.ACTIVITY.TXN", activityId));
                String activityMapping = activityRecord.getMappingComp().toString();
                String activity = activityRecord.getActivity().toString();
                if(!activityRecord.getTransactionRef().toString().equals(Losid) && !this.Losid.equals("''")){
                    continue;
                }
                if (!(activity.equals("HL.LOS.AUTHORISE.CHARGE_V4")  || 
                        activity.equals("VL.LOS.AUTHORISE.CHARGE_V4")) ||
                        !(activityMapping.equals("Y")))
                {
                    log("mapping or product doesn't match");
                    log(activity + " : " + activityRecord.getMappingComp().toString());
                    continue;
                }
                date1 = activityRecord.getCompletionDate().toString();
                if (!checkDate(date1)){
                    continue;
                }
            }catch(Exception e){
                log(e.toString());
                continue;
            }
            try{
                //log("LOS Record");
                EbLosNewLoansCreationNsbRecord losRec = new EbLosNewLoansCreationNsbRecord(this.da.getRecord("EB.LOS.NEW.LOANS.CREATION.NSB", activityRecord.getTransactionRef().toString()));
                //log(losRec.toString());
                if(!losRec.getCoCode().equals(this.branch) && !this.branch.equals("''")){
                    log(losRec.getCoCode() + " != " + this.branch);
                    continue; 
                }
                if(!losRec.getMainCifNumber().toString().equals(this.customer) && !this.customer.equals("''")){
                    log(losRec.getMainCifNumber().toString() + " != " + this.customer);
                    continue;
                }
                if(!losRec.getLoanGroup().toString().equals(this.productGroup) && !this.productGroup.equals("''")){
                    log(losRec.getLoanGroup().toString() + " != " + this.productGroup);
                    continue;
                }
                List<com.temenos.t24.api.tables.eblosnewloanscreationnsb.LoanProductClass> loanProduct = losRec.getLoanProduct();
                boolean groupFlag = false;
                for (int j=0;j<loanProduct.size();j++){
                    if(loanProduct.get(j).toString().equals(this.product) && !this.product.equals("''")){
                        groupFlag = true;
                    }
                }
                if(!groupFlag && !this.product.equals("''")){
                    log(" != " + this.product);
                    continue;
                }
                //log("FilterCriteriaDone");

                chargeType = losRec.getChargeType();
                //log("CHARGETYPE");
                log(chargeType.toString());
            }catch(Exception e){
                continue;
            }
            try{
                for (ChargeTypeClass chargeTypeClass : chargeType) {
                    String charge  = "";
                    String chargeName = chargeTypeClass.getChargeType().getValue().toString();
                    String chargeDebitAccount = chargeTypeClass.getChargeDebitAcct().toString();
                    if(chargeName.equals("LOMORTREG")){
                        charge = chargeTypeClass.getChargeAmount().getValue().toString();
                        log("Charge " + charge);
                        try{
                            if(!charge.equals("") && Double.valueOf(charge)>0.00){
                                buildList(activityRecord.getTransactionRef().toString(), chargeName, charge, date1, chargeDebitAccount );   
                            }
                        }catch(Exception e){
                            log(e.toString());
                            continue;
                        }
                    }
                }
            }catch(Exception e){
                log(e.toString());
                continue;
            }
        }
        return output;
    }
    /*      setFilterCriteria(filterCriteria);
        String charge = "";
        try{
            List<String> losIds = this.da.selectRecords("", "EB.LOS.NEW.LOANS.CREATION.NSB", "","");
            for(String losId:losIds){
                EbLosNewLoansCreationNsbRecord losRec = new EbLosNewLoansCreationNsbRecord(this.da.getRecord("EB.LOS.NEW.LOANS.CREATION.NSB", losId));
                if(!losRec.getCoCode().equals(this.branch) && !this.branch.equals("''")){
                    continue;
                }
                if(!losRec.getMainCifNumber().toString().equals(this.customer) && !this.customer.equals("''")){
                    continue;
                }
                if(!losRec.getLoanGroup().toString().equals(this.productGroup) && !this.productGroup.equals("''")){
                    continue;
                }
                List<com.temenos.t24.api.tables.eblosnewloanscreationnsb.LoanProductClass> loanProduct = losRec.getLoanProduct();
                boolean groupFlag = false;
                for (int j=0;j<loanProduct.size();j++){
                    if(loanProduct.get(j).toString().equals(this.product) && !this.product.equals("''")){
                        groupFlag = true;
                    }
                }
                if(!groupFlag && !this.product.equals("''")){
                    continue;
                }
                if(!losRec.getDateTime(0).toString().equals(this.date) && !this.date.equals("''")){
                    continue;
                }
                List<ChargeTypeClass> chargeType = losRec.getChargeType();

                log("FilterCriteriaDone");
                log("OBJECT");
                log(losRec.toString());
                log("CHARGETYPE");
                log(chargeType.toString());
                try{
                for (ChargeTypeClass chargeTypeClass : chargeType) {
                    String chargeName = chargeTypeClass.getChargeType().getValue().toString();
                    if(chargeName.equals("LOMORTREG") ){

                        log("FOUND");
                        charge = chargeTypeClass.getChargeAmount().getValue().toString();
                    }
                }
                }catch(Exception e){
                    continue;
                 }
                try{
                    if(!charge.equals("") && Integer.valueOf(charge)>0){
                        output.add(losId+ "*" +charge);
                    }
                }catch(Exception e){
                    log(e.toString());
                    if(!charge.equals("")){
                        output.add(losId+ "*" +charge);
                    }
                }
            }
        }catch(Exception e){
            log(e.toString());
            empty_List.add("" + "*" + "");
            return empty_List;
        }
        return output;
    }*/

    private void setFilterCriteria(List<FilterCriteria> filtercriteria) {
        for (FilterCriteria fieldNames : filtercriteria) {

            String FieldName = fieldNames.getFieldname();

            if (FieldName.equals("PRODUCT.GROUP"))
                this.productGroup = fieldNames.getValue();

            if (FieldName.equals("PRODUCT"))
                this.product = fieldNames.getValue();

            if (FieldName.equals("BRANCH"))
                this.branch = fieldNames.getValue();

            if (FieldName.equals("LOS"))
                this.Losid  = fieldNames.getValue();

            if (FieldName.equals("DATE") && !fieldNames.getValue().equals("''")){
                this.date = fieldNames.getValue();
                this.dateOperand = fieldNames.getOperand();
            }

            if (FieldName.equals("CUSTOMER"))
                this.customer = fieldNames.getValue();
        }
    }

    private boolean checkDate(String date1) {
        if (this.dateOperand.equals("''")){
            return true;
        }
        if (this.date.equals("''")){
            return false;
        }
        try {
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMdd");
            Date date;
            date = sdformat.parse(date1);
            Date inputDate = sdformat.parse(this.date);
            if(this.dateOperand.equals("5")) {
                return date.compareTo(inputDate) == 0;
            }
            if(this.dateOperand.equals("9")) {
                return date.compareTo(inputDate) >= 0;
            }
            if(this.dateOperand.equals("8")) {
                return date.compareTo(inputDate) <= 0;
            }
            if(this.dateOperand.equals("3")) {
                return date.compareTo(inputDate) < 0;
            }
            if(this.dateOperand.equals("4")) {
                return date.compareTo(inputDate) > 0;
            }
            if(this.dateOperand.equals("2")) {
                Date startDate = sdformat.parse(this.date.substring(0,8));
                Date endDate = sdformat.parse(this.date.substring(9,17));
                return ((date.compareTo(startDate)) > 0 && (date.compareTo(endDate) <0));
            }

        } catch (Exception e) {
            log(e.toString());
            return false;
        }
        return false;
    }

    private void buildList(String losId,  String chargeName, String charge, String date, String debitAccount) {
        String result = "";
        try {
            StringBuilder str = new StringBuilder();
            str.append(losId);
            str.append("*" + chargeName);
            str.append("*" + charge);
            str.append("*" + date);
            str.append("*" + debitAccount);
            result = str.toString();
            output.add(result);
        }catch (Exception e) {
            output.add("" + "*" + "" + "*" + "" + "*" + "" + "*" + "");
        } 
    }

}



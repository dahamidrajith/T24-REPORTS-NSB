package com.nsb.nable.reports;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.records.limit.LimitRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class EConDlyDisb extends Enquiry {

    DataAccess da = new DataAccess((T24Context) this);

//    public static void Info(String line) {
//
//      //  String filePath = "D:\\test.txt";
//        String filePath = "/nsbt24/debug/log.txt";
//        line = String.valueOf(line) + "\n";
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        try {
//            File myObj = new File(filePath);
//            if (myObj.createNewFile()) {
//                FileWriter fw = new FileWriter(filePath);
//                fw.write("---" + line);
//                fw.close();
//            } else {
//                Files.write(Paths.get(filePath, new String[0]), line.getBytes(),
//                        new OpenOption[] { StandardOpenOption.APPEND });
//            }
//        } catch (Exception e) {
//            e.getStackTrace();
//        }
//    }

    @Override
    public String setValue(String value, String currentId, TStructure currentRecord,
            List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
       
        String appendDate = "";
        String applicationId = "";
        String loanIntrestList = "";
        String loanIntrestValue = "";
        String approvedAmountValue = "";
        
//        String string = value;
//        String[] parts = string.split("/");
//        String arrId = parts[0];
//        String loanNo = parts[1];

        Contract cnt = new Contract((T24Context) this);
        cnt.setContractId(value);
        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);

        try {
            
            AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", value));
            String loanNoArr= AaArr.getLinkedAppl().toString();
         
            
            JSONArray jsonArrayUp = new JSONArray(loanNoArr);
            for(int k=0; k<jsonArrayUp.length();k++){
                
            JSONObject explrObjectUp = jsonArrayUp.getJSONObject(k);
            String accNo = explrObjectUp.get("linkedApplId").toString();
            
            AccountRecord acont= new AccountRecord(this.da.getRecord("ACCOUNT", accNo));
            String limitKey= acont.getLimitKey().toString();
            
            if(!limitKey.equals("") || !limitKey.equals(null)){
                
              
                LimitRecord collRightRec = new LimitRecord(this.da.getRecord("LIMIT", limitKey));
                String collatRight= collRightRec.getCollatRightTop().toString();
             
                
                JSONArray jsonArrayOne = new JSONArray(collatRight);
             for(int i=0; i<jsonArrayOne.length(); i++){
                 
                 JSONObject explrObjectOne = jsonArrayOne.getJSONObject(i);
                String collatRightJson = explrObjectOne.get("CollatRight").toString();
                
                JSONArray jsonArrayTwo = new JSONArray(collatRightJson);
                 for(int j=0; j<jsonArrayTwo.length(); j++){
                     JSONObject explrObjectTwo = jsonArrayTwo.getJSONObject(j);
                     String collatRightValue = explrObjectTwo.get("collatRight").toString();
                     
                     CollateralRightRecord colRytRec = new CollateralRightRecord(this.da.getRecord("COLLATERAL.RIGHT", collatRightValue));
                     String colID=colRytRec.getCollateralId().toString();
                  
                     
                     
                 }
             }
                
            }
        }

//            List<String> recarr = this.da.selectRecords("", "COLLATERAL", "", "WITH APPLICATION.ID EQ " + part2);
//            int recarrSize = recarr.size();
//
//            if (recarrSize > 0) {
//
//                CollateralRecord CollateralRec = new CollateralRecord(this.da.getRecord("COLLATERAL", recarr.get(0)));
//                applicationId = CollateralRec.getApplicationId().toString();
//
//            }

        } catch (Exception e) {
            applicationId = "";
        }

        try {

            AaPrdDesInterestRecord aaintRecord = new AaPrdDesInterestRecord(
                    cnt.getConditionForPropertyEffectiveDate("PRINCIPALINT", tdate));
            List<FixedRateClass> Fxrate = aaintRecord.getFixedRate();

            if (Fxrate.size() > 0) {

                JSONArray jsonArrayTwo = new JSONArray(Fxrate);
                JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
                loanIntrestList = explrObjectFour.get("effectiveRate").toString();
                JSONObject explrObjectFive = new JSONObject(loanIntrestList);
                loanIntrestValue = explrObjectFive.get("value").toString();

            }

        } catch (Exception e) {
            loanIntrestValue = "";
        }

        try {

            String prop = cnt.getPropertyIdsForPropertyClass("TERM.AMOUNT").get(0);
            AaPrdDesTermAmountRecord aaPrdDesTermAmountRecord = new AaPrdDesTermAmountRecord(
                    cnt.getConditionForProperty(prop));
            approvedAmountValue = aaPrdDesTermAmountRecord.getAmount().toString();

        } catch (Exception e) {
            approvedAmountValue = "";
        }

        appendDate = buildList(applicationId, loanIntrestValue, approvedAmountValue);

        return appendDate;
    }

    private String buildList(String applicationIdValFin, String loanIntrestValueFin, String approvedAmountValueFin) {

        String Result = "";

        try {

            StringBuilder str = new StringBuilder();
            str.append(applicationIdValFin);
            str.append("*" + loanIntrestValueFin);
            str.append("*" + approvedAmountValueFin);

            Result = str.toString();

        } catch (Exception e) {
            Result = "" + "*" + "" + "*" + "";
        }
        return Result;
    }

}

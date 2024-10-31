package com.nsb.nable.reports.v9;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.OutstandingBalances;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;
import com.temenos.api.TDate;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * TODO: Document me!
 *
 * @author maneth
 *
 */
public class EEsyCash2 extends Enquiry {

    private DataAccess da = new DataAccess((T24Context)this);
    private List<String> output = new ArrayList<>();
    

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
    public List<String> setValues(String value, String currentId, TStructure currentRecord,
           List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        String[] s1 =  new String[3];
        s1[0] = "";
        s1[1] = "";
        s1[2] = "";
        String[] s2;
        s1 = value.split("\\*",3);
        String arrangementId = s1[0];
        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);
        String loanIntrestList = "";
        String loanIntrestValue = "";
        String outstandingBal = "";
        String exceeded = "";
        String collateralId = "";
        if (!arrangementId.equals("") || !arrangementId.equals(null)) {
            Contract cnt = new Contract((T24Context) this);
            cnt.setContractId(arrangementId);
            
            try {
                log(cnt.getPropertyIds().toString());
                log(cnt.getAccountDetailsRecord().toString());
                AaPrdDesInterestRecord aaintRecord = new AaPrdDesInterestRecord(
                        cnt.getConditionForPropertyEffectiveDate("DRINTEREST", tdate));
                log(aaintRecord.toString());
                List<FixedRateClass> Fxrate = aaintRecord.getFixedRate();
                log(Fxrate.toString());
                if (Fxrate.size() > 0) {
                    JSONArray jsonArrayTwo = new JSONArray(Fxrate);
                    JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
                    loanIntrestList = explrObjectFour.get("effectiveRate").toString();
                    JSONObject explrObjectFive = new JSONObject(loanIntrestList);
                    loanIntrestValue = explrObjectFive.get("value").toString();
                }
            }catch (Exception e) {
                loanIntrestValue = "";
            }
            try {
                OutstandingBalances outstandingBalList = cnt.getOutstandingBalance();
                outstandingBal = outstandingBalList.getAccountBalance().toString();
            } catch (Exception e) {
                outstandingBal = "";
            }
            try {
                Double total = Double.valueOf(outstandingBal) + Double.valueOf(s1[3]);
                exceeded = String.valueOf(total);
            }catch (Exception e) {
                exceeded = "";
            }
        }

        try{
            try{
                //Assigning Collateral Right IDs to List
                String collateralRightRecords = s1[1];
                s2 = collateralRightRecords.split(" ");
            }catch(Exception e){
                s2 = null;
            }
            if(s2.length>0 && s2 != null){
                //Iterating through collateral right records
                for (int i = 0; i < s2.length; i++) {
                    if (!s2[i].equals("") && !s2[i].isEmpty() && s2[i] != null){
                        CollateralRightRecord collateralRightRecord;
                        try{
                            collateralRightRecord = new CollateralRightRecord(this.da.getRecord("COLLATERAL.RIGHT", s2[i]));
                            //Iterating through collateral Records
                        }catch(Exception e){
                            continue;
                        }
                        try{
                            if (collateralRightRecord != null){
                                for(int j =0; j<collateralRightRecord.getCollateralId().size();j++){
                                    collateralId = collateralRightRecord.getCollateralId().get(j).toString();
                                    buildList(loanIntrestValue,outstandingBal,collateralId ,exceeded);
                                }
                            }
                        }catch (Exception e) {
                            buildList(loanIntrestValue,outstandingBal,"",exceeded);
                        }
                    }
                } 
            }
        }catch (Exception e) {
            buildList(loanIntrestValue,outstandingBal,"",exceeded);
        }
        return output;
    }

    private void buildList(String loanIntrestValueFin, String outstandingBalFin, String collateral,String exceeded) {
        String result = "";
        try{
            StringBuilder str = new StringBuilder();
            str.append(loanIntrestValueFin);
            str.append("*" + outstandingBalFin);
            str.append("*" + collateral);
            str.append("*" + exceeded);
            result = str.toString();
        }catch (Exception e) {
            result = "" + "*" + "" + "*" + "" + "*" + "";
        }
        output.add(result);
    }
}
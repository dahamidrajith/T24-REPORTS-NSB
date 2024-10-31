
package com.nsb.nable.reports.v9;

import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;

import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.records.currency.CurrencyRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;
import java.util.ArrayList;
import java.util.List;

public class EConLisColtrLad20 extends Enquiry {
    private DataAccess da = new DataAccess((T24Context)this);
    private List<String> output = new ArrayList<>();
    private String collateralRightRecords = "";
    private String collateralCurrency;
    private Double totalNominalValue = new Double(0.0);
    //private Double exceededAmount = new Double(0.0);
    private Double nominalValue = new Double(0.0);
    private Double exceeded = new Double(0.0);
    private CollateralRightRecord collateralRightRecord;
    private String grantedAmountValue = "";
    private String grantedDateValue = "";
    private String totOutstanding = "";
    private String firstcoll = "";
    private String firstnom = "";
    private double totOutstanding1 = new Double(0.00);
    

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
    public List<String> setValues(String value, String currentId, TStructure currentRecord, List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        NumberFormat nf= NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setMinimumFractionDigits(2);
        nf.setGroupingUsed(false);
        String[] s1 = new String[4];
        s1[0] = "";
        s1[1] = "";
        s1[2] = "";
        s1[3] = "";
        String[] s2;
        try{
            s1 = value.split("\\*",4);
            log(s1[0]);
            log(s1[1]);
            log(s1[2]);
            log(s1[3]);
            //Getting Outstanding Balance
            if(currentId != null && !currentId.equals("")){
                Contract cnt = new Contract(this);
                cnt.setContractId(currentId);
                String prop = cnt.getPropertyIdsForPropertyClass("TERM.AMOUNT").get(0);
                AaPrdDesTermAmountRecord aaPrdDesTermAmountRecord = new AaPrdDesTermAmountRecord(cnt.getConditionForProperty(prop));
                grantedAmountValue  = aaPrdDesTermAmountRecord.getAmount().toString();
            }
            try{
                AaAccountDetailsRecord AaAccdet = new AaAccountDetailsRecord(this.da.getRecord("AA.ACCOUNT.DETAILS", currentId));
                grantedDateValue = AaAccdet.getContractDate().get(0).toString();
            }catch(Exception e){
                grantedDateValue = "";
            }
        }catch(Exception e){
            log(e.toString());
        }
        try{
            //Assigning Collateral Right IDs to List
            collateralRightRecords = s1[1];
            s2 = collateralRightRecords.split(" ");
        }catch(Exception e){
            log("assign");
            log(e.toString());
            s2 = null;
        }
        try{
        if(s2.length>0 && s2 != null){
            //Iterating through collateral right records
            for (int i = 0; i < s2.length; i++) {
                if (!s2[i].equals("") && !s2[i].isEmpty() && s2[i] != null){
                    try{
                        collateralRightRecord = new CollateralRightRecord(this.da.getRecord("COLLATERAL.RIGHT", s2[i]));
                        //Iterating through collateral Records
                    }catch(Exception e){
                        log("collright");
                        log(e.toString());
                        collateralRightRecord = null;
                    }
                    try{
                        if (collateralRightRecord != null){
                            for(int j =0; j<collateralRightRecord.getCollateralId().size();j++){
                                String collateralId = collateralRightRecord.getCollateralId().get(j).toString();
                                CollateralRecord collateralRecord = new CollateralRecord(this.da.getRecord("COLLATERAL", collateralId));
                                collateralCurrency = collateralRecord.getCurrency().toString();
                                nominalValue = Double.valueOf(collateralRecord.getNominalValue().toString());
                                if(!collateralCurrency.equals("LKR")){
                                    try{
                                        CurrencyRecord currRec = new CurrencyRecord(this.da.getRecord("CURRENCY", collateralCurrency));
                                        Double buyRate = Double.valueOf(currRec.getCurrencyMarket().get(0).getBuyRate().toString());
                                        nominalValue = nominalValue * buyRate;
                                    }catch(Exception e){
                                        log(e.toString());
                                        nominalValue = Double.valueOf(collateralRecord.getNominalValue().toString());
                                    }
                                }
                                totalNominalValue += Double.valueOf(nominalValue);
                                if(output.size() != 0){
                                    buildList("",nf.format(nominalValue),collateralId, "","","","","","");
                                }
                                else{
                                    firstcoll = collateralId;
                                    firstnom  = nf.format(nominalValue);
                                    buildList("","","","","","","","","");
                                }
                            }
                        }
                    }catch(Exception e){
                        log("collLOOP");
                        log(e.toString());
                        continue;
                    }
                }
            }
        }
        }catch(Exception e){
            log("output");
            log(e.toString());
        }
        try{
            totOutstanding = s1[2];
            totOutstanding1 = Double.valueOf(totOutstanding);
            if(!s1[3].equals("LKR")){
                try{
                    CurrencyRecord currRec = new CurrencyRecord(this.da.getRecord("CURRENCY", s1[3]));
                    Double buyRate = Double.valueOf(currRec.getCurrencyMarket().get(0).getBuyRate().toString());
                    totOutstanding1  = Double.valueOf(totOutstanding) * buyRate;
                }catch(Exception e){
                    log(e.toString());
                    log("currency outstanding conversion error");
                }
            }
            exceeded = totalNominalValue + totOutstanding1;
            String percent = "";
            String flag = "0";
            BigDecimal bd = new BigDecimal(exceeded);
            bd.setScale(2, RoundingMode.HALF_UP);
            if(output.size() == 0){
                buildList("","","","","","","","","");
            }
            try{
               log("percentStart");
               Double percent1 =  (totOutstanding1/totalNominalValue) * 100;
               log(percent1.toString());
               percent = nf.format(percent1);
               if(totalNominalValue <= 0.00 || totOutstanding.equals("0")){
                   flag = "Exceeded";
                   percent = "";
               }
               else if(percent1> 100.00){
                   flag = "Exceeded";
               }
               else if(percent1< 100.00){
                   flag = "Available";
               }
               else{
                   flag = "";
               }
               log("percent: " + percent);
               
            }catch(Exception e){
                flag = "0";
                percent = "";
                log("percent");
                log(e.toString());
            }
            buildListfirst(nf.format(totalNominalValue),"","", nf.format(exceeded), grantedDateValue, grantedAmountValue, percent, flag , nf.format(totalNominalValue));
        }catch(Exception e){
            log("calc");
            log(e.toString());
            buildListfirst("","","","","","","","","");
        }
        try{
        for(String a:output){
            log(a);
        }
        }catch(Exception e){
            log("output");
            log(e.toString());
        }
        return output;
    }

    private void buildList(String totalNominalValue, String nominalValue, String collateralId, String exceededAmount, String grantedAmount, String grantedDate, String percent, String flag, String Nom2) {
        String result = "";
        try {
            StringBuilder str = new StringBuilder();
            str.append(totalNominalValue);
            str.append("*" + nominalValue);
            str.append("*" + collateralId);
            str.append("*" + exceededAmount);
            str.append("*" + grantedAmount);
            str.append("*" + grantedDate);
            str.append("*" + percent);
            str.append("*" + flag);
            str.append("*" + Nom2);
            result = str.toString();
            output.add(result);
        }catch (Exception e) {
            output.add(""+"*"+""+"*"+""+"*"+""+"*"+""+"*"+"");
        } 
    }

    private void buildListfirst(String totalNominalValue, String nominalValue, String collateralId, String exceededAmount, String grantedAmount, String grantedDate, String percent, String flag, String Nom2) {
        String result = "";
        try {
            StringBuilder str = new StringBuilder();
            str.append(totalNominalValue);
            str.append("*" + firstnom);
            str.append("*" + firstcoll);
            str.append("*" + exceededAmount);
            str.append("*" + grantedAmount);
            str.append("*" + grantedDate);
            str.append("*" + percent);
            str.append("*" + flag);
            str.append("*" + Nom2);
            result = str.toString();
            output.set(0, result);
        }catch (Exception e) {
            output.set(0, ""+"*"+""+"*"+""+"*"+""+"*"+""+"*"+"");
        } 
    }
}


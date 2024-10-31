package com.nsb.nable.reports.m;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.api.TDate;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrpaymentschedule.AaArrPaymentScheduleRecord;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aaprddeslimit.AaPrdDesLimitRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.ebcontractbalances.EbContractBalancesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;
import com.temenos.t24.api.records.aaarrpaymentschedule.PaymentTypeClass;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class EConLnPendingDisbsModzaz3 extends Enquiry {
    
    DataAccess da = new DataAccess((T24Context) this);
    
   public static void Info(String line) {

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
    public String setValue(String value, String currentId, TStructure currentRecord,
            List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        
        
        String loanIntrestList = "";
        String loanIntrestValue = "";
        String grantedAmountValue = "";
        String limitId = "";
        String approvedAmount="";

        String appendDate = "";
        String loanIntrestValueFinn = "";
        String noOfInstallmentDisbuserd="";
        
        String AvailableAccountBalance = "";
        String TotalCommitmentAmount = "";
        String grantedDateValue="";
        String tenureValue="";
        String ilstallment="";
        String CrrntCommitmentAmount = "";
        
        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);

        DecimalFormat df = new DecimalFormat("0.00");

        try {

            if (!value.equals("") || !value.equals(null)) {

                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(value);

                try {

                    AaPrdDesInterestRecord aaintRecord = new AaPrdDesInterestRecord(
                            cnt.getConditionForPropertyEffectiveDate("PRINCIPALINT", tdate));
                    List<FixedRateClass> Fxrate = aaintRecord.getFixedRate();

                    StringBuilder sb = new StringBuilder();

                  for (int i=0;i<Fxrate.size();i++) {

                        JSONArray jsonArrayTwo = new JSONArray(Fxrate);
                        JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(i);
                        loanIntrestList = explrObjectFour.get("effectiveRate").toString();
                        JSONObject explrObjectFive = new JSONObject(loanIntrestList);
                        loanIntrestValue = explrObjectFive.get("value").toString();
                        
                        String loanIntrestValueFinnBef="";
                     
                        if (!loanIntrestValue.equals("")) {
                            
                            double loanIntrestValueFin = Double.parseDouble(loanIntrestValue);
                            loanIntrestValueFinnBef = df.format(loanIntrestValueFin).toString();
                        }
                        
                        if(i==0){
                            sb.append(loanIntrestValueFinnBef);
                        }else{
                            sb.append("|"+loanIntrestValueFinnBef);
                        }
                       
                    }
                  
                  loanIntrestValueFinn=sb.toString();
                  
                } catch (Exception e) {
                    loanIntrestValueFinn = "";
                }

                try{
                    
                    double TotalCommitmentAmountIntSub = 0.00;
                    double AvailableAccountBalanceIntSub = 0.00;
                    double CurCommitBalanceIntSub = 0.00;
                    
                    List<BalanceMovement> AvailableAccountBalanceList = null;
                    List<BalanceMovement> TotalCommitmentAmountList = null;
                    List<BalanceMovement> CurrntComitBalanceList = null;

                    try {

                        AvailableAccountBalanceList = cnt.getContractBalanceMovements("AVLACCOUNT", "");
                        for (BalanceMovement bl : AvailableAccountBalanceList) {
                            AvailableAccountBalance = bl.getBalance().toString();

                            if (AvailableAccountBalance.contains("-")) {
                                int AvailableAccountBalanceLen = AvailableAccountBalance.length();
                                AvailableAccountBalance = AvailableAccountBalance.substring(1,
                                        AvailableAccountBalanceLen);

                                
                            }

                            

                        }

                        if (!AvailableAccountBalance.equals("")) {

                            AvailableAccountBalanceIntSub = Double.parseDouble(AvailableAccountBalance);
                            
                        }

                    } catch (Exception w) {
                        AvailableAccountBalanceIntSub = 0.00;
                    }
                    
                    try {

                        CurrntComitBalanceList = cnt.getContractBalanceMovements("CURCOMMITMENT", "");
                        for (BalanceMovement b3 : CurrntComitBalanceList) {
                            CrrntCommitmentAmount = b3.getBalance().toString();

                            if (CrrntCommitmentAmount.contains("-")) {
                                int CurnCommitBalanceLen = CrrntCommitmentAmount.length();
                                CrrntCommitmentAmount = CrrntCommitmentAmount.substring(1,
                                        CurnCommitBalanceLen);

                            }

                        }

                        if (!CrrntCommitmentAmount.equals("")) {

                            CurCommitBalanceIntSub = Double.parseDouble(CrrntCommitmentAmount);
                            Info("CurCommitBalanceIntSub="+CurCommitBalanceIntSub);
                        }

                    } catch (Exception w) {
                        CurCommitBalanceIntSub = 0.00;
                    }

                    try {

                        TotalCommitmentAmountList = cnt.getContractBalanceMovements("TOTCOMMITMENT", "");
                        for (BalanceMovement bl : TotalCommitmentAmountList) {
                            TotalCommitmentAmount = bl.getBalance().toString();

                            if (TotalCommitmentAmount.contains("-")) {
                                int TotalCommitmentAmountLen = TotalCommitmentAmount.length();
                                TotalCommitmentAmount = TotalCommitmentAmount.substring(1, TotalCommitmentAmountLen);

                               
                            }

                            

                        }

                        if (!TotalCommitmentAmount.equals("")) {

                            TotalCommitmentAmountIntSub = Double.parseDouble(TotalCommitmentAmount);
                           
                        }

                    } catch (Exception w) {
                        TotalCommitmentAmountIntSub = 0.00;
                    }
                    
                    double grantedAmountValueTemp=0.00;
                    
                    if(AvailableAccountBalanceIntSub>0){
                        grantedAmountValueTemp = (TotalCommitmentAmountIntSub - AvailableAccountBalanceIntSub);
                    }else if(CurCommitBalanceIntSub>0){
                        grantedAmountValueTemp = (TotalCommitmentAmountIntSub - CurCommitBalanceIntSub);
                    }

                    try {


                            BigDecimal bigDecimal2 = new BigDecimal(grantedAmountValueTemp);
                            grantedAmountValue = df.format(bigDecimal2);
                           

                    } catch (Exception e) {
                        grantedAmountValue = "0.00";
                    }
                    
                    try{
                        
                        if (TotalCommitmentAmountIntSub > 0) {

                            BigDecimal bigDecimal5 = new BigDecimal(TotalCommitmentAmountIntSub);
                            approvedAmount = df.format(bigDecimal5);
                            
                        }
                        
                    }catch(Exception e){
                        approvedAmount="0.00";
                    }
                    
                }catch(Exception e){
                    grantedAmountValue="0.0";
                    approvedAmount = "";
                }

                try {

                    AaPrdDesLimitRecord limRec = new AaPrdDesLimitRecord(
                            cnt.getConditionForPropertyEffectiveDate("LIMIT", tdate));
                    limitId = limRec.getLimit().getValue();

                } catch (Exception e) {
                    limitId = "";
                }
                
                try{
                    
                    AaArrPaymentScheduleRecord aaArrDesPaymentScheduleRecord = new AaArrPaymentScheduleRecord(cnt.getConditionForPropertyEffectiveDate("SCHEDULE", tdate));
//                    String paymentType= aaArrDesPaymentScheduleRecord.getPaymentType().toString();
                    Info("aaArrDesPaymentScheduleRecord="+aaArrDesPaymentScheduleRecord);
                    String getCalcAmount= aaArrDesPaymentScheduleRecord.getPaymentType().toString();
                    
                    JSONArray jsonArrayTwo = new JSONArray(getCalcAmount);
                    JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(1);
                    String calcAmountObj = explrObjectFour.get("Percentage").toString();
                    Info("calcAmountObj"+calcAmountObj);
                    JSONArray jsonArrayThree = new JSONArray(calcAmountObj);
                    JSONObject explrObjectFive = jsonArrayThree.getJSONObject(0);
                    ilstallment = explrObjectFive.get("calcAmount").toString();
                    Info("ilstallment"+ilstallment);

                    
                }catch(Exception e){
                    Info("Exception="+e);
                    noOfInstallmentDisbuserd="";
                    ilstallment="";
                }
                
                try {

                    AaAccountDetailsRecord AaAccdet = new AaAccountDetailsRecord(
                            this.da.getRecord("AA.ACCOUNT.DETAILS", value));

                        grantedDateValue = ((TField) AaAccdet.getContractDate().get(0)).toString();
                        Info("grantedDateValue="+grantedDateValue);

                } catch (Exception z) {
                    grantedDateValue = "";
                }
                
                try {

                    AaPrdDesTermAmountRecord aaAccrec = new AaPrdDesTermAmountRecord(
                            cnt.getConditionForPropertyEffectiveDate("COMMITMENT", tdate));
                    tenureValue = aaAccrec.getTerm().toString();
                    Info("tenureValue="+tenureValue);
                } catch (Exception e) {
                 
                    tenureValue = "";
                }

            }
        } catch (Exception e) {
            loanIntrestValueFinn = "";
            approvedAmount = "";
            limitId = "";
            grantedAmountValue="";
            grantedDateValue="";
            tenureValue = "";
        }

        appendDate = buildList(loanIntrestValueFinn, approvedAmount, limitId, grantedAmountValue, noOfInstallmentDisbuserd, grantedDateValue, tenureValue, ilstallment);

        return appendDate;
        
    }
    
    private String buildList(String loanIntrestValueFin, String approvedAmountValueFin, String limitIdValueFin, String grantedAmountValueFinVal, String noOfInstallmentDisbuserd, String grantedDateValueFin, String tenureValueFin, String ilstallmentFin) {

        String Result = "";

        try {
            StringBuilder str = new StringBuilder();
            str.append(loanIntrestValueFin);
            str.append("*" + approvedAmountValueFin);
            str.append("*" + limitIdValueFin);
            str.append("*" + grantedAmountValueFinVal);
            str.append("*" + noOfInstallmentDisbuserd);
            str.append("*" + grantedDateValueFin);
            str.append("*" + tenureValueFin);
            str.append("*" + ilstallmentFin);

            Result = str.toString();
            Info("Result="+Result);
        } catch (Exception e) {
            Result = "" + "*" + "" + "*" + ""+"" + "*" + "" + "*" + ""+"*" + "";
        }
        return Result;

    }
    

}

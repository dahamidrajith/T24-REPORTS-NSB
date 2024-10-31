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
import com.temenos.t24.api.party.Account;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrpaymentschedule.AaArrPaymentScheduleRecord;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aaprddeslimit.AaPrdDesLimitRecord;
import com.temenos.t24.api.records.aaprddessettlement.AaPrdDesSettlementRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.aascheduledactivity.AaScheduledActivityRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.records.limit.LimitRecord;
import com.temenos.t24.api.records.stmtentry.StmtEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.eblosloanorignsb.EbLosLoanOrigNsbRecord;
import com.temenos.t24.api.tables.eblosnewloancreationnsb.EbLosNewLoanCreationNsbRecord;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class EConDisbsTransDetModUp97 extends Enquiry {

    DataAccess da = new DataAccess((T24Context) this);
    

    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);

    DecimalFormat df = new DecimalFormat("0.00");

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

        String limitId = "";
        String grantedDateValue = "";
        String grantedAmountValue = "";
        String loanIntrestList = "";
        String loanIntrestValue = "";
        String tenureValue = "";
        String settlementAccont = "";
        String applicationId = "";
        String loanIntrestValueFinn = "";
        String txnRef = "";
        String nextDueDate = "";
        String AvailableAccountBalance="";
        String txnCode="";
        String TotalCommitmentAmount="";
        String purpose="";
        double totAmountVal = 0;
        String ilstallment="";
        String payAccFina="";
        String CrrntCommitmentAmount = "";
        double CurCommitBalanceIntSub = 0.00;
        String totAmountValN="";

        String appendDate = "";


        try {

                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(value);
                    
                    try {

                        AaAccountDetailsRecord AaAccdet = new AaAccountDetailsRecord(
                                this.da.getRecord("AA.ACCOUNT.DETAILS", value));

                        grantedDateValue = ((TField) AaAccdet.getContractDate().get(0)).toString();
                        

                    } catch (Exception e) {
                       
                        grantedDateValue = "";
                    }
                    
                    double creditAmountValue = 0;
                    
                    
                    try {


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

                                creditAmountValue = Double.parseDouble(AvailableAccountBalance);
                                Info("creditAmountValue="+creditAmountValue);


                        } catch (Exception w) {
                            creditAmountValue = 0.00;
                            Info("ECPPPPPPP="+w);
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


                                CurCommitBalanceIntSub = Double.parseDouble(CrrntCommitmentAmount);
                                Info("CurCommitBalanceIntSub="+CurCommitBalanceIntSub);


                        } catch (Exception w) {
                            CurCommitBalanceIntSub = 0.00;
                            Info("ECPPPPPPPqqqqqqqq="+w);
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

                                totAmountVal = Double.parseDouble(TotalCommitmentAmount);
                                Info("totAmountVal="+totAmountVal);


                        } catch (Exception w) {
                            totAmountVal = 0.00;
                            Info("ECPPPPPPPqqqqqqqqeeeeeee="+w);
                        }
                        
                        double grantedAmountValueBigDec=0.00;
                        
                      if(creditAmountValue>0){
                          grantedAmountValueBigDec = (totAmountVal - creditAmountValue);
                      }else if(CurCommitBalanceIntSub>0){
                          grantedAmountValueBigDec = (totAmountVal - CurCommitBalanceIntSub);
                      }
                      
                      BigDecimal bigDecimal = new BigDecimal(grantedAmountValueBigDec);
                      grantedAmountValue=df.format(bigDecimal);
                      
                      try{
                          
                          BigDecimal bigDecimalN = new BigDecimal(totAmountVal);
                          totAmountValN=df.format(bigDecimalN);
                          Info("totAmountValN="+totAmountValN);
                      }catch(Exception e){
                          totAmountValN="0.00";
                          Info("e="+e);
                      }
                     

                  } catch (Exception e) {
                      
                      grantedAmountValue = "0.00";
                      
                      try{
                          
                          BigDecimal bigDecimalN = new BigDecimal(totAmountVal);
                          totAmountValN=df.format(bigDecimalN);
                          Info("totAmountValNEXCEP="+totAmountValN);
                      }catch(Exception q){
                          totAmountValN="0.00";
                          Info("q="+q);
                      }
                      
                  }
                    
                    try {


                        AaPrdDesInterestRecord aaintRecord = new AaPrdDesInterestRecord(
                                cnt.getConditionForPropertyEffectiveDate("PRINCIPALINT", tdate));

                        List<FixedRateClass> Fxrate = aaintRecord.getFixedRate();

                        StringBuilder sb = new StringBuilder();

                      for (int i=0;i<Fxrate.size();i++) {
                        
                            JSONArray jsonArraySeven = new JSONArray(Fxrate);
                            JSONObject explrObjectSix = jsonArraySeven.getJSONObject(i);
                            loanIntrestList = explrObjectSix.get("effectiveRate").toString();

                            JSONObject explrObjectEight = new JSONObject(loanIntrestList);
                            loanIntrestValue = explrObjectEight.get("value").toString();

                            String loanIntrestValueFinnBef="";
                                double loanIntrestValueFin = Double.parseDouble(loanIntrestValue);
                                loanIntrestValueFinnBef = df.format(loanIntrestValueFin).toString();
                                
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
                    
                    try {

                        AaPrdDesTermAmountRecord aaAccrec = new AaPrdDesTermAmountRecord(
                                cnt.getConditionForPropertyEffectiveDate("COMMITMENT", tdate));
                        tenureValue = aaAccrec.getTerm().toString();

                    } catch (Exception e) {
                     
                        tenureValue = "";
                    }


                    try {

                        AaScheduledActivityRecord sheduleActiy = new AaScheduledActivityRecord(
                                this.da.getRecord("AA.SCHEDULED.ACTIVITY", value));
                        nextDueDate = sheduleActiy.getNextRunDate().toString();

                    } catch (Exception e) {
                       
                        nextDueDate = "";
                    }
                    
                    try{
                        
                        AaPrdDesAccountRecord accRecord = new AaPrdDesAccountRecord(cnt.getConditionForPropertyEffectiveDate("ACCOUNT", this.tdate));
                        purpose=accRecord.getLocalRefField("L.LN.PUR.NSB").getValue();
                        
                    }catch(Exception e){
                        
                        purpose="";
                        
                    }
                    
                    try{
                        
                        AaArrPaymentScheduleRecord aaArrDesPaymentScheduleRecord = new AaArrPaymentScheduleRecord(cnt.getConditionForPropertyEffectiveDate("SCHEDULE", tdate));

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
                          ilstallment="";
                      }
                    
                    try{
                        
                        AaPrdDesSettlementRecord settle = new AaPrdDesSettlementRecord(cnt.getConditionForPropertyEffectiveDate("SETTLEMENT",tdate));
                        String patOutCrnt=settle.getPayoutCurrency().toString();
                        
                        JSONArray jsonArrayThree = new JSONArray(patOutCrnt);
                        JSONObject explrObjectFive = jsonArrayThree.getJSONObject(0);
                        String payOutAcc = explrObjectFive.get("PayoutAccount").toString();
                        Info("payOutAcc="+payOutAcc);
                        JSONArray jsonArrayFour = new JSONArray(payOutAcc);
                        JSONObject explrObjectSix = jsonArrayFour.getJSONObject(0);
                        payAccFina=explrObjectSix.get("payoutAccount").toString();
                        
                        Info("payAccFina="+payAccFina);
                        
                    }catch(Exception e){
                        payAccFina="";
                        Info("EXCEPTIONPAYOUT="+e);
                    }


        } catch (Exception e) {
            limitId = "";
            grantedAmountValue = "";
            loanIntrestValueFinn = "";
            tenureValue = "";
            settlementAccont = "";
            nextDueDate = "";
            purpose="";
            applicationId="";
            totAmountValN="0.00";
            ilstallment="";
            payAccFina="";
        }


        appendDate = buildList(limitId, grantedDateValue, grantedAmountValue, loanIntrestValueFinn, tenureValue,
                settlementAccont, txnRef, nextDueDate,purpose, totAmountValN, ilstallment, payAccFina);

        return appendDate;

    }

    private String buildList(String LimitValueFin, String grantedDateValueFin, String grantedAmountValue,
            String loanIntrestValueFin, String tenureValueFin, String settlementAccontValueFin, String txnRefValueFin, String nextDueDateValFin, String purposeFin, String totAmountValFin, String ilstallmentFin, String payAccFinaFin) {

        String Result = "";

        try {
            StringBuilder str = new StringBuilder();
            str.append(LimitValueFin);
            str.append("*" + grantedDateValueFin);
            str.append("*" + grantedAmountValue);
            str.append("*" + loanIntrestValueFin);
            str.append("*" + tenureValueFin);
            str.append("*" + settlementAccontValueFin);
            str.append("*" + txnRefValueFin);
            str.append("*" + nextDueDateValFin);
            str.append("*" + purposeFin);
            str.append("*" + totAmountValFin);
            str.append("*" + ilstallmentFin);
            str.append("*" + payAccFinaFin);

            Result = str.toString();
            Info("Result=" + Result);
        } catch (Exception e) {
            Result = "" + "*" + "" + "*" + "" + "*" + "" + "*" + "" + "*" + "" + "*" + "" + "*" + "" + "*" + "";
        }
        return Result;

    }

}

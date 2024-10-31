package com.nsb.nable.reports;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aascheduledactivity.AaScheduledActivityRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class EConLnTriBalUpdate extends Enquiry {
    
    DataAccess da = new DataAccess((T24Context) this);

    @Override
    public String setValue(String value, String currentId, TStructure currentRecord,
            List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {


        String loanIntrestList = "";
        String loanIntrestValue = "";
        String grantedAmountValue = "";
        String outstandingBal = "";
        String nextDueDate = "";
        String grantedDateValue = "";
        String loanIntrestValueFinn = "";

        String AvailableAccountBalance = "";
        String TotalCommitmentAmount = "";

        String appendDate = "";

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

                    if (Fxrate.size() > 0) {
                        JSONArray jsonArrayTwo = new JSONArray(Fxrate);
                        JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
                        loanIntrestList = explrObjectFour.get("effectiveRate").toString();

                        JSONObject explrObjectFive = new JSONObject(loanIntrestList);
                        loanIntrestValue = explrObjectFive.get("value").toString();

                        if (!loanIntrestValue.equals("")) {

                            double loanIntrestValueFin = Double.parseDouble(loanIntrestValue);
                            loanIntrestValueFinn = df.format(loanIntrestValueFin).toString();

                        } 
//                        else if (!loanIntrestValue.equals("")) {
//                            double loanIntrestValueFin = Double.parseDouble(loanIntrestValue);
//                            loanIntrestValueFinn = df.format(loanIntrestValueFin).toString();
//                        }

                    }

                } catch (Exception e) {
                    loanIntrestValueFinn = "";
                }

                try {

                    List<BalanceMovement> PrincipleCurrentList = null;
                    List<BalanceMovement> AvailableAccountBalanceList = null;
                    List<BalanceMovement> TotalCommitmentAmountList = null;

                    try {

                        PrincipleCurrentList = cnt.getContractBalanceMovements("CURACCOUNT", "");
                        for (BalanceMovement bl : PrincipleCurrentList) {
                            outstandingBal = bl.getBalance().toString();

                            if (outstandingBal.contains("-")) {
                                int outstandingBalBefLen = outstandingBal.length();
                                outstandingBal = outstandingBal.substring(1, outstandingBalBefLen);

                               
                            }

                            if (!outstandingBal.equals("")) {

                                double outstandingBalIntSub = Double.parseDouble(outstandingBal);
                                BigDecimal bigDecimal3 = new BigDecimal(outstandingBalIntSub);
                                outstandingBal = df.format(bigDecimal3);

                            }

                          

                        }

                    } catch (Exception q) {
                        outstandingBal = "0.00";
                    }

                    double AvailableAccountBalanceInt = 0.00;
                    double TotalCommitmentAmountInt = 0.00;
                    double TotalCommitmentAmountIntSub = 0.00;
                    double AvailableAccountBalanceIntSub = 0.00;

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

                    double grantedAmountValueTemp = (TotalCommitmentAmountIntSub - AvailableAccountBalanceIntSub);

                    try {

                        if (grantedAmountValueTemp > 0) {

                            BigDecimal bigDecimal2 = new BigDecimal(grantedAmountValueTemp);
                            grantedAmountValue = df.format(bigDecimal2);
                           
                        }

                    } catch (Exception e) {
                        grantedAmountValue = "0.00";
                    }

                } catch (Exception e) {
                    outstandingBal = "0.00";
                    grantedAmountValue = "0.00";
                  
                }

                // try {
                //
                // AaArrTermAmountRecord aaPrdDesTermAmountRecord = new
                // AaArrTermAmountRecord(cnt.getConditionForPropertyEffectiveDate("COMMITMENT",
                // tdate));
                // grantedAmountValue =
                // aaPrdDesTermAmountRecord.getAmount().toString();
                //
                //
                // } catch (Exception e) {
                // grantedAmountValue = "";
                // }

                // try {
                //
                // AaArrangementRecord AaArr = new
                // AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT",
                // value));
                // String accountNumber = AaArr.getLinkedAppl().toString();
                // String accNumber = "";
                // try {
                //
                // JSONArray jsonArrayTwo = new JSONArray(accountNumber);
                // JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
                // accNumber = explrObjectFour.get("linkedApplId").toString();
                //
                // } catch (Exception e) {
                // accNumber = "";
                // }
                //
                // if (!accNumber.equals("") || accNumber != null) {
                //
                // EbContractBalancesRecord accRec = new
                // EbContractBalancesRecord(
                // this.da.getRecord("EB.CONTRACT.BALANCES", accNumber));
                // String typeSysDte = accRec.getTypeSysdate().toString();
                // JSONArray jsonArrayThree = new JSONArray(typeSysDte);
                // double curAccountValDob = 0.00;
                // double creditAmountDob = 0.00;
                //
                // double creditAmountValue = 0;
                // double totAmountVal = 0;
                // grantedAmountValue="";
                // outstandingBal="";
                //
                // Info("jsonArrayThree.length()=" + jsonArrayThree.length());
                // for (int i = 0; i < jsonArrayThree.length(); i++) {
                //
                // JSONObject explrObjectFive = jsonArrayThree.getJSONObject(i);
                // String matDate = explrObjectFive.get("MatDate").toString();
                // String currentAssetType =
                // explrObjectFive.get("typeSysdate").toString();
                // String currentAssetTypeGrat =
                // explrObjectFive.get("currAssetType").toString();
                // Info("matDate=" + matDate);
                // Info("currentAssetType=" + currentAssetType);
                // Info("currentAssetType=" + currentAssetType);
                //
                // try {
                //
                // List<BalanceMovement> curAccBalList = null;
                //
                // try{
                //
                // curAccBalList = cnt.getContractBalanceMovements("CURACCOUNT",
                // "");
                // for (BalanceMovement bl : curAccBalList) {
                // String balance = bl.getBalance().toString();
                // Info("PrincipleCurrent="+balance);
                //
                // }
                //
                // }catch(Exception q){
                //
                // }
                //
                // try{
                //
                // curAccBalList = cnt.getContractBalanceMovements("AVLACCOUNT",
                // "");
                // for (BalanceMovement bl : curAccBalList) {
                // String balance = bl.getBalance().toString();
                // Info("AvailableAccountBalance="+balance);
                //
                // }
                //
                // }catch(Exception w){
                //
                // }
                //
                // try{
                //
                // curAccBalList =
                // cnt.getContractBalanceMovements("TOTCOMMITMENT", "");
                // for (BalanceMovement bl : curAccBalList) {
                // String balance = bl.getBalance().toString();
                // Info("TotalCommitmentAmount="+balance);
                //
                // }
                //
                // }catch(Exception w){
                //
                // }
                //
                // if (currentAssetType.equals("CURACCOUNT")) {
                //
                // JSONArray jsonArrayFour = new JSONArray(matDate);
                // Info("jsonArrayFour=" + jsonArrayFour);
                // if (jsonArrayFour.length() > 0) {
                //
                // JSONObject explrObjectSix = jsonArrayFour.getJSONObject(0);
                // if (explrObjectSix.has("openBalance")) {
                //
                // String curAccountVal =
                // explrObjectSix.get("openBalance").toString();
                //
                // try {
                //
                // curAccountValDob = Double.parseDouble(curAccountVal);
                // Info("curAccountValDob=" + curAccountValDob);
                // } catch (Exception e) {
                // Info("ExceptionA=" + e);
                // curAccountValDob = 0.00;
                // }
                //
                // }
                //
                // }
                //
                // } else if (currentAssetType.contains("DUEACCOUNT-")) {
                //
                // JSONArray jsonArrayFive = new JSONArray(matDate);
                // Info("jsonArrayFive=" + jsonArrayFive);
                // Info("jsonArrayFive.length()=" + jsonArrayFive.length());
                // if (jsonArrayFive.length() > 0) {
                //
                // JSONObject explrObjectSeven = jsonArrayFive.getJSONObject(0);
                // if (explrObjectSeven.has("creditMvmt")) {
                //
                // String creditAmount =
                // explrObjectSeven.get("creditMvmt").toString();
                //
                // try {
                //
                // creditAmountDob = Double.parseDouble(creditAmount);
                // Info("creditAmountDob=" + creditAmountDob);
                // } catch (Exception e) {
                // Info("ExceptionB=" + e);
                // creditAmountDob = 0.00;
                // }
                //
                // }
                //
                // }
                //
                // }
                //
                // } catch (Exception e) {
                // curAccountValDob = 0.00;
                // creditAmountDob = 0.00;
                // }
                //
                // try {
                //
                // if (currentAssetType.equals("AVLACCOUNTBL")) {
                //
                // Info("matDate=" + matDate);
                // JSONArray jsonArrayFour = new JSONArray(matDate);
                // Info("jsonArrayFourSize=" + jsonArrayFour.length());
                // if (jsonArrayFour.length() > 0) {
                // JSONObject explrObjectSix = jsonArrayFour.getJSONObject(0);
                // Info("explrObjectSix=" + explrObjectSix);
                // if (explrObjectSix.has("openBalance")) {
                //
                // String creditAmount =
                // explrObjectSix.get("openBalance").toString();
                // Info("creditAmount=" + creditAmount);
                // if (!creditAmount.equals("") || !creditAmount.equals(null)) {
                //
                // creditAmountValue = Double.parseDouble(creditAmount);
                //
                // }
                //
                // }
                //
                // }
                // } else if (currentAssetType.equals("TOTCOMMITMENTBL")) {
                //
                // Info("matDate=" + matDate);
                // JSONArray jsonArrayFourTot = new JSONArray(matDate);
                // Info("jsonArrayFourSize=" + jsonArrayFourTot.length());
                // if (jsonArrayFourTot.length() > 0) {
                // JSONObject explrObjectSixTot =
                // jsonArrayFourTot.getJSONObject(0);
                // Info("explrObjectSix=" + explrObjectSixTot);
                // if (explrObjectSixTot.has("openBalance")) {
                //
                // String totAmount =
                // explrObjectSixTot.get("openBalance").toString();
                // Info("creditAmount=" + totAmount);
                // if (!totAmount.equals("") || !totAmount.equals(null)) {
                //
                // totAmountVal = Double.parseDouble(totAmount);
                //
                // }
                //
                // }
                //
                // }
                //
                // }
                //
                // } catch (Exception e) {
                // totAmountVal = 0;
                // creditAmountValue = 0;
                // Info("EXCEPTIONLAST="+e);
                // }
                //
                // Info("I=" + i);
                // if (i == (jsonArrayThree.length() - 1)) {
                //
                // double outstandingBalVir = (curAccountValDob +
                // creditAmountDob);
                // String outstandingBalBef =
                // df.format(outstandingBalVir).toString();
                //
                // try {
                //
                // if (outstandingBalBef.contains("-")) {
                // int outstandingBalBefLen = outstandingBalBef.length();
                // outstandingBal = outstandingBalBef.substring(1,
                // outstandingBalBefLen);
                //
                // Info("outstandingBal=" + outstandingBal);
                // }else{
                //
                // outstandingBal=outstandingBalBef;
                // Info("outstandingBalElse=" + outstandingBal);
                // }
                //
                //
                // } catch (Exception e) {
                // Info("EXCEPTION=" + e);
                // outstandingBal = "";
                // }
                //
                // try{
                //
                // double grantedAmountValueBigDec = (totAmountVal -
                // creditAmountValue);
                // BigDecimal bigDecimal = new
                // BigDecimal(grantedAmountValueBigDec);// form to BigDecimal
                // grantedAmountValue=df.format(bigDecimal);// get the String
                // value
                // Info("disbAmount=" + grantedAmountValue);
                //
                // }catch(Exception e){
                // grantedAmountValue="";
                // Info("ExceptionDist="+e);
                // }
                //
                // }
                //
                //
                //
                // }
                //
                // }
                //
                //
                // } catch (Exception e) {
                // Info("Exception1=" + e);
                // outstandingBal = "";
                // grantedAmountValue="";
                // }

                // try{
                //
                // EbContractBalancesRecord accRec = new
                // EbContractBalancesRecord(cnt.getConditionForPropertyEffectiveDate("AVLACCOUNTBL",
                // tdate));
                // Info("accRec="+accRec);
                //
                // }catch(Exception e){
                // Info("Exception accRec="+e);
                // }

            }
        } catch (Exception e) {
            loanIntrestValueFinn = "";
            grantedAmountValue = "0.00";
            outstandingBal = "0.00";
           
        }

//        try {
//
//            AaAccountDetailsRecord AaAccdet = new AaAccountDetailsRecord(
//                    this.da.getRecord("AA.ACCOUNT.DETAILS", value));
//
//            try {
//
//                grantedDateValue = ((TField) AaAccdet.getContractDate().get(0)).toString();
//
//            } catch (Exception e) {
//                grantedDateValue = "";
//
//            }
//
//        } catch (Exception z) {
//            grantedDateValue = "";
//        }
//
//        try {
//
//            AaScheduledActivityRecord sheduleActiy = new AaScheduledActivityRecord(
//                    this.da.getRecord("AA.SCHEDULED.ACTIVITY", value));
//            nextDueDate = sheduleActiy.getNextRunDate().toString();
//
//        } catch (Exception e) {
//            nextDueDate = "";
//        }

        // try{
        //
        // Info("T24session=="+T24session);
        // Info("getUserId=="+T24session.getUserId());
        //
        // }catch(Exception e){
        //
        // }

        appendDate = buildList(loanIntrestValueFinn, grantedAmountValue, outstandingBal);

        return appendDate;
    
    }
    
    private String buildList(String loanIntrestValueFin, String grantedAmountValueFin, String outstandingBalFin) {

        String Result = "";

        try {

            StringBuilder str = new StringBuilder();
            str.append(loanIntrestValueFin);
            str.append("*" + grantedAmountValueFin);
            str.append("*" + outstandingBalFin);


            Result = str.toString();
         
        } catch (Exception e) {
            Result = "" + "*" + "" + "*" + "";
        }
        return Result;
    }

}

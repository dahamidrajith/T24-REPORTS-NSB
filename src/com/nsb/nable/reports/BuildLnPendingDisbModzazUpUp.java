package com.nsb.nable.reports;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.OpenOption;

import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.ebcontractbalances.EbContractBalancesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class BuildLnPendingDisbModzazUpUp extends Enquiry {

    DataAccess da = new DataAccess((T24Context) this);

    public static void Info(String line) {

        // String filePath = "D:\\test.txt";
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
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {

        String accNumber = "";
        String fieldName = "@ID";
        String val="";
        
        String AvailableAccountBalance = "";
        String TotalCommitmentAmount = "";
        String CrrntCommitmentAmount = "";
        
        try{
            
            
            String a1 = filterCriteria.get(0).toString();
            JSONObject obj1= new JSONObject(a1);
            val= obj1.get("fieldname").toString();
            Info("val="+val);
            
        }catch(Exception e){
            val="";
            Info("Exceprtion="+e);
        }

        try {
            
            if(!val.equals("ARRANGEMENT.ID")){
                
            Info("filterCriteria="+filterCriteria);
            
            List<String> recarr = this.da.selectRecords("", "AA.ARRANGEMENT", "", "WITH PRODUCT.LINE EQ LENDING AND ARR.STATUS EQ CURRENT ");
           
            
            
            for (int j = 0; j < recarr.size(); j++) {
                
                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(recarr.get(j));
                
                try{
                    
                    List<BalanceMovement> AvailableAccountBalanceList = null;
                    List<BalanceMovement> TotalCommitmentAmountList = null;
                    List<BalanceMovement> CurrntComitBalanceList = null;
                    
                    double TotalCommitmentAmountIntSub = 0.00;
                    double AvailableAccountBalanceIntSub = 0.00;
                    double CurCommitBalanceIntSub = 0.00;
                    
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

                    double grantedAmountValueTemp = (TotalCommitmentAmountIntSub - AvailableAccountBalanceIntSub);
                    double grantedAmountValueTempTwo = (TotalCommitmentAmountIntSub - CurCommitBalanceIntSub);

                    try {

                        if (grantedAmountValueTemp > 0 && AvailableAccountBalanceIntSub>0) {

                          FilterCriteria fc1 = new FilterCriteria();
                         
                        fc1.setFieldname(fieldName);
                        fc1.setOperand("EQ");
                        fc1.setValue(recarr.get(j));
                        filterCriteria.add(fc1);
                       
                        
                        }else if(grantedAmountValueTempTwo > 0 && CurCommitBalanceIntSub>0){
                            
                            FilterCriteria fc1 = new FilterCriteria();
                            
                            fc1.setFieldname(fieldName);
                            fc1.setOperand("EQ");
                            fc1.setValue(recarr.get(j));
                            filterCriteria.add(fc1);
                            
                        }

                    } catch (Exception e) {
                        continue;
                    }
                    
                }catch(Exception e){
                    continue;
                }

//                AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", recarr.get(j)));
//                String accountNumber = AaArr.getLinkedAppl().toString();
//
//                try {
//                    Info("accountNumber="+accountNumber);
//                    JSONArray jsonArrayTwo = new JSONArray(accountNumber);
//                    JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
//                    accNumber = explrObjectFour.get("linkedApplId").toString();
//                    Info("accNumber="+accNumber);
//                } catch (Exception e) {
//                    accNumber = "";
//                    Info("Exception1="+e);
//                }
//
//                if (!accNumber.equals("")) {
//                    
//                    Info("ArrNo="+recarr.get(j));
//                    
//                    

//                    try {
//
//                        boolean status=false;
//                        
//                        Info("accNumber=" + accNumber);
//                        EbContractBalancesRecord accRec = new EbContractBalancesRecord(
//                                this.da.getRecord("EB.CONTRACT.BALANCES", accNumber));
//                        String typeSysDte = accRec.getTypeSysdate().toString();
//                        
//                        Info("accRec=" + accRec);
//                        Info("typeSysDte=" + typeSysDte);
//                        JSONArray jsonArrayThree = new JSONArray(typeSysDte);
//                        Info("jsonArrayThree.length()=="+jsonArrayThree.length());
//                        
//                        double creditAmountValue=0;
//                        double totAmountVal=0;
//                        
//                        for (int i = 0; i < jsonArrayThree.length(); i++) {
//
//                           
//                            
//                            try {
//
//                                JSONObject explrObjectFive = jsonArrayThree.getJSONObject(i);
//                                String currentAssetType = explrObjectFive.get("typeSysdate").toString();
//                                Info("currentAssetType="+currentAssetType);
//                                if (currentAssetType.equals("AVLACCOUNTBL")) {
//
//                                    String matDate = explrObjectFive.get("MatDate").toString();
//                                    Info("matDate="+matDate);
//                                    JSONArray jsonArrayFour = new JSONArray(matDate);
//                                    Info("jsonArrayFourSize="+jsonArrayFour.length());
//                                    if (jsonArrayFour.length() > 0) {
//                                        JSONObject explrObjectSix = jsonArrayFour.getJSONObject(0);
//                                        Info("explrObjectSix="+explrObjectSix);
//                                        if (explrObjectSix.has("openBalance")) {
//
//                                            String creditAmount = explrObjectSix.get("openBalance").toString();
//                                            Info("creditAmount="+creditAmount);
//                                            if (!creditAmount.equals("") || !creditAmount.equals(null)) {
//
//                                                creditAmountValue = Double.parseDouble(creditAmount);
//                                                
////                                                if (creditAmountValue > 0) {
////
////                                                    FilterCriteria fc1 = new FilterCriteria();
////
////                                                    fc1.setFieldname(fieldName);
////                                                    fc1.setOperand("EQ");
////                                                    fc1.setValue(recarr.get(j));
////                                                    filterCriteria.add(fc1);
////                                                    Info("Done="+filterCriteria);
////                                                }
//
//                                            }
//
//                                        }
//
//                                    }
//                                }else if(currentAssetType.equals("TOTCOMMITMENTBL")){
//                                    
//                                    String matDateTot = explrObjectFive.get("MatDate").toString();
//                                    Info("matDate="+matDateTot);
//                                    JSONArray jsonArrayFourTot = new JSONArray(matDateTot);
//                                    Info("jsonArrayFourSize="+jsonArrayFourTot.length());
//                                    if (jsonArrayFourTot.length() > 0) {
//                                        JSONObject explrObjectSixTot = jsonArrayFourTot.getJSONObject(0);
//                                        Info("explrObjectSix="+explrObjectSixTot);
//                                        if (explrObjectSixTot.has("openBalance")) {
//
//                                            String totAmount = explrObjectSixTot.get("openBalance").toString();
//                                            Info("creditAmount="+totAmount);
//                                            if (!totAmount.equals("") || !totAmount.equals(null)) {
//
//                                                totAmountVal = Double.parseDouble(totAmount);
//                                                
////                                                if (totAmountVal > 0) {
////
////                                                    FilterCriteria fc1 = new FilterCriteria();
////
////                                                    fc1.setFieldname(fieldName);
////                                                    fc1.setOperand("EQ");
////                                                    fc1.setValue(recarr.get(j));
////                                                    filterCriteria.add(fc1);
////                                                    Info("Done="+filterCriteria);
////                                                }
//
//                                            }
//
//                                        }
//
//                                    }
//                                    
//                                }
//                                Info("CREDDDDDDDDDD="+creditAmountValue);
//                                Info("TOTTTTTTTTTT="+totAmountVal);
//                                Info("status="+status);
//                                if((creditAmountValue!=totAmountVal) && creditAmountValue>0 && status==false){
//                                    
//                                    FilterCriteria fc1 = new FilterCriteria();
//                                   
//                                  fc1.setFieldname(fieldName);
//                                  fc1.setOperand("EQ");
//                                  fc1.setValue(recarr.get(j));
//                                  filterCriteria.add(fc1);
//                                  Info("Done="+filterCriteria);
//                                  status=true;
//                                    
//                                }
//
//                            } catch (Exception q) {
//                                continue;
//                            }
//
//                        }
//
//                    } catch (Exception e) {
//                        continue;
//                    }
//
//                }
//
           }
            
        }

        } catch (Exception e) {

        }
        Info("filterCriteria="+filterCriteria);
        // REASON IS IF FILYER CRITERIA IS EMPTY AUTOMATICALLY ENQUIRY BASE TABLE AUTOMATICALLY RUNNING
        if(filterCriteria.size()==0){
            
            FilterCriteria fc1 = new FilterCriteria();

            fc1.setFieldname(fieldName);
            fc1.setOperand("EQ");
            fc1.setValue("XXXX");
            filterCriteria.add(fc1);
            
        }

        return filterCriteria;
    }

}

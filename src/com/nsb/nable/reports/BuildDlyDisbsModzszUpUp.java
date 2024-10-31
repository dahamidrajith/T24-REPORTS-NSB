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
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.ebcontractbalances.EbContractBalancesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class BuildDlyDisbsModzszUpUp extends Enquiry {

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

       
        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);
        
       
        String val="";
        String accNumber = "";
        String fieldName = "@ID";
        
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

            // List<String> recarrr = this.da.selectRecords("",
            // "EB.DISBURSEMENT.DETS.NSB", "", "WITH DISBURSEMENT.DT EQ
            // 20200915");
            //
            // String fieldName = "@ID";
            //
            // for(int i=0; i<recarrr.size(); i++){
            //
            // FilterCriteria fc1 = new FilterCriteria();
            //
            // fc1.setFieldname(fieldName);
            // fc1.setOperand("EQ");
            // fc1.setValue(recarrr.get(i));
            // filterCriteria.add(fc1);
            //
            // }
            
            if(!val.equals("ARRANGEMENT.ID")){

            List<String> recarr = this.da.selectRecords("", "AA.ARRANGEMENT", "", "WITH PRODUCT.LINE EQ LENDING AND ARR.STATUS EQ CURRENT ");

            for (int j = 0; j < recarr.size(); j++) {
                Info("ArrangementId="+recarr.get(j));
                AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", recarr.get(j)));
                String accountNumber = AaArr.getLinkedAppl().toString();
                Info("accountNumber="+accountNumber);
                try {
                    JSONArray jsonArrayTwo = new JSONArray(accountNumber);
                    JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
                    accNumber = explrObjectFour.get("linkedApplId").toString();
                    Info("accNumber="+accNumber);
                    if (!accNumber.equals("")) {

                        try {
                            
                            boolean status=false;
                            EbContractBalancesRecord accRec = new EbContractBalancesRecord(
                                    this.da.getRecord("EB.CONTRACT.BALANCES", accNumber));
                            
                            String typeSysDte = accRec.getTypeSysdate().toString();
                            String DateLstUpd= accRec.getDateLastUpdate().toString();
                            
                            JSONArray jsonArrayThree = new JSONArray(typeSysDte);
                            
                            double creditAmountValue = 0;
                            double totAmountVal = 0;
                            
                            //====================================
                            
                            Contract cnt = new Contract((T24Context) this);
                            cnt.setContractId(recarr.get(j));
                            
                            List<BalanceMovement> AvailableAccountBalanceList = null;
                            List<BalanceMovement> TotalCommitmentAmountList = null;
                            List<BalanceMovement> CurrntComitBalanceList = null;
                            
                            double TotalCommitmentAmountIntSub = 0.00;
                            double AvailableAccountBalanceIntSub = 0.00;
                            double CurCommitBalanceIntSub = 0.00;
                            
                            try{
                                
                            Info("DateLstUpd="+DateLstUpd);
                            Info("tdate="+tdate);
                            
                            int idateLastUpInt=Integer.parseInt(DateLstUpd);
                            int tdateInt=Integer.parseInt((tdate+"").trim());
                            
                            if(idateLastUpInt==tdateInt){
                                
                                status=true;
                                Info("status="+status);
                            }
                            
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
                                    Info("AvailableAccountBalanceIntSub="+AvailableAccountBalanceIntSub);
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
                                    Info("TotalCommitmentAmountIntSub="+TotalCommitmentAmountIntSub);
                                }

                            } catch (Exception w) {
                                TotalCommitmentAmountIntSub = 0.00;
                            }

                            if((TotalCommitmentAmountIntSub!=AvailableAccountBalanceIntSub) && AvailableAccountBalanceIntSub>0 && status==true){
                             
                                FilterCriteria fc1 = new FilterCriteria();
                            
                                   fc1.setFieldname(fieldName);
                                   fc1.setOperand("EQ");
                                   fc1.setValue(recarr.get(j));
                                   filterCriteria.add(fc1);
                                   Info("FIRST");
                                   Info("StatusAfter="+status);         
                                   Info("ArrId="+recarr.get(j));   
                                   Info("AftDateLstUpd="+DateLstUpd);
                                   Info("Afttdate="+tdate);
                                   status=false;
                                   
                                
                            }else if((TotalCommitmentAmountIntSub!=CurCommitBalanceIntSub) && CurCommitBalanceIntSub>0 && status==true){
                                
                                FilterCriteria fc1 = new FilterCriteria();
                                
                                fc1.setFieldname(fieldName);
                                fc1.setOperand("EQ");
                                fc1.setValue(recarr.get(j));
                                filterCriteria.add(fc1);
                                Info("SECOND");
                                Info("StatusAfter="+status);         
                                Info("ArrId="+recarr.get(j));   
                                Info("AftDateLstUpd="+DateLstUpd);
                                Info("Afttdate="+tdate);
                                status=false;
                                
                            }
                            
                            }catch(Exception e){
                                Info("Main Exception="+e);
                                continue;
                            }
                            //====================================
                            
                          //==============================================================================

//                            for (int i = 0; i < jsonArrayThree.length(); i++) {
//
//                                try {
//
//                                    
//                                    JSONObject explrObjectFive = jsonArrayThree.getJSONObject(i);
//                                    String currentAssetType = explrObjectFive.get("typeSysdate").toString();
//
//                                    
//
//                                    if (currentAssetType.equals("AVLACCOUNTBL-" + tdate)) {
//
//                                        status=true;
//                                       
//
//                                    } else if (currentAssetType.equals("AVLACCOUNTBL")) { // Modified//
//
//                                        String matDate = explrObjectFive.get("MatDate").toString();
//                                        JSONArray jsonArrayFour = new JSONArray(matDate);
//                                        if (jsonArrayFour.length() > 0) {
//                                            JSONObject explrObjectSix = jsonArrayFour.getJSONObject(0);
//                                            if (explrObjectSix.has("openBalance")) {
//
//                                                String creditAmount = explrObjectSix.get("openBalance").toString();
//                                                if (!creditAmount.equals("") || !creditAmount.equals(null)) {
//
//                                                    creditAmountValue = Double.parseDouble(creditAmount);
//                                                    
//                                                }
//                                            }
//                                        }
//
//                                    } else if (currentAssetType.equals("TOTCOMMITMENTBL")) {
//
//                                        String matDateTot = explrObjectFive.get("MatDate").toString();
//                                        JSONArray jsonArrayFourTot = new JSONArray(matDateTot);
//                                        if (jsonArrayFourTot.length() > 0) {
//                                            JSONObject explrObjectSixTot = jsonArrayFourTot.getJSONObject(0);
//                                            if (explrObjectSixTot.has("openBalance")) {
//
//                                                String totAmount = explrObjectSixTot.get("openBalance").toString();
//                                                if (!totAmount.equals("") || !totAmount.equals(null)) {
//
//                                                    totAmountVal = Double.parseDouble(totAmount);
//                                                    
//                                                }
//                                            }
//                                        }
//                                    }
//                                  
//                                    if((creditAmountValue!=totAmountVal) && creditAmountValue>0 && status==true){
//                                        
//                                        
//
//                                        FilterCriteria fc1 = new FilterCriteria();
//
//                                        fc1.setFieldname(fieldName);
//                                        fc1.setOperand("EQ");
//                                        fc1.setValue(recarr.get(j));
//                                        filterCriteria.add(fc1);
//                                        
//                                        status=false;
//                                        
//                                    }
//
//
//                                } catch (Exception e) {
//                                    
//                                    continue;
//                                }
//                            }

                          //==============================================================================
                        } catch (Exception e) {
                           
                            accNumber = "";
                        }

                    }

                } catch (Exception e) {
                    accNumber = "";
                }

            }

        }
        } catch (Exception e) {

        }
       
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

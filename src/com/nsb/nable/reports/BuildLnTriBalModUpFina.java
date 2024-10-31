package com.nsb.nable.reports;

import java.io.File;
import java.io.FileWriter;
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

import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.company.CompanyRecord;
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
public class BuildLnTriBalModUpFina extends Enquiry {
    
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
        
        DecimalFormat df = new DecimalFormat("0.00");
        String outstandingBal = "";
        
        Session T24session = new Session((T24Context) this);
        
        List<BalanceMovement> PrincipleCurrentList = null;
        
        try{
            
            
            String a1 = filterCriteria.get(0).toString();
            JSONObject obj1= new JSONObject(a1);
            val= obj1.get("fieldname").toString();
            Info("val="+val);
            
        }catch(Exception e){
            val="";
            Info("Exceprtion="+e);
        }
        
        //--------------------------------------------------------------------
        String myCoCode=T24session.getCompanyId().toString();
        CompanyRecord company = new CompanyRecord(this.da.getRecord("COMPANY", myCoCode));
        String compType=company.getCompanyName().toString().toLowerCase();
        
        ReportFilterModUp2 rptFilt= new ReportFilterModUp2();
        String branchList=rptFilt.repFilterz(compType, myCoCode);
        Info("BranchList="+branchList);
      //--------------------------------------------------------------------
        
        try {

            if(!val.equals("ARRANGEMENT.ID")){
            
//            List<String> recarr = this.da.selectRecords("", "AA.ARRANGEMENT", "", "WITH PRODUCT.LINE EQ LENDING AND ARR.STATUS EQ CURRENT ");
                List<String> recarr = this.da.selectRecords("", "AA.ARRANGEMENT", "", "WITH PRODUCT.LINE EQ LENDING AND ARR.STATUS EQ CURRENT EXPIRED "+branchList);

            for (int j = 0; j < recarr.size(); j++) {
                
                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(recarr.get(j));
                
               
                
                try{
                   
                        PrincipleCurrentList = cnt.getContractBalanceMovements("CURACCOUNT", "");
                        for (BalanceMovement bl : PrincipleCurrentList) {
                            outstandingBal = bl.getBalance().toString();
                            
                            if (outstandingBal.contains("-")) {
                              int outstandingBalBefLen = outstandingBal.length();
                              outstandingBal = outstandingBal.substring(1, outstandingBalBefLen);

                              
                          }
                            
                            

                        }
                        
//                        if (!outstandingBal.equals("") || outstandingBal != null){
                            double outBalDoubleFin = Double.parseDouble(outstandingBal);
                            
                            if(outBalDoubleFin>0){
                                
                              FilterCriteria fc1 = new FilterCriteria();
                              fc1.setFieldname(fieldName);
                              fc1.setOperand("EQ");
                              fc1.setValue(recarr.get(j));
                              filterCriteria.add(fc1);

                              
                                
                            }
                            
//                        }

                }catch(Exception e){
                    continue;
                }

//                AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", recarr.get(j)));
//                String accountNumber = AaArr.getLinkedAppl().toString();
//
//                try {
//                    Info("accountNumber=" + accountNumber);
//                    JSONArray jsonArrayTwo = new JSONArray(accountNumber);
//                    JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
//                    accNumber = explrObjectFour.get("linkedApplId").toString();
//                    Info("accNumber=" + accNumber);
//                } catch (Exception e) {
//                    accNumber = "";
//                    Info("Exception1=" + e);
//                }
//
//                if (!accNumber.equals("")) {
//
//                    Info("ArrNo=" + recarr.get(j));
//
//                    try {
//
//                        Info("accNumber=" + accNumber);
//                        EbContractBalancesRecord accRec = new EbContractBalancesRecord(
//                                this.da.getRecord("EB.CONTRACT.BALANCES", accNumber));
//                        String typeSysDte = accRec.getTypeSysdate().toString();
//
//                        Info("accRec=" + accRec);
//                        Info("typeSysDte=" + typeSysDte);
//                        JSONArray jsonArrayThree = new JSONArray(typeSysDte);
//                        Info("jsonArrayThree.length()==" + jsonArrayThree.length());
//
//                        double creditAmountValue = 0;
//                        double totAmountVal = 0;
//
//                        double curAccountValDob = 0.00;
//                        double creditAmountDob = 0.00;
//
//                        for (int i = 0; i < jsonArrayThree.length(); i++) {
//
//                            JSONObject explrObjectFive = jsonArrayThree.getJSONObject(i);
//                            String currentAssetType = explrObjectFive.get("typeSysdate").toString();
//                            String matDate = explrObjectFive.get("MatDate").toString();
//
//                            try {
//
//                                if (currentAssetType.equals("CURACCOUNT")) {
//
//                                    JSONArray jsonArrayFour = new JSONArray(matDate);
//                                    Info("jsonArrayFour=" + jsonArrayFour);
//                                    if (jsonArrayFour.length() > 0) {
//
//                                        JSONObject explrObjectSix = jsonArrayFour.getJSONObject(0);
//                                        if (explrObjectSix.has("openBalance")) {
//
//                                            String curAccountVal = explrObjectSix.get("openBalance").toString();
//                                            //
//                                            // if(curAccountVal.contains("-")){
//                                            // int curAccountValLength=
//                                            // curAccountVal.length();
//                                            // curAccountVal=outstandingBal.substring(1,curAccountValLength);
//                                            // }
//
//                                            try {
//
//                                                curAccountValDob = Double.parseDouble(curAccountVal);
//                                                Info("curAccountValDob=" + curAccountValDob);
//                                            } catch (Exception e) {
//                                                Info("ExceptionA=" + e);
//                                                curAccountValDob = 0.00;
//                                            }
//
//                                        }
//
//                                    }
//
//                                } else if (currentAssetType.contains("DUEACCOUNT-")) {
//
//                                    JSONArray jsonArrayFive = new JSONArray(matDate);
//                                    Info("jsonArrayFive=" + jsonArrayFive);
//                                    Info("jsonArrayFive.length()=" + jsonArrayFive.length());
//                                    if (jsonArrayFive.length() > 0) {
//
//                                        JSONObject explrObjectSeven = jsonArrayFive.getJSONObject(0);
//                                        if (explrObjectSeven.has("creditMvmt")) {
//
//                                            String creditAmount = explrObjectSeven.get("creditMvmt").toString();
//
//                                            try {
//
//                                                creditAmountDob = Double.parseDouble(creditAmount);
//                                                Info("creditAmountDob=" + creditAmountDob);
//                                            } catch (Exception e) {
//                                                Info("ExceptionB=" + e);
//                                                creditAmountDob = 0.00;
//                                            }
//
//                                        }
//
//                                    }
//
//                                }
//
//                                Info("I=" + i);
//                                if (i == (jsonArrayThree.length() - 1)) {
//
//                                    double outstandingBalVir = (curAccountValDob + creditAmountDob);
//                                    String outstandingBalBef = df.format(outstandingBalVir).toString();
//
//                                    try {
//
//                                        if (outstandingBalBef.contains("-")) {
//                                            int outstandingBalBefLen = outstandingBalBef.length();
//                                            outstandingBal = outstandingBalBef.substring(1, outstandingBalBefLen);
//
//                                            Info("outstandingBal=" + outstandingBal);
//                                        } else {
//
//                                            outstandingBal = outstandingBalBef;
//                                            Info("outstandingBalElse=" + outstandingBal);
//                                        }
//
//                                    } catch (Exception e) {
//                                        Info("EXCEPTION=" + e);
//                                        outstandingBal = "";
//                                    }
//
//                                    try {
//                                        if (!outstandingBal.equals("") || outstandingBal != null);
//
//                                        double outBalDoubleFin = Double.parseDouble(outstandingBal);
//                                        if (outBalDoubleFin > 0) {
//
//                                            FilterCriteria fc1 = new FilterCriteria();
//                                            fc1.setFieldname(fieldName);
//                                            fc1.setOperand("EQ");
//                                            fc1.setValue(recarr.get(j));
//                                            filterCriteria.add(fc1);
//
//                                            Info("Done=" + filterCriteria);
//
//                                        }
//
//                                    } catch (Exception e) {
//                                        Info("Exception4="+e);
//                                    }
//
//                                }
//
//                            } catch (Exception e) {
//                                Info("Exception3="+e);
//                                continue;
//                            }
//
//                        }
//
//                    } catch (Exception e) {
//                        Info("Exception2="+e);
//                        continue;
//                    }
//
//                }

            }
            
        }

        } catch (Exception e) {
            
        }

        if (filterCriteria.size() == 0) {

            FilterCriteria fc1 = new FilterCriteria();

            fc1.setFieldname(fieldName);
            fc1.setOperand("EQ");
            fc1.setValue("XXXX");
            filterCriteria.add(fc1);

        }
        Info("COUNT-"+filterCriteria.size());
        return filterCriteria;
    }
    
}

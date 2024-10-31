package com.nsb.nable.reports.fin;

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
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.api.TDate;
import com.temenos.api.TField;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.party.Account;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.CustomerClass;
import com.temenos.t24.api.records.aaarrangement.LinkedApplClass;
import com.temenos.t24.api.records.aaarrangement.ProductClass;
import com.temenos.t24.api.records.aaarrpaymentschedule.AaArrPaymentScheduleRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aaprddeslimit.AaPrdDesLimitRecord;
import com.temenos.t24.api.records.aaprddessettlement.AaPrdDesSettlementRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.aascheduledactivity.AaScheduledActivityRecord;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.ebcontractbalances.EbContractBalancesRecord;
import com.temenos.t24.api.records.stmtentry.StmtEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class NoFileDlyDisbsFinUp2 extends Enquiry {

    DataAccess da = new DataAccess((T24Context) this);

    List<String> RETURN_LIST = new ArrayList<>();
    String customerId;
    String branch;
    String productGroup;
    String product;
    String arrId;
    String disbDate;
    String process = null;
    String Result = null;

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
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        Info("Done");
        try {

            List<String> selarr = getAccId(filterCriteria);

            try {

                this.customerId = selarr.get(0);
                this.branch = selarr.get(1);
                this.productGroup = selarr.get(2);
                this.product = selarr.get(3);
                this.arrId = selarr.get(4);
                this.disbDate = selarr.get(5);

                Info("customerId=" + customerId);
                Info("branch=" + branch);
                Info("productGroup=" + productGroup);
                Info("product=" + product);
                Info("startDate=" + disbDate);

                String process = processarr(this.customerId, this.branch, this.productGroup, this.product, this.arrId,
                        this.disbDate, "");

            } catch (Exception e) {

                this.process = processarr("", "", "", "", "", "", "");
            }

        } catch (Exception e) {
        }

        return this.RETURN_LIST;

    }

    private List<String> getAccId(List<FilterCriteria> filtercriteria) {

        for (FilterCriteria fieldNames : filtercriteria) {
            String FieldName = fieldNames.getFieldname();
            // if (FieldName.equals("DATE.INPUT"))
            if (FieldName.equals("CUSTOMER.ID"))
                this.customerId = fieldNames.getValue();
            if (FieldName.equals("BRANCH"))
                this.branch = fieldNames.getValue();
            if (FieldName.equals("PRODUCT.GROUP"))
                this.productGroup = fieldNames.getValue();
            if (FieldName.equals("PRODUCT"))
                this.product = fieldNames.getValue();
            if (FieldName.equals("ARRANGEMENT.ID"))
                this.arrId = fieldNames.getValue();
            if (FieldName.equals("DISB.DATE"))
                this.disbDate = fieldNames.getValue();
        }
        List<String> li = new ArrayList<>();
        li.add(this.customerId);
        li.add(this.branch);
        li.add(this.productGroup);
        li.add(this.product);
        li.add(this.arrId);
        li.add(this.disbDate);
        return li;
    }

    private String processarr(String customerIdVal, String branchVal, String productGroupVal, String productVal,
            String arrIdVal, String disbDateVal, String PR_LIST) {

        String AvailableAccountBalance = "";
        String TotalCommitmentAmount = "";
        String CrrntCommitmentAmount = "";
        String disbsDate = "";
        String assignDate = "";

        String loanIntrestList = "";
        String approvedAmountValue = "";
        String loanIntrestValue = "";
        double totAmountVal = 0.00;
        String loanIntrestValueFinn = "";
        String grantedAmountValue = "";
        String tenureValue="";
        String ilstallment="";

        String appendDate = "";

        DecimalFormat df = new DecimalFormat("0.00");

        String grantedDateValue = "";

        Session T24session = new Session((T24Context) this);
        if (disbDate == null || disbDate.trim().equals("")) {
            disbsDate = "!TODAY";
        }else{
            disbsDate=disbDateVal;
        }

        String T24Startdate = T24session.getCurrentVariable(disbsDate);
        TDate disburseDate = new TDate(T24Startdate);
        
        //--------------------------------------------------------------------
        String myCoCode=T24session.getCompanyId().toString();
        CompanyRecord company = new CompanyRecord(this.da.getRecord("COMPANY", myCoCode));
        String compType=company.getCompanyName().toString().toLowerCase();
        
        ReportFilterModUp2 rptFilt= new ReportFilterModUp2();
        String branchList=rptFilt.repFilterz(compType, myCoCode);
        Info("BranchList="+branchList);
      //--------------------------------------------------------------------


        List<String> recarr = this.da.selectRecords("", "AA.ARRANGEMENT", "",
                "WITH PRODUCT.LINE EQ LENDING AND ARR.STATUS EQ CURRENT "+branchList);

        for (int j = 0; j < recarr.size(); j++) {

            Contract cnt = new Contract((T24Context) this);
            cnt.setContractId(recarr.get(j));

            try {

                AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", recarr.get(j)));

                String cus = "";
                String lonAccNo = "";
                String prod = "";

                List<CustomerClass> cusClass = AaArr.getCustomer();
                for (CustomerClass clz : cusClass) {

                    cus = clz.getCustomer().getValue();
                    Info("cus=" + cus);
                }

                String branchCode = AaArr.getCoCodeRec().toString();
                String prodGroup = AaArr.getProductGroup().toString();

                List<ProductClass> podClass = AaArr.getProduct();
                for (ProductClass podClz : podClass) {

                    prod = podClz.getProduct().getValue();
                    Info("prod=" + prod);
                }

                String arr = recarr.get(j);

                List<LinkedApplClass> linkAppl = AaArr.getLinkedAppl();
                for (LinkedApplClass liClas : linkAppl) {

                    lonAccNo = liClas.getLinkedApplId().getValue();
                    Info("lonAccNo=" + lonAccNo);
                }

                boolean status = false;

                if (customerIdVal != null && status == false && !customerIdVal.equals(cus)) {

                    status = true;

                    if (customerIdVal.trim().equals("")) {
                        status = false;
                    }

                } else if (branchVal != null && status == false && !branchVal.equals(branchCode)) {

                    status = true;

                    if (branchVal.trim().equals("")) {
                        status = false;
                    }

                } else if (productGroupVal != null && status == false && !productGroupVal.equals(prodGroup)) {

                    status = true;

                    if (productGroupVal.trim().equals("")) {
                        status = false;
                    }

                } else if (productVal != null && status == false && !productVal.equals(prod)) {

                    status = true;

                    if (productVal.trim().equals("")) {
                        status = false;
                    }

                } else if (arrIdVal != null && status == false && !arrIdVal.equals(arr)) {

                    status = true;

                    if (arrIdVal.trim().equals("")) {
                        status = false;
                    }

                }

                try {

                    List<BalanceMovement> AvailableAccountBalanceList = null;
                    List<BalanceMovement> TotalCommitmentAmountList = null;
                    List<BalanceMovement> CurrntComitBalanceList = null;

                    double TotalCommitmentAmountIntSub = 0.00;
                    double AvailableAccountBalanceIntSub = 0.00;
                    double CurCommitBalanceIntSub = 0.00;
                    
                    boolean status3=false;
                    
                    try{
                        
                        EbContractBalancesRecord accRec = new EbContractBalancesRecord(
                                this.da.getRecord("EB.CONTRACT.BALANCES", lonAccNo));
                        
                        String DateLstUpd= accRec.getDateLastUpdate().toString();
                        
                        int idateLastUpInt=Integer.parseInt(DateLstUpd);
                        int tdateInt=Integer.parseInt((disburseDate+"").trim());
                        
                        if(idateLastUpInt==tdateInt){
                            
                            status3=true;
                            Info("status="+status);
                        }
                        
                    }catch(Exception e){
                        Info("Exception date="+e);
                    }
                    

                    try {

                        AvailableAccountBalanceList = cnt.getContractBalanceMovementsForPeriod("AVLACCOUNT", "",
                                disburseDate, disburseDate);
                        for (BalanceMovement bl : AvailableAccountBalanceList) {
                            AvailableAccountBalance = bl.getBalance().toString();

                            if (AvailableAccountBalance.contains("-")) {
                                int AvailableAccountBalanceLen = AvailableAccountBalance.length();
                                AvailableAccountBalance = AvailableAccountBalance.substring(1,
                                        AvailableAccountBalanceLen);

                            }

                            Info("AvailableAccountBalance=" + AvailableAccountBalance);

                        }

                        if (!AvailableAccountBalance.equals("")) {

                            AvailableAccountBalanceIntSub = Double.parseDouble(AvailableAccountBalance);

                        }

                    } catch (Exception w) {
                        AvailableAccountBalanceIntSub = 0.00;
                    }

                    try {

                        CurrntComitBalanceList = cnt.getContractBalanceMovementsForPeriod("CURCOMMITMENT", "",
                                disburseDate, disburseDate);
                        for (BalanceMovement b3 : CurrntComitBalanceList) {
                            CrrntCommitmentAmount = b3.getBalance().toString();

                            if (CrrntCommitmentAmount.contains("-")) {
                                int CurnCommitBalanceLen = CrrntCommitmentAmount.length();
                                CrrntCommitmentAmount = CrrntCommitmentAmount.substring(1, CurnCommitBalanceLen);

                            }

                            Info("AvailableAccountBalance=" + CrrntCommitmentAmount);

                        }

                        if (!CrrntCommitmentAmount.equals("")) {

                            CurCommitBalanceIntSub = Double.parseDouble(CrrntCommitmentAmount);
                            Info("CurCommitBalanceIntSub=" + CurCommitBalanceIntSub);
                        }

                    } catch (Exception w) {
                        CurCommitBalanceIntSub = 0.00;
                    }

                    try {

                        TotalCommitmentAmountList = cnt.getContractBalanceMovementsForPeriod("TOTCOMMITMENT", "",
                                disburseDate, disburseDate);
                        for (BalanceMovement bl : TotalCommitmentAmountList) {
                            TotalCommitmentAmount = bl.getBalance().toString();

                            if (TotalCommitmentAmount.contains("-")) {
                                int TotalCommitmentAmountLen = TotalCommitmentAmount.length();
                                TotalCommitmentAmount = TotalCommitmentAmount.substring(1, TotalCommitmentAmountLen);

                            }

                            Info("AvailableAccountBalance=" + TotalCommitmentAmount);

                        }

                        if (!TotalCommitmentAmount.equals("")) {

                            TotalCommitmentAmountIntSub = Double.parseDouble(TotalCommitmentAmount);

                        }

                    } catch (Exception w) {
                        TotalCommitmentAmountIntSub = 0.00;
                    }


                    try {

                        boolean status2 = false;

                        if ((TotalCommitmentAmountIntSub!=AvailableAccountBalanceIntSub) && AvailableAccountBalanceIntSub>0 && status==false && status3==true) {

                            status2 = true;

                        } else if ((TotalCommitmentAmountIntSub!=CurCommitBalanceIntSub) && CurCommitBalanceIntSub>0 && status==false && status3==true) {

                            status2 = true;

                        }

                        if (status2 == true) {

                            Info("Done");

                            try {

                                AaPrdDesInterestRecord aaintRecord = new AaPrdDesInterestRecord(
                                        cnt.getConditionForPropertyEffectiveDate("PRINCIPALINT", disburseDate));

                                List<FixedRateClass> Fxrate = aaintRecord.getFixedRate();

                                StringBuilder sb = new StringBuilder();

                                for (int i = 0; i < Fxrate.size(); i++) {

                                    JSONArray jsonArraySeven = new JSONArray(Fxrate);
                                    JSONObject explrObjectSix = jsonArraySeven.getJSONObject(i);
                                    loanIntrestList = explrObjectSix.get("effectiveRate").toString();

                                    JSONObject explrObjectEight = new JSONObject(loanIntrestList);
                                    loanIntrestValue = explrObjectEight.get("value").toString();

                                    String loanIntrestValueFinnBef = "";
                                    double loanIntrestValueFin = Double.parseDouble(loanIntrestValue);
                                    loanIntrestValueFinnBef = df.format(loanIntrestValueFin).toString();

                                    if (i == 0) {
                                        sb.append(loanIntrestValueFinnBef);
                                    } else {
                                        sb.append("|" + loanIntrestValueFinnBef);
                                    }

                                }

                                loanIntrestValueFinn = sb.toString();
                                Info("loanIntrestValueFinn=" + loanIntrestValueFinn);
                            } catch (Exception e) {

                                loanIntrestValueFinn = "";
                            }

                            try {

                                if (TotalCommitmentAmountIntSub > 0) {

                                    BigDecimal bigDecimal5 = new BigDecimal(TotalCommitmentAmountIntSub);
                                    approvedAmountValue = df.format(bigDecimal5);

                                }

                            } catch (Exception e) {
                                approvedAmountValue = "0.00";
                            }

                            try {

                                double grantedAmountValueBigDec = 0.00;

                                if (AvailableAccountBalanceIntSub > 0) {
                                    grantedAmountValueBigDec = (TotalCommitmentAmountIntSub - AvailableAccountBalanceIntSub);
                                } else if (CurCommitBalanceIntSub > 0) {
                                    grantedAmountValueBigDec = (TotalCommitmentAmountIntSub - CurCommitBalanceIntSub);
                                }

                                BigDecimal bigDecimal = new BigDecimal(grantedAmountValueBigDec);
                                
                                grantedAmountValue = df.format(bigDecimal);
                                Info("grantedAmountValue=" + grantedAmountValue);
                            } catch (Exception e) {
                                grantedAmountValue = "0.00";
                                Info("Exceptionqq=" + e);
                            }
                            
                            try {

                                AaAccountDetailsRecord AaAccdet = new AaAccountDetailsRecord(
                                        this.da.getRecord("AA.ACCOUNT.DETAILS", recarr.get(j)));

                                grantedDateValue = ((TField) AaAccdet.getContractDate().get(0)).toString();
                                

                            } catch (Exception e) {
                               
                                grantedDateValue = "";
                            }
                            
                            try {

                                AaPrdDesTermAmountRecord aaAccrec = new AaPrdDesTermAmountRecord(
                                        cnt.getConditionForPropertyEffectiveDate("COMMITMENT", disburseDate));
                                tenureValue = aaAccrec.getTerm().toString();
                                Info("tenureValue="+tenureValue);

                            } catch (Exception e) {
                             
                                tenureValue = "";
                            }
                            
                            try{
                                
                                AaArrPaymentScheduleRecord aaArrDesPaymentScheduleRecord = new AaArrPaymentScheduleRecord(cnt.getConditionForPropertyEffectiveDate("SCHEDULE", disburseDate));

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

                            appendDate = buildList(loanIntrestValueFinn, approvedAmountValue, grantedAmountValue, grantedDateValue, tenureValue, ilstallment, prodGroup, prod, lonAccNo, arr, cus);

                        }

                    } catch (Exception e) {
                        continue;
                    }

                } catch (Exception e) {
                    continue;
                }

            } catch (Exception e) {
                Info("Exception1=" + e);
            }

        }

        return PR_LIST;
    }

    private String buildList(String loanIntrestValueFinnVal, String approvedAmountValue, String grantedAmountValue, String grantedDateValueFin, String tenureValueFin, String ilstallmentFin, String prodGroupValFin, String prodValFin, String lonAccNoValFin, String arrValFin, String cusFinVAl) {
        try {

            StringBuilder str = new StringBuilder();
            str.append(loanIntrestValueFinnVal);

            str.append("*" + approvedAmountValue);
            str.append("*" + grantedAmountValue);
            str.append("*" + grantedDateValueFin);
            str.append("*" + tenureValueFin);
            str.append("*" + ilstallmentFin);
            str.append("*" + prodGroupValFin);
            str.append("*" + prodValFin);
            str.append("*" + lonAccNoValFin);
            str.append("*" + arrValFin);
            str.append("*" + cusFinVAl);

            String Result = str.toString();

            RETURN_LIST.add(Result);

        } catch (Exception e) {
            e.getMessage();
        }
        return this.Result;
    }

}

package com.nsb.nable.reports;

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
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.records.aaprddescharge.AaPrdDesChargeRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.records.ebcontractbalances.EbContractBalancesRecord;
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
public class EConInsuPremPay extends Enquiry {

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
    public String setValue(String value, String currentId, TStructure currentRecord,
            List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {

        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);

        String appendDate = "";
        String applicationId = "";
        String grantedAmountValue = "";
        String commisionMount = "";

        String assignedDate = "";
        String assignedDateSecond="";
        String policePrem="";
        
        String AvailableAccountBalance = "";
        String TotalCommitmentAmount = "";
        
        DecimalFormat df = new DecimalFormat("0.00");

        try {
            
            AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", value));
            String loanNoArr = AaArr.getLinkedAppl().toString();



            JSONArray jsonArrayUp = new JSONArray(loanNoArr);
            for (int k = 0; k < jsonArrayUp.length(); k++) {

                JSONObject explrObjectUp = jsonArrayUp.getJSONObject(k);
                String accNo = explrObjectUp.get("linkedApplId").toString();

                AccountRecord acont = new AccountRecord(this.da.getRecord("ACCOUNT", accNo));
                String limitKey = acont.getLimitKey().toString();

                if (!limitKey.equals("") || !limitKey.equals(null)) {

                    LimitRecord collRightRec = new LimitRecord(this.da.getRecord("LIMIT", limitKey));
                    String collatRight = collRightRec.getCollatRightTop().toString();
                    
                    
                    try{
                        
                    applicationId =collRightRec.getNotes().get(0).getValue();
                    
                    }catch(Exception e){
                        applicationId="";
                    }

                    JSONArray jsonArrayOne = new JSONArray(collatRight);
                    for (int i = 0; i < jsonArrayOne.length(); i++) {

                        JSONObject explrObjectOne = jsonArrayOne.getJSONObject(i);
                        String collatRightJson = explrObjectOne.get("CollatRight").toString();

                        JSONArray jsonArrayTwo = new JSONArray(collatRightJson);
                        for (int j = 0; j < jsonArrayTwo.length(); j++) {
                            JSONObject explrObjectTwo = jsonArrayTwo.getJSONObject(j);
                            String collatRightValue = explrObjectTwo.get("collatRight").toString();

                            CollateralRightRecord colRytRec = new CollateralRightRecord(
                                    this.da.getRecord("COLLATERAL.RIGHT", collatRightValue));
                            String colID = colRytRec.getCollateralId().toString();

                            JSONArray jsonArrayThree = new JSONArray(colID);

                            for (int x = 0; x < jsonArrayThree.length(); x++) {

                                CollateralRecord CollateralRec = new CollateralRecord(
                                        this.da.getRecord("COLLATERAL", jsonArrayThree.get(x).toString()));

//                                try {
//
//                                    if (applicationId.equals("")) {
//                                        applicationId += CollateralRec.getApplicationId().toString();
//                                    } else {
//                                        applicationId += "|" + CollateralRec.getApplicationId().toString();
//                                    }
//                                    
//                                } catch (Exception e) {
//                                    applicationId = "";
//                                }

//                                try {
//
//                                    if (assignedDate.equals("")) {
//                                        assignedDate = CollateralRec.getLocalRefField("L.D.O.I.A.1.NSB").getValue();
//                                    } else {
//                                        assignedDate = "|"+ CollateralRec.getLocalRefField("L.D.O.I.A.1.NSB").getValue();
//                                    }
//                                    
//
//                                    Info("assignedDate="+assignedDate);
//                                } catch (Exception e) {
//                                    assignedDate = "";
//                                    Info("Exception2="+e);
//                                }
                                
                                try{
                                    
                                    if (assignedDate.equals("")) {
                                        assignedDate = CollateralRec.getLocalRefField("L.D.O.V.A.1.NSB").getValue();
                                    } else {
                                        assignedDate = "|"+ CollateralRec.getLocalRefField("L.D.O.V.A.1.NSB").getValue();
                                    }

                                }catch(Exception e){
                                    assignedDate="";
                                }
                                
                                try {
                                    
                                    String fireInsuPemLen="";
                                    String dtaInsuPremLen="";
                                    
                                    
                                 // Title Insurance Not Have Premium Amount
                                    
                                    try {
                                        
                                       // fireInsuPemLen = CollateralRec.getLocalRefField("L.FR.SM.A.NSB").getValue();
                                        
                                        String fireInsuPemLenStr = CollateralRec.getLocalRefField("L.FR.SM.A.NSB").getValue();
                                        
                                        if(!fireInsuPemLenStr.equals("") || fireInsuPemLenStr!=null){
                                           double fireInsuPemLenDoub = Double.parseDouble(fireInsuPemLenStr);
                                           fireInsuPemLen = df.format(fireInsuPemLenDoub).toString();
                                        }
                                        
                                    } catch (Exception e) {
                                        fireInsuPemLen="";
                                    }
                                    
                                    try {
                                        
                                        //dtaInsuPremLen = CollateralRec.getLocalRefField("L.DTA.S.A.1.NSB").getValue();
                                        
                                        String dtaInsuPremLenStr = CollateralRec.getLocalRefField("L.DTA.S.A.1.NSB").getValue();
                                        if(!dtaInsuPremLenStr.equals("") || dtaInsuPremLenStr!=null){
                                            double dtaInsuPremLenDoub = Double.parseDouble(dtaInsuPremLenStr);
                                            dtaInsuPremLen = df.format(dtaInsuPremLenDoub).toString();
                                         }
                                        
                                    } catch (Exception e) {
                                        dtaInsuPremLen="";
                                    }
                                    
                                    policePrem="|"+fireInsuPemLen + "|" + dtaInsuPremLen;
                                    
                                    if(fireInsuPemLen.equals("") && dtaInsuPremLen.equals("")){
                                        
                                        policePrem="";
                                        
                                    }

                                } catch (Exception e) {
                                    policePrem = "";
                                }

                            }

                        }
                    }

                }else{
                    assignedDate="";
                    policePrem = "";
                }
                
//                try{
//                    
//                    EbContractBalancesRecord accRec = new EbContractBalancesRecord(
//                            this.da.getRecord("EB.CONTRACT.BALANCES", accNo));
//                    String typeSysDte = accRec.getTypeSysdate().toString();
//                    
//                    Info("accRec=" + accRec);
//                    Info("typeSysDte=" + typeSysDte);
//                    JSONArray jsonArrayThree = new JSONArray(typeSysDte);
//                    Info("jsonArrayThree.length()=="+jsonArrayThree.length());
//                    
//                    double creditAmountValue=0;
//                    double totAmountVal=0;
//                    
//                    for (int i = 0; i < jsonArrayThree.length(); i++) {
//
//                      
//                        
//                        try {
//
//                            JSONObject explrObjectFive = jsonArrayThree.getJSONObject(i);
//                            String currentAssetType = explrObjectFive.get("currAssetType").toString();
//                            Info("currentAssetType="+currentAssetType);
//                            if (currentAssetType.equals("AVLACCOUNTBL")) {
//
//                                String matDate = explrObjectFive.get("MatDate").toString();
//                                Info("matDate="+matDate);
//                                JSONArray jsonArrayFour = new JSONArray(matDate);
//                                Info("jsonArrayFourSize="+jsonArrayFour.length());
//                                if (jsonArrayFour.length() > 0) {
//                                    JSONObject explrObjectSix = jsonArrayFour.getJSONObject(0);
//                                    Info("explrObjectSix="+explrObjectSix);
//                                    if (explrObjectSix.has("openBalance")) {
//
//                                        String creditAmount = explrObjectSix.get("openBalance").toString();
//                                        Info("creditAmount="+creditAmount);
//                                        if (!creditAmount.equals("") || !creditAmount.equals(null)) {
//
//                                            creditAmountValue = Double.parseDouble(creditAmount);
//
//                                        }
//
//                                    }
//
//                                }
//                            }else if(currentAssetType.equals("TOTCOMMITMENTBL")){
//                                
//                                String matDateTot = explrObjectFive.get("MatDate").toString();
//                                Info("matDate="+matDateTot);
//                                JSONArray jsonArrayFourTot = new JSONArray(matDateTot);
//                                Info("jsonArrayFourSize="+jsonArrayFourTot.length());
//                                if (jsonArrayFourTot.length() > 0) {
//                                    JSONObject explrObjectSixTot = jsonArrayFourTot.getJSONObject(0);
//                                    Info("explrObjectSix="+explrObjectSixTot);
//                                    if (explrObjectSixTot.has("openBalance")) {
//
//                                        String totAmount = explrObjectSixTot.get("openBalance").toString();
//                                        Info("creditAmount="+totAmount);
//                                        if (!totAmount.equals("") || !totAmount.equals(null)) {
//
//                                            totAmountVal = Double.parseDouble(totAmount);
//
//                                        }
//
//                                    }
//
//                                }
//                                
//                            }
//                            Info("CREDDDDDDDDDD="+creditAmountValue);
//                            Info("TOTTTTTTTTTT="+totAmountVal);
//                            
//                        } catch (Exception q) {
//                            continue;
//                        }
//
//                    }
//                    
//                    if(totAmountVal!=0){
//            
//                        double grantedAmountValueBigDec = (totAmountVal - creditAmountValue);
//                        BigDecimal bigDecimal = new BigDecimal(grantedAmountValueBigDec);// form to BigDecimal
//                        grantedAmountValue=df.format(bigDecimal);// get the String value
//                    Info("disbAmount="+grantedAmountValue);
//                    
//                    }
//                    
//                }catch(Exception e){
//                    grantedAmountValue="0.0";
//                }
                

            }

        } catch (Exception e) {
            applicationId = "";
            assignedDate = "";
            policePrem="";
            
        }

        try {

            if (!value.equals("") || !value.equals(null)) {

                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(value);

//                try {
//
//                    AaArrTermAmountRecord aaPrdDesTermAmountRecord = new AaArrTermAmountRecord(cnt.getConditionForPropertyEffectiveDate("COMMITMENT", tdate));
//                    grantedAmountValue = aaPrdDesTermAmountRecord.getAmount().toString();
//                    
//                } catch (Exception e) {
//                    grantedAmountValue = "";
//                }
                
                try{
                    
                    double TotalCommitmentAmountIntSub = 0.00;
                    double AvailableAccountBalanceIntSub = 0.00;
                    
                    List<BalanceMovement> AvailableAccountBalanceList = null;
                    List<BalanceMovement> TotalCommitmentAmountList = null;

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
                    
                }catch(Exception e){
                    grantedAmountValue = "0.00";
                }
                

                try {

                    AaPrdDesChargeRecord prodChar = new AaPrdDesChargeRecord(
                            cnt.getConditionForPropertyEffectiveDate("SERVCHG", tdate));

                    commisionMount = prodChar.getLocalRefField("L.INS.COMM.NSB").getValue();
                    
                } catch (Exception e) {
                    commisionMount = "";
                }

            }

        } catch (Exception e) {
            commisionMount = "";
        }

        appendDate = buildList(applicationId, grantedAmountValue, commisionMount, assignedDate, policePrem);

        return appendDate;
    }

    private String buildList(String applicationIdValFin, String grantedAmountValueFin, String commisionMountValFin, String assignedDate, String policePrem) {

        String Result = "";

        try {

            StringBuilder str = new StringBuilder();
            str.append(applicationIdValFin);
            str.append("*" + grantedAmountValueFin);
            str.append("*" + commisionMountValFin);
            str.append("*" + assignedDate);
            str.append("*" + policePrem);

            Result = str.toString();
            Info("Result="+Result);
//            Info("Result==" + Result);
        } catch (Exception e) {
            Result = "" + "*" + ""+"*"+""+"*"+""+"*"+"";
        }
        return Result;
    }

}

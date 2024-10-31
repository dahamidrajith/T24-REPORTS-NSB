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
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.aa.contractapi.OutstandingBalances;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
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

public class EConLnTriBalSumMod extends Enquiry {

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

        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);

        String outstandingBal = "";
        String grantedAmountValue = "";
        String dueAmount="";
        String outStandingBalFinal="";

        String AvailableAccountBalance = "";
        String TotalCommitmentAmount = "";
        String CrrntCommitmentAmount = "";

        String appendDate = "";

        DecimalFormat df = new DecimalFormat("0.00");

        try {

            if (!value.equals("") || !value.equals(null)) {

                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(value);

                List<BalanceMovement> PrincipleCurrentList = null;
                List<BalanceMovement> AvailableAccountBalanceList = null;
                List<BalanceMovement> TotalCommitmentAmountList = null;
                List<BalanceMovement> PrincipleCurrentListDue = null;
                List<BalanceMovement> CurrntComitBalanceList = null;
                
                double outstandingBalIntSub=0.00;
                double dueAmountBefLenSub=0.00;
                
                try {

                    PrincipleCurrentList = cnt.getContractBalanceMovements("CURACCOUNT", "");
                    PrincipleCurrentListDue = cnt.getContractBalanceMovements("DUEACCOUNT", "");
                    
                    for (BalanceMovement bl : PrincipleCurrentList) {
                        outstandingBal = bl.getBalance().toString();

                        if (outstandingBal.contains("-")) {
                            int outstandingBalBefLen = outstandingBal.length();
                            outstandingBal = outstandingBal.substring(1, outstandingBalBefLen);

                           
                        }


                            outstandingBalIntSub = Double.parseDouble(outstandingBal);


                    }
                    Info("outstandingBalIntSub="+outstandingBalIntSub);
                    for (BalanceMovement b2 : PrincipleCurrentListDue) {
                        dueAmount = b2.getBalance().toString();

                        if (dueAmount.contains("-")) {
                            int dueAmountBefLen = dueAmount.length();
                            dueAmount = dueAmount.substring(1, dueAmountBefLen);

                           
                        }


                            dueAmountBefLenSub = Double.parseDouble(dueAmount);


                    }
                    Info("dueAmountBefLenSub="+dueAmountBefLenSub);
                    double finalOutStand=outstandingBalIntSub+dueAmountBefLenSub;
                    BigDecimal bigDecimal4 = new BigDecimal(finalOutStand);
                    outStandingBalFinal = df.format(bigDecimal4);
                    Info("outStandingBalFinal="+outStandingBalFinal);
                } catch (Exception q) {
                    outStandingBalFinal = "0.00";
                    Info("Exception New="+q);
                }

                double TotalCommitmentAmountIntSub = 0.00;
                double AvailableAccountBalanceIntSub = 0.00;
                double CurCommitBalanceIntSub = 0.00;

                try {

                    AvailableAccountBalanceList = cnt.getContractBalanceMovements("AVLACCOUNT", "");
                    for (BalanceMovement bl : AvailableAccountBalanceList) {
                        AvailableAccountBalance = bl.getBalance().toString();

                        if (AvailableAccountBalance.contains("-")) {
                            int AvailableAccountBalanceLen = AvailableAccountBalance.length();
                            AvailableAccountBalance = AvailableAccountBalance.substring(1, AvailableAccountBalanceLen);

                           
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
                
                double grantedAmountValueTemp =0.00;
                
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

            }
        } catch (Exception e) {
            outStandingBalFinal = "0.00";
            grantedAmountValue = "0.00";
        }

        appendDate = buildList(outStandingBalFinal, grantedAmountValue);

        return appendDate;
    }

    private String buildList(String outstandingBalValueFin, String grantedAmountValueFin) {

        String Result = "";

        try {

            StringBuilder str = new StringBuilder();
            str.append(outstandingBalValueFin);
            str.append("*" + grantedAmountValueFin);

            Result = str.toString();
            Info("Result=" + Result);
        } catch (Exception e) {
            Result = "" + "*" + "";
        }
        return Result;
    }

}

package com.nsb.nable.reports;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.api.TDate;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.party.Account;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aaprddeslimit.AaPrdDesLimitRecord;
import com.temenos.t24.api.records.aaprddessettlement.AaPrdDesSettlementRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.records.limit.LimitRecord;
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
public class EConDisbTransDet extends Enquiry {

    DataAccess da = new DataAccess((T24Context) this);

    // public static void Info(String line) {
    //
    // // String filePath = "D:\\test.txt";
    // String filePath = "/nsbt24/debug/log.txt";
    // line = String.valueOf(line) + "\n";
    // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd
    // HH:mm:ss");
    // LocalDateTime now = LocalDateTime.now();
    // try {
    // File myObj = new File(filePath);
    // if (myObj.createNewFile()) {
    // FileWriter fw = new FileWriter(filePath);
    // fw.write("---" + line);
    // fw.close();
    // } else {
    // Files.write(Paths.get(filePath, new String[0]), line.getBytes(),
    // new OpenOption[] { StandardOpenOption.APPEND });
    // }
    // } catch (Exception e) {
    // e.getStackTrace();
    // }
    // }

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
        String txnRef = "";

        String appendDate = "";

        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);

        try {

            if (!value.equals("") || !value.equals(null)) {

                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(value);

                try {

                    AaPrdDesLimitRecord limRec = new AaPrdDesLimitRecord(
                            cnt.getConditionForPropertyEffectiveDate("LIMIT", tdate));
                    limitId = limRec.getLimit().getValue();

                } catch (Exception e) {
                    limitId = "";
                }

                try {

                    String prop = cnt.getPropertyIdsForPropertyClass("TERM.AMOUNT").get(0);
                    AaPrdDesTermAmountRecord aaPrdDesTermAmountRecord = new AaPrdDesTermAmountRecord(
                            cnt.getConditionForProperty(prop));
                    grantedAmountValue = aaPrdDesTermAmountRecord.getAmount().toString();

                } catch (Exception e) {
                    grantedAmountValue = "";
                }

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

                    }

                } catch (Exception e) {
                    loanIntrestValue = "";
                }

                try {

                    AaPrdDesTermAmountRecord aaAccrec = new AaPrdDesTermAmountRecord(
                            cnt.getConditionForPropertyEffectiveDate("COMMITMENT", tdate));
                    tenureValue = aaAccrec.getTerm().toString();

                } catch (Exception e) {
                    tenureValue = "";
                }

                try {

                    // AaPrdDesSettlementRecord aaPrdDesSettlementRecord = new
                    // AaPrdDesSettlementRecord(cnt.getConditionForPropertyEffectiveDate("SETTLEMENT",
                    // tdate));

                    String prop = cnt.getPropertyIdsForPropertyClass("SETTLEMENT").get(0);
                    AaPrdDesSettlementRecord aaPrdDesSettlementRecord = new AaPrdDesSettlementRecord(
                            cnt.getConditionForProperty(prop));
                    String payinCurrencyArray = aaPrdDesSettlementRecord.getPayinCurrency().toString();

                    if (aaPrdDesSettlementRecord.getPayinCurrency().size() > 0) {
                        JSONArray jsonArrayOne = new JSONArray(payinCurrencyArray);

                        for (int i = 0; i < jsonArrayOne.length(); i++) {

                            JSONObject explrObjectOne = jsonArrayOne.getJSONObject(i);
                            String DdMandateRefArray = explrObjectOne.get("DdMandateRef").toString();

                            JSONArray jsonArrayTwo = new JSONArray(DdMandateRefArray);

                            for (int j = 0; j < jsonArrayTwo.length(); j++) {

                                JSONObject explrObjectTwo = jsonArrayTwo.getJSONObject(j);
                                settlementAccont = explrObjectTwo.get("payinAccount").toString();

                                // if (settlementAccontValue.equals("")) {
                                // settlementAccontArr +=
                                // explrObjectTwo.get("payinAccount").toString();
                                // } else {
                                // settlementAccontArr +="|"+
                                // explrObjectTwo.get("payinAccount").toString();
                                // }

                            }

                        }

                    }

                } catch (Exception e) {
                    settlementAccont = "";
                }

            }

        } catch (Exception e) {
            limitId = "";
            grantedAmountValue = "";
            loanIntrestValue = "";
            tenureValue = "";
        }

        try {

            AaAccountDetailsRecord AaAccdet = new AaAccountDetailsRecord(
                    this.da.getRecord("AA.ACCOUNT.DETAILS", value));

            grantedDateValue = ((TField) AaAccdet.getContractDate().get(0)).toString();

        } catch (Exception e) {
            grantedDateValue = "";
        }

        try {

            AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", value));
            String loanNoArr = AaArr.getLinkedAppl().toString();
            String accountNumber = AaArr.getLinkedAppl().toString();

            JSONArray jsonArrayUp = new JSONArray(loanNoArr);
            for (int k = 0; k < jsonArrayUp.length(); k++) {

                JSONObject explrObjectUp = jsonArrayUp.getJSONObject(k);
                String accNo = explrObjectUp.get("linkedApplId").toString();

                AccountRecord acont = new AccountRecord(this.da.getRecord("ACCOUNT", accNo));
                String limitKey = acont.getLimitKey().toString();

                if (!limitKey.equals("") || !limitKey.equals(null)) {

                    LimitRecord collRightRec = new LimitRecord(this.da.getRecord("LIMIT", limitKey));
                    String collatRight = collRightRec.getCollatRightTop().toString();

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

                                if (applicationId.equals("")) {
                                    applicationId += CollateralRec.getApplicationId().toString();
                                } else {
                                    applicationId += "|" + CollateralRec.getApplicationId().toString();
                                }

                            }

                        }
                    }

                }
            }

            try {

                String accNumber = "";
                String txnCode = "";

                JSONArray jsonArrayTwo = new JSONArray(accountNumber);
                JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
                accNumber = explrObjectFour.get("linkedApplId").toString();

                if (!accNumber.equals("") || !accNumber.equals(null)) {

                    Account ac = new Account((T24Context) this);
                    ac.setAccountId(accNumber);

                    List<String> ids = ac.getEntries("BOOK", " ", " ", " ", tdate, tdate);

                    for (String stmtid : ids) {

                        StmtEntryRecord ser = new StmtEntryRecord(this.da.getRecord("STMT.ENTRY", stmtid));
                        txnCode = ser.getTransactionCode().getValue();
                        if (txnCode.equals("850")) {
                            txnRef = ser.getTransReference().getValue();

                        }

                    }

                }

            } catch (Exception z) {
                txnRef = "";
            }

        } catch (Exception e) {
            applicationId = "";
            
            try {
                
                AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", value));
                String accountNumber = AaArr.getLinkedAppl().toString();

                String accNumber = "";
                String txnCode = "";

                JSONArray jsonArrayTwo = new JSONArray(accountNumber);
                JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
                accNumber = explrObjectFour.get("linkedApplId").toString();

                if (!accNumber.equals("") || !accNumber.equals(null)) {

                    Account ac = new Account((T24Context) this);
                    ac.setAccountId(accNumber);

                    List<String> ids = ac.getEntries("BOOK", " ", " ", " ", tdate, tdate);

                    for (String stmtid : ids) {

                        StmtEntryRecord ser = new StmtEntryRecord(this.da.getRecord("STMT.ENTRY", stmtid));
                        txnCode = ser.getTransactionCode().getValue();
                        if (txnCode.equals("850")) {
                            txnRef = ser.getTransReference().getValue();

                        }

                    }

                }

            } catch (Exception m) {
                txnRef="";
            }
            
        }

        appendDate = buildList(limitId, grantedDateValue, grantedAmountValue, loanIntrestValue, tenureValue,
                settlementAccont, applicationId);

        return appendDate;
    }

    private String buildList(String LimitValueFin, String grantedDateValueFin, String grantedAmountValue,
            String loanIntrestValueFin, String tenureValueFin, String settlementAccontValueFin,
            String applicationIdValFin) {

        String Result = "";

        try {
            StringBuilder str = new StringBuilder();
            str.append(LimitValueFin);
            str.append("*" + grantedDateValueFin);
            str.append("*" + grantedAmountValue);
            str.append("*" + loanIntrestValueFin);
            str.append("*" + tenureValueFin);
            str.append("*" + settlementAccontValueFin);
            str.append("*" + applicationIdValFin);

            Result = str.toString();

        } catch (Exception e) {
            Result = "" + "*" + "" + "*" + "" + "*" + "" + "*" + "" + "*" + "" + "*" + "";
        }
        return Result;

    }

}

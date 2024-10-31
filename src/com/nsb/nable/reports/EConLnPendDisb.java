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
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aaprddeslimit.AaPrdDesLimitRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.records.isdisbbalances.DisburseRefClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class EConLnPendDisb extends Enquiry {

    DataAccess da = new DataAccess((T24Context) this);

    // public static void Info(String line) {
    //
    // String filePath = "D:\\test.txt";
    // // String filePath = "/nsbt24/debug/log.txt";
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

        String loanIntrestList = "";
        String loanIntrestValue = "";
        String grantedAmountValue = "";
        String limitId = "";

        String appendDate = "";

        Contract cnt = new Contract((T24Context) this);
        cnt.setContractId(value);
        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);

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

            String prop = cnt.getPropertyIdsForPropertyClass("TERM.AMOUNT").get(0);
            AaPrdDesTermAmountRecord aaPrdDesTermAmountRecord = new AaPrdDesTermAmountRecord(
                    cnt.getConditionForProperty(prop));
            grantedAmountValue = aaPrdDesTermAmountRecord.getAmount().toString();

        } catch (Exception e) {
            grantedAmountValue = "";
        }

        try {

            AaPrdDesLimitRecord limRec = new AaPrdDesLimitRecord(
                    cnt.getConditionForPropertyEffectiveDate("LIMIT", tdate));
            limitId = limRec.getLimit().getValue();

        } catch (Exception e) {
            limitId = "";
        }

        appendDate = buildList(loanIntrestValue, grantedAmountValue, limitId);

        return appendDate;
    }

    private String buildList(String loanIntrestValueFin, String grantedAmountValueFin, String limitIdValueFin) {

        String Result = "";

        try {
            StringBuilder str = new StringBuilder();
            str.append(loanIntrestValueFin);
            str.append("*" + grantedAmountValueFin);
            str.append("*" + limitIdValueFin);

            Result = str.toString();

        } catch (Exception e) {
            Result=""+"*"+""+"*"+"";
        }
        return Result;

    }

}

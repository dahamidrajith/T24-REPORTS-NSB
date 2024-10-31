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

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.party.Account;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddescharge.AaPrdDesChargeRecord;
import com.temenos.t24.api.records.acchargerequest.AcChargeRequestRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebinsurancecompanydetsnsb.EbInsuranceCompanyDetsNsbRecord;
import com.temenos.t24.api.tables.ebinsurancecompanydetsnsb.InsuranceTypeClass;
import com.temenos.tafj.api.client.impl.T24Context;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.records.stmtentry.StmtEntryRecord;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class EInsuPremColt extends Enquiry {

    DataAccess da = new DataAccess((T24Context) this);
    AaPrdDesChargeRecord chg = null;

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
        
        

        String appendDate = "";
        String collatIdArray = "";
        String collatValue = "";
        String insuType = "";
        String commisionMount = "";
        

        String titleype = "";
        String fireType = "";
        String dtaType = "";
        String titleTypeName = "";
        String fireTypeName = "";
        String dtaTypeName = "";
        String insuCompany = "";
        String txnRef = "";
        String policePrem = "";

        String string = value;
        String[] parts = string.split("/");
        String part1 = parts[0];
        String part2 = parts[1];

        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);

        String[] splited = part1.split("\\s+");
        int splitedSize = splited.length;

        try {

            for (int i = 0; i < splitedSize; i++) {

                CollateralRightRecord colRytRec = new CollateralRightRecord(
                        this.da.getRecord("COLLATERAL.RIGHT", splited[i]));

                collatIdArray = colRytRec.getCollateralId().toString();

                if (colRytRec.getCollateralId().size() > 0) {
                    JSONArray jsonArray = new JSONArray(collatIdArray);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        collatValue = jsonArray.get(j).toString();

                        CollateralRecord collateralRec = new CollateralRecord(
                                this.da.getRecord("COLLATERAL", collatValue));


                        try {

                            int titleTypeLength = collateralRec.getLocalRefGroups("L.T.IN.A.NSB").size();
                            for (int k = 0; k < titleTypeLength; k++) {
                                titleype = collateralRec.getLocalRefGroups("L.T.IN.A.NSB").get(k)
                                        .getLocalRefField("L.T.IN.A.NSB").getValue();
                                titleTypeName = "Title Insurance";
                            }

                        } catch (Exception e) {
                            titleTypeName = "";
                        }

                        try {

                            fireType = collateralRec.getLocalRefField("L.FIRE.I.AP.NSB").getValue();
                            if (fireType.equals("Y")) {
                                fireTypeName = "Fire Type";
                            }

                        } catch (Exception e) {
                            fireTypeName = "";
                        }

                        try {

                            int dtaTypeLength = collateralRec.getLocalRefGroups("L.DTA.IN.A.NSB").size();
                            for (int z = 0; z < dtaTypeLength; z++) {
                                dtaType = collateralRec.getLocalRefGroups("L.DTA.IN.A.NSB").get(z)
                                        .getLocalRefField("L.DTA.IN.A.NSB").getValue();
                                dtaTypeName = "DTA Insurance";
                            }

                        } catch (Exception e) {
                            dtaTypeName = "";
                        }

                        try {

                            String insuCompanyTitle = "";
                            String insuCompanyFire = "";
                            String insuCompanyDta = "";

                            try {

                                int insuCompanyLengthTitle = collateralRec.getLocalRefGroups("L.T.IN.IS.NSB").size();
                                for (int w = 0; w < insuCompanyLengthTitle; w++) {
                                    insuCompanyTitle = collateralRec.getLocalRefGroups("L.T.IN.IS.NSB").get(w)
                                            .getLocalRefField("L.T.IN.IS.NSB").getValue();
                                }
                            } catch (Exception e) {
                                insuCompanyTitle = "";
                            }

                            try {

                                int insuCompanyLengthFire = collateralRec.getLocalRefGroups("L.FR.IN.IS.NSB").size();
                                for (int w = 0; w < insuCompanyLengthFire; w++) {
                                    insuCompanyFire = collateralRec.getLocalRefGroups("L.FR.IN.IS.NSB").get(w)
                                            .getLocalRefField("L.FR.IN.IS.NSB").getValue();
                                }
                            } catch (Exception e) {
                                insuCompanyFire = "";
                            }

                            try {

                                int insuCompanyLengthDta = collateralRec.getLocalRefGroups("L.DTA.I.I.1.NSB").size();
                                for (int w = 0; w < insuCompanyLengthDta; w++) {
                                    insuCompanyDta = collateralRec.getLocalRefGroups("L.DTA.I.I.1.NSB").get(w)
                                            .getLocalRefField("L.DTA.I.I.1.NSB").getValue();
                                }
                            } catch (Exception e) {
                                insuCompanyDta = "";
                            }

                            insuCompany = insuCompanyTitle + "|" + insuCompanyFire + "|" + insuCompanyDta;
                            
                            if(insuCompanyTitle.equals("") && insuCompanyFire.equals("") && insuCompanyDta.equals("")){
                                insuCompany="";
                            }

                        } catch (Exception e) {
                            insuCompany = "";
                        }
                        
                        try {
                            String titleInsuPemLen="";
                            String fireInsuPemLen="";
                            String dtaInsuPremLen="";
                            DecimalFormat df = new DecimalFormat("0.00");
                            
                            try {
//                                
//                                int titleInsuPemLenSize = collateralRec.getLocalRefGroups("L.T.IN.A.NSB").size();
//                                for(int z=0; z<titleInsuPemLenSize;z++){
//                                    titleInsuPemLen = collateralRec.getLocalRefGroups("L.T.IN.A.NSB").get(z)
//                                            .getLocalRefField("L.T.IN.A.NSB").getValue();
//                                }
                                // Title Insurance Not Have Premium Amount
                                String titleInsuPemLenStr = df.format(collateralRec.getLocalRefField("L.T.IN.A.NSB").getValue());
                                if(!titleInsuPemLenStr.equals("") || titleInsuPemLenStr!=null){
                                    double titleInsuPemLenDoub = Double.parseDouble(titleInsuPemLenStr);
                                    titleInsuPemLen = df.format(titleInsuPemLenDoub).toString();
                                 }
                                
                            } catch (Exception e) {
                               
                                titleInsuPemLen="";
                            }

                            try {
                                
                                String fireInsuPemLenStr = collateralRec.getLocalRefField("L.FR.SM.A.NSB").getValue();
                                
                                if(!fireInsuPemLenStr.equals("") || fireInsuPemLenStr!=null){
                                   double fireInsuPemLenDoub = Double.parseDouble(fireInsuPemLenStr);
                                   fireInsuPemLen = df.format(fireInsuPemLenDoub).toString();
                                }
                                
                                
                                
                            } catch (Exception e) {
                                fireInsuPemLen="";
                            }
                            
                            try {
                                
                                String dtaInsuPremLenStr = collateralRec.getLocalRefField("L.DTA.S.A.1.NSB").getValue();
                                if(!dtaInsuPremLenStr.equals("") || dtaInsuPremLenStr!=null){
                                    double dtaInsuPremLenDoub = Double.parseDouble(dtaInsuPremLenStr);
                                    dtaInsuPremLen = df.format(dtaInsuPremLenDoub).toString();
                                 }
                                
                                
                            } catch (Exception e) {
                                dtaInsuPremLen="";
                            }
                            
                            policePrem=titleInsuPemLen+"|"+fireInsuPemLen + "|" + dtaInsuPremLen;
                            
                            if(titleInsuPemLen.equals("") && fireInsuPemLen.equals("") && dtaInsuPremLen.equals("")){
                                
                                policePrem="";
                                
                            }

                        } catch (Exception e) {
                            
                            policePrem = "";
                        }


                        try {

                            Contract cnt = new Contract((T24Context) this);
                            cnt.setContractId(part2);

                            AaPrdDesChargeRecord prodChar = new AaPrdDesChargeRecord(
                                    cnt.getConditionForPropertyEffectiveDate("SERVCHG", tdate));

                            commisionMount = prodChar.getLocalRefField("L.INS.COMM.NSB").getValue();

                        } catch (Exception e) {
                            commisionMount = "";
                        }

                        insuType = titleTypeName + "|" + fireTypeName + "|" + dtaTypeName;
                        
                        if(titleTypeName.equals("") && fireTypeName.equals("") && dtaTypeName.equals("")){
                            insuType="";
                        }

                        appendDate = buildList(insuType, insuCompany, commisionMount, policePrem);

                    }
                }else{
                    insuType="";
                    insuCompany = "";
                    policePrem = "";
                    
                    appendDate = buildList(insuType, insuCompany, commisionMount, policePrem);
                    
                }

            }

        } catch (Exception e) {

            try {

                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(part2);

                AaPrdDesChargeRecord prodChar = new AaPrdDesChargeRecord(
                        cnt.getConditionForPropertyEffectiveDate("SERVCHG", tdate));

                commisionMount = prodChar.getLocalRefField("L.INS.COMM.NSB").getValue();

                appendDate = buildList("", "", commisionMount, "");

            } catch (Exception q) {
                appendDate = buildList("", "", "", "");
            }

        }

        return appendDate;
    }

    private String buildList(String insuTypeValueFin, String insuCompanyValueFin, String commisionMountValueFin,
            String policePremFinVAl) {

        String Result = "";

        try {

            StringBuilder str = new StringBuilder();
            str.append(insuTypeValueFin);
            str.append("*" + insuCompanyValueFin);
            str.append("*" + commisionMountValueFin);
            str.append("*" + policePremFinVAl);

            Result = str.toString();
            Info("Result="+Result);
        } catch (Exception e) {
            Result = "" + "*" + "" + "*" + "" + "*" + "";
        }
        return Result;
    }

}

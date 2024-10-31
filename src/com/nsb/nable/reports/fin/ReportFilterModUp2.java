package com.nsb.nable.reports.fin;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class ReportFilterModUp2{
    
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
    
    public String repFilterz(String myCompType, String myCompId){
        

        String sql="";
        
        try{
            
            
            if (myCompType.contains("head office")) {
                
                sql="";
                
            }else if(myCompType.contains("region")) {
                
                String myCompIdSubString=myCompId.substring(6,myCompId.length());
                int compIdLast=Integer.parseInt(myCompIdSubString);  
                
                StringBuilder result = new StringBuilder();
                
                for(int i=0; i<50;i++){
                    
                    int valueInt=++compIdLast;
                    
                    if(i==0){
                        result.append(valueInt);
                    }else{
                        result.append(" "+valueInt);
                    }
                    
                }
                
                sql="AND CO.CODE EQ "+result+" ";
                
            }else{
                
                sql="AND CO.CODE EQ "+myCompId+" ";
                
            }
            
            Info("sqlClass="+sql);
            
        }catch(Exception e){
            Info("ClassExcep="+e);
        }
        
        return sql;
    }

}

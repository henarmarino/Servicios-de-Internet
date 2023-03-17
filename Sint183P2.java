package p2;

import p2.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.*;
import java.util.Map;
import java.util.HashMap;
import java.net.*;
import org.w3c.dom.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

//This class has two methods. The init that calls the method that parsers the files and classifies them into error, fatalerror and correct files. 
//The second method is the doGet() that calls all the method of the FrontEnd with the objective of showing all the screens in auto and browser mode.
public class Sint183P2 extends HttpServlet {

    //To call the methods that are in the DataModel and DrontEnd classes
    FrontEnd frontEnd = new FrontEnd();
    DataModel dataModel= new DataModel();
    //The correct password that is going to be compared with the password entered
    private final static String goodPasswd = "HenymSINT9";  

    //It calls the method that parsers the files and classifies them into error, fatalerror and correct files. 
    public void init(){
  
        try{
            //Calling the method to parse all the files
            dataModel.parserDocs();
            }catch(Exception e){

            }
    
    }
    
    //It calls all the method of the FrontEnd with the objective of showing all the screens in auto and browser mode.
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        try{

            PrintWriter out = res.getWriter(); 
            //To collect all the parameters that will determined the correct screen.
            String passwd= req.getParameter("p");
            String pphase= req.getParameter("pphase");
            String reqAutomode= req.getParameter("auto");
            String country = req.getParameter("pcountry");
            String paid= req.getParameter("paid");
            //The lists with all the countries, albums of a given country and songs of a given country and album
            ArrayList<Song> listSongs=new ArrayList<Song>();
            ArrayList<Album> listAlbums= new ArrayList<Album>();   
            ArrayList<String> listCountries = new ArrayList<String>();
            //The lists with all error and fatalerror files
            ArrayList<String> errorsFile= new ArrayList<String>();
            ArrayList<String> fatalerrorsFile= new ArrayList<String>();

            //Here the parameters are analyzed to call the correct methods of the FrontEnd and DataModel classes
            //There is no password
            if(passwd== null){  
                //The auto mode is required
                if((reqAutomode != null) && (reqAutomode.equals("true"))){
                    frontEnd.respAutoPMissing(res);
                }else{
                    frontEnd.doGetPMissing(res);
                }
            //The password is wrong.
            }else if(!(passwd.equals(goodPasswd))){ 
                //The auto mode is required
                if((reqAutomode != null) && (reqAutomode.equals("true"))){
                    frontEnd.respAutoPWrong(res);
                }else
                    frontEnd.doGetPWrong(res);
            //The pphase is null, so there is no pphase and the initial screen is called
            }else if(pphase==null){  
                //The auto mode is required
                if((reqAutomode != null) && (reqAutomode.equals("true"))){
                    frontEnd.respAutoPhase01(res);
                } else{
                    frontEnd.doGetPhase01(res, passwd);
                }
            }else{
            //The value of the pphase is analyzed
            switch(pphase){
                //The pphase is 01, so the initial screen is called.
                case "01": 
                    //The auto mode is required
                    if((reqAutomode != null) && (reqAutomode.equals("true"))){
                        frontEnd.respAutoPhase01(res);
                    } else{
                        frontEnd.doGetPhase01(res, passwd);
                    }
                    break;
                //The pphase is 02, so the screen with all the error and fatalerror filles is called
                case "02": 
                    //The auto mode is required
                    if((reqAutomode != null) && (reqAutomode.equals("true"))){
                        //2 methods of the DataModel class are called for getting the error and fatalerror files
                        errorsFile= dataModel.getErrorsFilesAUTO();
                        fatalerrorsFile= dataModel.getFatalErrorsFilesAUTO();
                        frontEnd.respAutoPhase02(res,errorsFile,fatalerrorsFile);
                    } else{
                        //2 methods of the DataModel class are called for getting the error and fatalerror files
                        errorsFile= dataModel.getErrorsFiles();
                        fatalerrorsFile= dataModel.getFatalErrorsFiles();
                        frontEnd.doGetPhase02(res, passwd,errorsFile,fatalerrorsFile);
                    }
                    break;
                //The pphase is 11, so the screen with all the countries is called.
                case "11":
                    //Calling the method getQ1Countries to collect all the found countries
                    listCountries= dataModel.getQ1Countries();
                    //The auto mode is required
                    if((reqAutomode != null) && (reqAutomode.equals("true"))){
                        frontEnd.respAutoPhase11(res,passwd,listCountries);
                    } else{
                        frontEnd.doGetPhase11(res,listCountries,passwd);
                    }
                    break;
                //The pphase is 12, so the screen with all the albums of a given country is called.
                case "12":
                    //A compulsory parameter (pcountry) is missing
                    if(country == null){  
                        //The auto mode is required
                        if((reqAutomode != null) && (reqAutomode.equals("true"))){
                            frontEnd.respAutoParamMissing(res,"pcountry");
                        } else{
                            frontEnd.doGetParamMissing(res,"pcountry");
                        }
                    } 
                    else {
                        //The parameter pcountry is not null. We use it to get the albums of this country calling the method getQ1Albums of the DataModel class
                        country = req.getParameter("pcountry");
                        listAlbums = dataModel.getQ1Albums(country);
                        //The auto mode is required
                        if((reqAutomode != null) && (reqAutomode.equals("true"))){
                            frontEnd.respAutoPhase12(res, listAlbums);
                        } else{
                            frontEnd.doGetPhase12(res, country, listAlbums, passwd);
                        }
                    }
                    break;
                case "13": 
                    //A compulsory parameter (pcountry) is missing
                    if((country == null)){ 
                        //The auto mode is required
                        if((reqAutomode != null) && (reqAutomode.equals("true"))){
                            frontEnd.respAutoParamMissing(res, "pcountry");
                        } else{
                            frontEnd.doGetParamMissing(res,"pcountry");
                        }
                    //A compulsory parameter (paid) is missing
                    } else if((paid==null)){
                        //The auto mode is required
                        if((reqAutomode != null) && (reqAutomode.equals("true"))){
                            frontEnd.respAutoParamMissing(res, "paid");
                        } else{
                            frontEnd.doGetParamMissing(res, "paid");
                        }
                    }
                    else {
                        //The parameters pcountry and paid are not null. We use them to get the songs of this country and this aid calling the method getQ1Songs of the DataModel class
                        paid= req.getParameter("paid");
                        country = req.getParameter("pcountry");
                        listSongs= dataModel.getQ1Songs(country, paid);
                        //The auto mode is required
                        if((reqAutomode != null) && (reqAutomode.equals("true"))){
                            frontEnd.respAutoPhase13(res, listSongs);
                        } else{
                            frontEnd.doGetPhase13(res, country,listSongs, passwd, paid);
                        }
                    }
                    break;
                }
            }
        }catch(Exception e){
        
        }
    }
}

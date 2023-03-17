package p2;

import java.io.*;
import java.util.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import p2.*;
import java.net.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

//This class is in charge of collecting all the required data (countries, albums and songs). Moreover, in this class all the files are parsed and classified in error, fatalerror and correct files.
class DataModel{

    //The map of correct files
    public static TreeMap<String,Document> mapDocs = new TreeMap<String,Document>(); //The year with its document (only the correct documents)
    //The list with all names of MUML files found in the first document
    public static ArrayList<String> allUrls = new ArrayList<String>();
    //The list with all names of MUML files found in the rest of documents
    public static ArrayList<String> allUrls2 = new ArrayList<String>();
    //The lists of error and fatalerror files. One is for broser mode, collecting the mumls starting with http, and the other is for auto mode that has only the mumlXXX.xml names. This is because of the screens, that are different in both modes.
    public static ArrayList<String> errorsFile = new ArrayList<String>();
    public static ArrayList<String> errorsFileAUTO = new ArrayList<String>();
    public static ArrayList<String> fatalerrorsFileAUTO = new ArrayList<String>();
    public static ArrayList<String> fatalerrorsFile = new ArrayList<String>();
    //To know what files has to be parsed
    public static LinkedList<String> pendientes = new LinkedList<String>(); 
    //To parse the files
    DocumentBuilderFactory dbf=null;
    DocumentBuilder db=null;
    
    //Method used to get all the countries that are in the documents. 
    public ArrayList<String> getQ1Countries() throws ParserConfigurationException, SAXException, IOException{ // We get the list of countries from the documents of the map

        //The list of all countries
        ArrayList<String> countriesMap= new ArrayList<String>();  
        try{   

            //To go all over the map with the correct files
            for (String key : mapDocs.keySet()) {
            //It gets the document that we need to read its information 
            Document docUrl = mapDocs.get(key);
            Node root = docUrl.getDocumentElement(); 
            //To collect the country elements it is used the getElementByTagName with the tag "Country"
            NodeList nl = docUrl.getElementsByTagName("Country");
            Element elemCountry;
            String countryName;
            
            //To collect all the country elements found
            for (int x = 0; x < nl.getLength(); x++) {
                elemCountry = (Element)nl.item(x);
                //It gets the text (string) of the element
                countryName = elemCountry.getTextContent();
                //To not repeat the countries that are stored
                if (!countriesMap.contains(countryName)) {
                    countriesMap.add(countryName); 
                }

            }
            
            }
            //To order in reverse alphabetical order
            Collections.sort(countriesMap);
            Collections.reverse(countriesMap);
        }catch(Exception e){
            
        }
        //It returns the list of all countries
        return countriesMap;
    }

    //Method used to get all the albums of a given country that are in the documents. It is used XPATH
    public ArrayList<Album> getQ1Albums (String country) { 
        //The list of all albums
        ArrayList<Album> albumsMap= new ArrayList<Album>();
        try{
            //To go all over the map with the correct files
            for (String key : mapDocs.keySet()) {
                //It gets the document that we need to read its information
                Document docUrl = mapDocs.get(key);
                //XPATH is used to get the year of the documents first, and then all the albums specifying the country
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                String xPathTargetYear =  "/Music/Year"; 
                NodeList YearDoc = (NodeList)xpath.evaluate(xPathTargetYear,docUrl, XPathConstants.NODESET );
                Element elemYear= (Element)YearDoc.item(0);
                String YearString = elemYear.getTextContent().trim();
                String xPathTarget =  "//Album[Country='"+country+"']";  //albums of a given country
            
                NodeList allAlbumsCountry= (NodeList)xpath.evaluate(xPathTarget,docUrl, XPathConstants.NODESET );
                //To get all the found albums
                for( int i=0; i< allAlbumsCountry.getLength(); i++){
                    Element elemAlbum= (Element)allAlbumsCountry.item(i); //album by album
                    
                    //To get the Review
                    String xPathTargetReview =  "text()[normalize-space()]";
                    String reviewAlbum= (String)xpath.evaluate(xPathTargetReview,elemAlbum, XPathConstants.STRING);
                    String reviewAlb = reviewAlbum.trim();
                    //To get the format (it is an attribute)
                    String format = elemAlbum.getAttribute("format").trim();
                    //To get the aid (it is and attribute)
                    String aid = elemAlbum.getAttribute("aid").trim();
                    //To get the Name
                    NodeList nameAlbum= elemAlbum.getElementsByTagName("Name");
                    Element elemNameAlbum = (Element)nameAlbum.item(0);
                    //It gets the text (string) of the element
                    String nameAlb = elemNameAlbum.getTextContent().trim();
                    //To get the Country
                    NodeList countryAlbum= elemAlbum.getElementsByTagName("Country");
                    Element elemCountryAlbum = (Element)countryAlbum.item(0);
                    //It gets the text (string) of the element
                    String countryAlb = elemCountryAlbum.getTextContent().trim();
                    //To get the Performer
                    NodeList singerAlbum =elemAlbum.getElementsByTagName("Singer");
                    String singerAlb= null;
                    Element elemSingerAlbum;
                    //The singer could be null because maybe the performer is a group
                    if((elemSingerAlbum = (Element)singerAlbum.item(0))!=null){
                    //It gets the text (string) of the element
                    singerAlb = elemSingerAlbum.getTextContent().trim();
                    } 
                    NodeList groupAlbum= elemAlbum.getElementsByTagName("Group");
                    String groupAlb=null;
                    Element elemGroupAlbum;
                    //The group could be null because maybe the performer is a singer
                    if((elemGroupAlbum = (Element)groupAlbum.item(0))!=null){
                    //It gets the text (string) of the element
                    groupAlb = elemGroupAlbum.getTextContent().trim();
                    }
                    //To get the ISBN
                    NodeList ISBNAlbum= elemAlbum.getElementsByTagName("ISBN");
                    Element elemISBNAlbum = (Element)ISBNAlbum.item(0);
                    //It gets the text (string) of the element
                    String ISBNAlb = elemISBNAlbum.getTextContent().trim();
                    //To get the Company
                    NodeList companyAlbum= elemAlbum.getElementsByTagName("Company");
                    Element elemCompanyAlbum = (Element)companyAlbum.item(0);
                    //It gets the text (string) of the element
                    String companyAlb = elemCompanyAlbum.getTextContent().trim();

                    //To call the constructor with the singer or group we need to know which one is null
                    if(singerAlb!=null){
                        Album AlbumObj = new Album(nameAlb,countryAlb,singerAlb,ISBNAlb,companyAlb,aid,YearString,reviewAlb,format);
                        //The album is added to the list
                        albumsMap.add(AlbumObj);
                    }
                    if(groupAlb!=null){
                        Album AlbumObj = new Album(nameAlb,countryAlb,groupAlb,ISBNAlb,companyAlb,aid,YearString,reviewAlb,format);
                        //The album is added to the list
                        albumsMap.add(AlbumObj);   
                    }
                }
            }
        }catch(Exception e){
        }
        //The albums must be in alphabetical order
        Collections.sort(albumsMap);
        //It returns the list of all found albums
        return albumsMap;
        
    }

    //Method used to get all the pop songs of a given country and a given album that are in the documents. It is used XPATH
    public ArrayList<Song> getQ1Songs (String country, String album) { 
        //List of all found songs 
        ArrayList<Song> songsMap= new ArrayList<Song>();
        try{
            //To go all over the map with the correct files
            for (String key : mapDocs.keySet()) {
                //It gets the document that we need to read its information
                Document docUrl = mapDocs.get(key);
                //XPATH is used to collect all the songs of a given country and a given album and that has a Pop genre
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                String xPathTarget =  "//Album[@aid='"+album+"'][Country='"+country+"']/Song[Genre='Pop']"; //asi tenemos todos las songs de un album

                NodeList allSongsAlbums= (NodeList)xpath.evaluate(xPathTarget,docUrl, XPathConstants.NODESET );
                //To get all the found songs
                for( int i=0; i< allSongsAlbums.getLength(); i++){
                    Element elemSong= (Element)allSongsAlbums.item(i); //song by song
                    
                    //To get the sid (it is an attribute)
                    String sid = elemSong.getAttribute("sid");
                    //To get the langage (it is an attribute)
                    String lang = elemSong.getAttribute("lang");
                    //To get the Title
                    NodeList titleSong= elemSong.getElementsByTagName("Title");
                    Element elemTitleSong = (Element)titleSong.item(0);
                    //It gets the text (string) of the element
                    String titleS = elemTitleSong.getTextContent().trim();
                    //To get the Duration
                    NodeList durationSong= elemSong.getElementsByTagName("Duration");
                    Element elemDurationSong = (Element)durationSong.item(0);
                    //It gets the text (string) of the element
                    String durationS = elemDurationSong.getTextContent().trim();
                    //To get the Genres
                    NodeList genreSong= elemSong.getElementsByTagName("Genre");
                    //To collect the genres because they could be more than one
                    String genreS[]=new String[genreSong.getLength()];
                    //To get all the genres
                    for(int x=0; x< genreSong.getLength();x++){
                        Element elemGenreSong = (Element)genreSong.item(x);
                        //It gets the text (string) of the element
                        genreS[x] = elemGenreSong.getTextContent().trim();
                    }
                    //An array is created to get a string with the genres separated with ','
                    ArrayList<String> list = new ArrayList<String>();
                    for(int index=0; index<genreS.length; index++){
                        list.add(genreS[index]);
                    }
                    //The genres into a string
                    String genresToString = String.join(", ", list);

                    //To get the Composer
                    NodeList composerSong= elemSong.getElementsByTagName("Composer");
                    String composerS=null;
                    Element elemComposerSong;
                    //It could be null because it is optional
                    if((elemComposerSong = (Element)composerSong.item(0))!=null){
                        //It gets the text (string) of the element
                        composerS = elemComposerSong.getTextContent().trim();
                    }
                    //To get the MUML
                    NodeList muMLSong= elemSong.getElementsByTagName("MuML");
                    Element elemmuMLSong;
                    String muMLS=null;
                    //It could be null because it is optional
                    if((elemmuMLSong= (Element)muMLSong.item(0))!=null){
                        //It gets the text (string) of the element
                        muMLS = elemmuMLSong.getTextContent().trim();
                    }
                    //Creating a song calling tha class Song
                    Song SongObj = new Song(composerS,titleS,durationS,genresToString,muMLS,lang,sid);
                    //Adding the song to the list
                    songsMap.add(SongObj);

                }
            }
        }catch(Exception e){
        }
        //The songs must be in alphabetical order
        Collections.sort(songsMap);
        //It returns the list of all found songs
        return songsMap;

        
    }

    //To find the year of the document using XPATH
    public String catchYear(Document documento){
        String YearString=null;
        try{
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            //To get the year of the file
            String xPathTargetYear =  "/Music/Year"; 
            NodeList YearDoc = (NodeList)xpath.evaluate(xPathTargetYear,documento, XPathConstants.NODESET );
            Element elemYear= (Element)YearDoc.item(0);
            //It gets the text (string) of the element
            YearString = elemYear.getTextContent().trim();
        }catch(Exception e){

        }
        //The year is returned
        return YearString;
    }

    //To find the mumls of the document using XPATH
    public ArrayList<String> catchUrls(Document documento){
        //The list with all the mumls
        ArrayList<String> allUrls= new ArrayList<String>();
        try{
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            //To get all the mumls of the file
            String xPathTargetMML =  "//MuML"; 
            NodeList MMLDoc = (NodeList)xpath.evaluate(xPathTargetMML,documento, XPathConstants.NODESET );
            for( int x=0; x< MMLDoc.getLength();x++){
                Element elemMML= (Element)MMLDoc.item(x);
                String MMLString = elemMML.getTextContent().trim();
                //To not repeat mumls
                if(allUrls.indexOf(MMLString)<1){
                    allUrls.add(MMLString); 
                }
            }
        }catch(Exception e){}
        //The mumls are returned
        return allUrls;
    }

    //This method parsers the files and classifies them into error, fatalerror and correct documents.
    public void parserDocs(){
        //The parameters are initialized
        String yearOfDoc="";
        String urlToParse="";
        Boolean wellform=false;
        Document doc=null;
        Document docNext=null;
        //The general URL and the URL of the first document
        String urlGeneral= "http://alberto.gil.webs.uvigo.es/SINT/22-23/";
        String firstUrl="http://alberto.gil.webs.uvigo.es/SINT/22-23/muml2001.xml";
        String firstUrlAUTO ="muml2001.xml";

        PrintWriter out=null;
        try{
            //The first document is parsed
            URL url = new URL(firstUrl);
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.parse(url.openStream()); 
            //To know if the document is wellformed or not
            wellform=true;
            
        }catch(Exception e){
                //It is not wellformed, there is a fatalerror 
                //It is added to the list of fatalerror files
                fatalerrorsFile.add(firstUrl);
                fatalerrorsFileAUTO.add(firstUrlAUTO);
                wellform=false;
        }

        //It is wellformed. Now we have to verify if it is correct or it has an error in the year
        if(wellform){
            //To get all mumls of the file
            allUrls= catchUrls(doc);
            //To get the year of the file
            yearOfDoc= catchYear(doc); 
            int yearOfDocInt = Integer.parseInt (yearOfDoc);
            //To know if the year is correct
            if(yearOfDocInt >= 1980 && yearOfDocInt <=2021){ 
                //It is correct, so it is added to the map of correct files
                mapDocs.put(yearOfDoc,doc); 
                //The found mumls are stored in the list of pendientes because they are going to be parsed
                for(int index=0; index<allUrls.size(); index++){
                    //To not repeat
                    if(pendientes.indexOf(allUrls.get(index))<1){
                        pendientes.add(allUrls.get(index)); 
                    }
            }
            }else{
                //The year is wrong, so the file is added to the list of error files
                errorsFile.add(firstUrl); 
                errorsFileAUTO.add(firstUrlAUTO);
            }
        }
        //While there is a file to be parsed
        while(!pendientes.isEmpty()) {
            try{
                //To get the url
                urlToParse= urlGeneral+pendientes.get(0);
                //The document is parsed
                docNext= db.parse(urlToParse);
                //To know if the document is wellformed or not
                wellform=true;

            }catch(Exception e){
                //It is not wellformed, there is a fatalerror 
                //It is added to the list of fatalerror files
                fatalerrorsFile.add(urlToParse);
                fatalerrorsFileAUTO.add(pendientes.get(0));
                //The file is removed from pendientes list because it is already analyzed
                pendientes.pop();
                wellform=false;
            }
             //It is wellformed. Now we have to verify if it is correct or it has an error in the year
            if(wellform){
                //To get all the MUMLs of a file
                allUrls2= catchUrls(docNext); 
                //To get the year of a file
                String yearOfDocNext= catchYear(docNext); 
                int yearOfDocNextInt = Integer.parseInt (yearOfDocNext);
                if(yearOfDocNextInt >= 1980 && yearOfDocNextInt <=2021){
                    //It is correct, so it is added to the map of correct files
                    //The file is removed from pendientes list because it is already analyzed
                    pendientes.pop();
                    mapDocs.put(yearOfDocNext,docNext); 
                    //The found mumls are stored in the list of pendientes because they are going to be parsed
                    for(int index=0; index<allUrls2.size(); index++){
                        //To not repeat
                        if(pendientes.indexOf(allUrls2.get(index))<1){
                            pendientes.add(allUrls2.get(index)); 
                        }
                    }
                } else{
                    //The year is wrong, so the file is added to the list of error files
                    errorsFile.add(urlToParse); 
                    errorsFileAUTO.add(pendientes.get(0));
                    //The file is removed from pendientes list because it is already analyzed
                    pendientes.pop();
                }

            }
        }
    }
    //To get the list of files with errors (in auto mode)
    public ArrayList<String> getErrorsFilesAUTO(){
        return errorsFileAUTO;
    }
    //To get the list of files with fatal errors (in auto mode)
    public ArrayList<String> getFatalErrorsFilesAUTO(){
        return fatalerrorsFileAUTO;
    }             
    //To get the list of files with errors (in browser mode)
    public ArrayList<String> getErrorsFiles(){
        return errorsFile;
    }
    //To get the list of files with fatal errors (in browser mode)
    public ArrayList<String> getFatalErrorsFiles(){
        return fatalerrorsFile;
    }

}


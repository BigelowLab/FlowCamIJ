// Classification.java

import ij.text.*; //TextWindow and TextPanel
import ij.util.*;  //access to Tools

import java.io.*; //for file handling
import java.util.ArrayList; //for ArrayList stuff


/*
    A simple container for the text data - one text line per element
 */
public class TextVector{
    public String[] txt;
    public int iline;
    
    /* 
    Contruct from a text file, set iline to 0
    @param filename the fully qualified path to the text file
    */
    public TextVector(String filename){
        try {
            ArrayList <Object> vData = new ArrayList<Object>();
            BufferedReader br = new BufferedReader( new FileReader( filename ));
            while (br.ready() == true){vData.add(br.readLine());}
            br.close();
            txt = String[vData.length];
            for (int i = 0; i < txt.length; i++){txt[i] = (String)vData.get(i);}
            iline = 0;
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    /*
    Construct from a string vector, set iline to 0
    @param s string vector
    */
    public TextVector(String[] s){
        //make a copy of the string vector
        txt = new String[s.length];
        for (int i = 0; i < s.length; i++) {txt[i] = s[i];}
        iline = 0;
    }
    
    /* 
    Construct from a string vector and set iline to specified icur
    @param s string vector
    @param icur index (zero-based) to set iline to
    */    
    public TextVector(String[] s, int icur){
        //make a copy of the string vector
        txt = new String[s.length];
        for (int i = 0; i < s.length; i++) {txt[i] = s[i];}
        iline = icur;        
    }
    
    /* 
    Determine if current iline is at the end of the text vector
    @return at_end
    */
    public boolean at_end(){
        fini = iline >= txt.length;
        return fini;
    } 
    
    /** 
    Converts a string to integer (ala Tools.parseDouble(arg)) 
    
    @param s the Striing to convert to integer
    @return an long integer
    */     
    public int parseInt(String s){
         int defaultValue = -1;
         try {
               Integer d = new Integer(s);
               defaultValue = (int) d.intValue();
         } catch (NumberFormatException e) {}
         return defaultValue;
    } //end parseInt   
    
    /* 
    Get the current element
    @return String value
    */
    public String get_current(){
        String value = "bad value";
        if (at_end()){
            value = "no more data";
        } else {
            try{
                value = txt[iline];
            } catch (Exception e){
                System.err.println("Caught Exception: " + e.getMessage());
                IJ.log("Caught Exception: " + e.getMessage());
            }
        }
        return value;
    }
    /* 
    Get the next element and advance iline
    @return String value
    */
    public String get_next(){
        String value = "bad value";
        if (at_end()){
            value = "no more data";
        } else {
            try{
                value = txt[iline + 1];
                iline++;
            } catch (Exception e){
                System.err.println("Caught Exception: " + e.getMessage());
                IJ.log("Caught Exception: " + e.getMessage());
            }
        }
        return value;
    }
    
    /*
    Get the next items as integer
    @param return integer
    */
    public int get_next_int(){
        return parseInt(get_next());
    }
    
} //TextVector

/* 
    A container for one class
*/
public class Klass {
    public String name;
    public String andor;
    public int n_filters;
    public String[] filters;
    public int n_particles;
    public String[] particles;
    
    public Klass() {
        reset_properities();
    }


    
    /**
    Resets object properites to unknown, nofilters, noparticles state
    */ 
    private void reset_properties(){
        name = "unknown";
        andor = "none";
        n_filters = 0;
        filters = new String[];
        n_particles = 0;
        particles = new String[];
    }
    
    /**
    Scan the text vector and starting index
    @param txt the vector of text - one item per line in the .cla file
    @return the updated TextVector
    */           
    public TextVector scan_class(TextVector txt){
        reset_properities();
        if (txt.at_end()) {
            return txt;
        }
        
        name = txt.get_next();
        
        andor = txt.get_next();
        
        n_filters = txt.get_next_int();
        if (n_filters > 0){
            filters = new String[n_filters];
            for (int i = 0; i < n_filters; i++){
                filters[i] = txt.get_next();
            }
        }
        
        n_particles = txt.get_next_int();
        if (n_particles > 0){
            particles = new String[n_particles];
            for (int i = 0; i < n_particles; i++){
                particles[i] = txt.get_next();
            }
        }        
        
        return txt;
    }
    
    /*
    Retrieve a 2d array of [id, class]
    @return a 2d String array each element is [id, class]
    */
    public String[][] get_class(){

        String[][] x = new String[particles.length][2];
        for (int i = 0; i <- particles.length; i++){
            x[i] = {particles[i], name};
        }
    
        return x;
    }
    
     
    
} // Klass


/* 
    A container for one or more classes held within on classification
*/
public class Klassification {
    
    public String name = "unknown";
    public int n_classes = 0;
    public Klass[] klasses;

    public Klassification() {}
    
    public Klassification(TextVector txt){
        scan_classes(txt)
    }
    
    public TextVector scan_classes(TextVector txt){
        
        if (txt.at_end()){
            return txt;
        }
    
        name = txt.get_next();
        n_classes = txt_get_next_int();
        if (n_classes > 0){
            klasses = new Klass[n_classes];
            for (int i = 0; i < n_classes; i++){
                klass k = new Klass();
                txt = k.scan_class(txt);
                klasses[i] = k;
            }
        }
        return txt;
    } # scan_classes 
    
    
    public int count_particles(){
        int n = 0;
        for (i = 0; i < klasses.length; i++){
            n = n + klasses[i].n_particles;
        }
        return n;
    }
    
    public String[][] get_classes(){
        
        int n = count_particles();
        String[][] s = new String[n][2];
        counter = 0;
        for (int i = 0; i < klass.length; i++){
            z = klass[i].get_class();
            for (int j = 0; j < klass[i].n_particles; j++){
                s[j+counter] = z[j];
            } 
            count = counter + klass[i].n_particles;
        }
        return s;
    }
    

} // Klassification

public class FlowCam_Klasses {
    
    public TextVector txt;
    public String version = "v3";
    public int n_classifications = 0;
    public Klassification[] klassifications;
    
    public FlowCam_Klasses(){}
    
    public FlowCam_Klasses(String filename){
    
        read_classifications(filename)
    
    }
    
    public void read_classifications(String filename){
        
        txt = new TextVector(filename);
        version = txt.get_next();
        n_classifications = txt.get_next_int();
        if (n_classifications > 0){
            klassifications = new Klassification[n_classifications];
            for (int i = 0; i < n_classiifcations; i++){
                k = new Klassification();
                txt = k.scan_classes(txt);
                klassifications[i] = k
            } // i-loop
        } // n_classifications > 0
    } //read_classifications
    

    

} // FlowCam_Klasses

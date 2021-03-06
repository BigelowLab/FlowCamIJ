// TODO throw exceptions?

import ij.*;
import ij.text.*; //TextWindow and TextPanel
import ij.util.*;  //access to Tools

import java.io.*; //for file handling
import java.util.ArrayList; //for ArrayList stuff


public class Spreadsheet {
  public String version = "3.4.5";
  /** The delimiter that separates fields of data in each record - by default tab ("\t") */
  public String delim = ",";
  /** State variable indicating the state of each row */
  public boolean dataAsArray = false;
  /** The vector that contains arbitrary lines of header info (if any)*/
  public ArrayList <Object>vHeader = new ArrayList<Object>();
  /** The vector of column names */
  public ArrayList <Object>vFieldnames = new ArrayList<Object>();
  /** The vector of data */
  public ArrayList <Object> vData = new ArrayList<Object>();
  /** Common legacy names - use matchColumnName() to get verision dependent equivalent */
  public String[] commonFieldNames = {"collage_file", "scr_image", "cal_image",
        "image_w", "image_h", "image_x", "image_y", 
        "src_x", "src_y",
        "sigma_intensity"};
/** 
  * a generic constructor
  */
  public Spreadsheet(){}
  
/** Creates and instance of this class and reads in the given file.  The files should be a simple 
  * ASCII delimited file of columns of data where the first row contains the column names (fieldnames)
  *
  * @param file the fully qualified filename to read
  */
  public Spreadsheet(String file){
    boolean bOK = readFile(file);
  } 

/** Creates and instance of this class and reads in the given file.  The files should be a simple 
  * ASCII delimited file of columns of data where the first row contains the column names (fieldnames)
  *
  * @param file the fully qualified filename to read
  * @param version the 'SoftwareVersion' number from the context file
  */  
  public Spreadsheet(String file, String version){
    this.version = version;
    boolean bOK = readFile(file);
  }


/***
   * Adds a row to the end of the data vector
   *
   * 

/**
  * Sets an element in the data.
  * 
  * @param colIndex the index of the column to set.
  * @param rowIndex the row index (essentially the vector index)
  * @param value the value to assign the element
  * @return a boolean indicating success
  */
  public boolean setDataElement(int colIndex, int rowIndex, String value){
    split();
    boolean ret = true;
    //IJ.log("dataAsArray=" + dataAsArray);
    //IJ.log("line= " + ((Object) vData.get(rowIndex)).toString());
    String[] d = (String[]) vData.get(rowIndex);
    d[colIndex] = value;
    try{
      vData.set(rowIndex, d);
    } catch (Exception e){
      ret = false;;
    }
    return ret;
  }//setDataElement
  
/** 
  * Set the data element in the row, column specified by number and name
  *.
  *
  * @param fieldname the field name (will be converted to column index)
  * @param rowIndex the row index (essentially the vector index)
  * @param value the value to assign the element
  * @return a boolean indicating success
  */
  public boolean setDataElement(String fieldname, int rowIndex, String value){
    int colIndex = getFieldIndex(fieldname);
    if (colIndex != -1){
      return setDataElement(colIndex, rowIndex, value);
    } else {
      return false;
    }
  }
  
/**
  * Sets the values in a column of vData.  Be advised
  * that no check is made to ensure that the vData and values have the
  * same number of elements.
  * 
  * @param colIndex the index of the column to set.
  * @param value the values to assign to the elements in the column
  * @return a boolean indicating success
  */
  public boolean setDataColumn(int colIndex, String[] value){
    split();
    for (int i = 0; i<value.length;i++){setDataElement(colIndex, i,value[i]);}
    return true;
  }// setDataColumn
      
/**
  * Sets the values in a column by column name.  Be advised
  * that no check is made to ensure that the vData and values have the
  * same number of elements.
  * 
  * @param fieldname the name of the column to set.
  * @param value the values to assign to the elements in the column
  * @return a boolean indicating success
  */
  public boolean setDataColumn(String fieldname, String[] value){
    int colIndex = getFieldIndex(fieldname);
    if (colIndex != -1){
      return setDataColumn(colIndex, value);
    } else {
    return false;
    }
  }// setDataColumn
  
/**
  * Append column to the data
  * @param fieldname the name of the field to append
  * @param value the values to append
  * @return a boolean indication success
  */
  public boolean appendDataColumn(String fieldname, String[] value){
  
      if (value.length != size()){
         IJ.showMessage("Spreadsheet.appendDataColumn","Provided column must have same length as data has rows");
         return(false);
      }
      
      join();
      String s="";
      if (vFieldnames.size() !=0){
         s = (String) vFieldnames.get(0)+ delim + fieldname;
         vFieldnames.set(0, s);
      }
      
      for (int i = 0; i<vData.size();i++){
          s = (String) vData.get(i) + delim + value[i];            
          vData.set(i, s);
      }
      dataAsArray = false;
      return !dataAsArray;
  }
/** Returns a group of data elements specified by column index and row indices.
  * 
  * @param colIndex The index of the column that contains the requested data.
  * @param rowIndices The indicies of the data to return
  * @return a String array of elements.
  */
  public String[] getDataColumn(int colIndex, int[] rowIndices){
    String[] r = new String[rowIndices.length];
    for (int i=0; i<rowIndices.length;i++){
      r[i] = getDataElement(colIndex, rowIndices[i]);
    }
    return r;
  }
  
/** Returns a group of data elements specified by column name and row indices.
  * 
  * @param fieldname The name of the column that contains the requested data.
  * @param rowIndices The indicies of the data to return
  * @return a String array of elements.
  */
  public String[] getDataColumn(String fieldname, int[] rowIndices){
    int colIndex = getFieldIndex(fieldname);
    return getDataColumn(colIndex, rowIndices);
  }
  
/**
  * Returns a column of data.
  *
  * @param colIndex the index of the column to retrieve.
  * @return a String array of values 
  */
  public String[] getDataColumn(int colIndex){
    split();
    String[] r = new String[vData.size()];
    for (int i = 0; i<vData.size();i++){r[i] = getDataElement(colIndex, i);}
    return r;
  }//getDataColumn
  
/**
  * Returns a column of data as specified by column name
  *
  * @param fieldname the name of the column to retrieve.
  * @return a String array of values 
  */
  public String[] getDataColumn(String fieldname){
    int colIndex = getFieldIndex(fieldname);
    if (colIndex != -1){
      return getDataColumn(colIndex);
    } else {
      return null;
    }
  }//getDataColumn

/**
  * Returns a column of int data from the ArrayList of String arrays.
  *
  * @param colIndex the index of the column to retrieve.
  * @return a double array of values 
  */
  public int[] getDataColumnInt(int colIndex){
    split();
    int[] r = new int[vData.size()];
    for (int i = 0; i<vData.size();i++){
      r[i] = getDataElementInt(colIndex, i);
    }
    return r;
  }//getDataColumnint
     
/**
  * Returns a column of int data from the ArrayList of String arrays.
  *
  * @param fieldname the name of the column to retrieve.
  * @return a double array of values 
  */
  public int[] getDataColumnInt(String fieldname){
    int colIndex = getFieldIndex(fieldname);
    if (colIndex != -1){
      return getDataColumnInt(colIndex);
    } else {
      return null;
    }
  }//getDataColumnInt  
  
/**
  * Returns a column of long data from the ArrayList of String arrays.
  *
  * @param colIndex the index of the column to retrieve.
  * @return a double array of values 
  */
  public long[] getDataColumnLong(int colIndex){
    split();
    long[] r = new long[vData.size()];
    for (int i = 0; i<vData.size();i++){
      r[i] = getDataElementLong(colIndex, i);
    }
    return r;
  }//getDataColumnLong
     
/**
  * Returns a column of long data from the ArrayList of String arrays.
  *
  * @param fieldname the name of the column to retrieve.
  * @return a double array of values 
  */
  public long[] getDataColumnLong(String fieldname){
    int colIndex = getFieldIndex(fieldname);
    if (colIndex != -1){
      return getDataColumnLong(colIndex);
    } else {
      return null;
    }
  }//getDataColumnLong  
  
/**
  * Returns a column of double data from the ArrayList of String arrays.
  *
  * @param colIndex the index of the column to retrieve.
  * @return a double array of values 
  */
  public double[] getDataColumnDouble(int colIndex){
    split();
    double[] r = new double[vData.size()];
    for (int i = 0; i<vData.size();i++){
      r[i] = getDataElementDouble(colIndex, i);
    }
    return r;
  }//getDataColumnDouble
     
/**
  * Returns a column of double data from the ArrayList of String arrays.
  *
  * @param fieldname the name of the column to retrieve.
  * @return a double array of values 
  */
  public double[] getDataColumnDouble(String fieldname){
    int colIndex = getFieldIndex(fieldname);
    if (colIndex != -1){
      return getDataColumnDouble(colIndex);
    } else {
      return null;
    }
  }//getDataColumnDouble
     


/**
  * Returns element specified
  * 
  * @param colIndex the column index of the element (essentially the index into 
  *   the array located in the vector at rowIndex
  * @param rowIndex the row index (essentially the vector index)
  * @return the element at [colIndex, rowIndex] as a String
  */
    public String getDataElement(int colIndex, int rowIndex){
      split();
      return ((String[])vData.get(rowIndex))[colIndex];
    }//getDataElement

/**
  * Returns element specified by column name and row number
  * 
  * @param colIndex the column index of the element (essentially the index into 
  *   the array located in the vector at row
  * @param rowIndex the row index (essentially the vector index)
  * @return the element at [colIndex, rowIndex] as a String
  */
    public String getDataElement(String fieldname, int rowIndex){
      int colIndex = getFieldIndex(fieldname);
      if (colIndex != -1){
        return getDataElement(colIndex, rowIndex);
      } else {
        return null;
      }
    }//getDataElement
  
 
/**
  * Returns element specified as a int.
  * 
  * @param colIndex the column index of the element (essentially the index into 
  *   the array located in the vector at row)
  * @param rowIndex the row index (essentially the vector index)
  */
    public int getDataElementInt(int colIndex, int rowIndex){
      return parseInt(getDataElement(colIndex, rowIndex));
    }//getDataElementInt    

/**
  * Returns element specified by column and row number as a int 
  * - it is up to the user to SPLIT the data first!
  * 
  * @param fieldname the name of the field (column name)
  * @param rowIndex the row index (essentially the vector index)
  */
    public int getDataElementInt(String fieldname, int rowIndex){
      int colIndex = getFieldIndex(fieldname);
      if (colIndex != -1){
        return parseInt(getDataElement(colIndex, rowIndex));
      } else {
        return parseInt("");
      }
    }//getDataElementInt   
       
/**
  * Returns element specified as a long - 
  * it is up to the user to SPLIT the data first!
  * 
  * @param colIndex the column index of the element (essentially the index into 
  *   the array located in the vector at row)
  * @param rowIndex the row index (essentially the vector index)
  */
    public long getDataElementLong(int colIndex, int rowIndex){
      return parseLong(getDataElement(colIndex, rowIndex));
    }//getDataElementLong    

/**
  * Returns element specified by column and row number as a long 
  * - it is up to the user to SPLIT the data first!
  * 
  * @param fieldname the name of the field (column name)
  * @param rowIndex the row index (essentially the vector index)
  */
    public long getDataElementLong(String fieldname, int rowIndex){
      int colIndex = getFieldIndex(fieldname);
      if (colIndex != -1){
        return parseLong(getDataElement(colIndex, rowIndex));
      } else {
        return parseLong("");
      }
    }//getDataElementLong    

  
    
/**
  * Returns element specified as a double - it is up to the user to SPLIT the data first!
  * 
  * @param colIndex the column index of the element (essentially the index into 
  *   the array located in the vector at row)
  * @param rowIndex the row index (essentially the vector index)
  */
    public double getDataElementDouble(int colIndex, int rowIndex){
      return Tools.parseDouble(getDataElement(colIndex, rowIndex));
    }//getDataElementDouble    

/**
  * Returns element specified by column and row number as a double 
  * - it is up to the user to SPLIT the data first!
  * 
  * @param fieldname the name of the field (column name)
  * @param rowIndex the row index (essentially the vector index)
  */
    public double getDataElementDouble(String fieldname, int rowIndex){
      int colIndex = getFieldIndex(fieldname);
      if (colIndex != -1){
        return Tools.parseDouble(getDataElement(colIndex, rowIndex));
      } else {
        return Tools.parseDouble("");
      }
    }//getDataElementDouble    

/**
  * Returns the fieldnames as a string array
  *
  *@return a string array of the fieldnames
  */
  public String[] getFieldnames(){
    String[] names;
    if (dataAsArray){
      names = (String[])vFieldnames.get(0);
    } else {
      names = ((String) vFieldnames.get(0)).split(delim);
    }
    return names;
  } //getFieldnames
  
/**
  * Match the data column name to the version
  * @param name the generic name of the column
  * @return the actual version dependent name
  */
    public String matchColumnName(String name){
    
        String[] R = new String[commonFieldNames.length];
        if (this.version.compareTo("3.4.5") == 0) {
            R = new String[] {"Image File", "Source Image", "Calibration Image",
                    "Image Height", "Image Width", "Image X", "Image Y",
                    "Capture X", "Capture Y",
                    "Sigma Intensity"};
        } else if (this.version.compareTo("2.0.0") == 0) {
            R = new String[]{"Filename", "Unknown", "Unknown",
                    "PixelH", "PixelW", "SaveX", "SaveY",
                    "CaptureX", "CaptureY",
                    "Sigma Intensity" };              
        } else {
            R = new String[] {"Image File", "Source Image", "Calibration Image",
                    "Image Height", "Image Width", "Image X", "Image Y",
                    "Capture X", "Capture Y",
                    "Sigma Intensity"};
        }
        
        int index = -1;
        for (int i = 0; i < commonFieldNames.length; i++){
            if ( commonFieldNames[i].equalsIgnoreCase(name)){
                index = i;
                break;
            }
        }
        if (index == -1) {
            return name;
        } else {
            return(R[index]); 
        }
    
    } 
    
/**
  * Prepends the specified string to each fieldname. For example, to prepend
  * "FIT_" to each fieldname do ...
  * objref.prependToFieldname("FIT_")
  * 
  * @param prependString the String to prepend with
  * @result a logical indicating success
  */
  public boolean prependToFieldname(String prependString){
    if (vFieldnames.size() == 0){ return false;}
  
    String[] fn;
    if (dataAsArray){
      fn = (String[]) vFieldnames.get(0);
    } else {
      fn = Tools.split(((String) vFieldnames.get(0)), delim);
    }
    
    for (int i = 0; i < fn.length; i++) fn[i] = prependString + fn[i];
    
    if (dataAsArray){
      vFieldnames.set(0, fn);
    } else {
      vFieldnames.set(0, join(fn));
    }
      
      
    return(true);
  }
/**
  * Returns the index of the field that matches the provided string (case independent)
  *
  * @param fieldname the name of the field index to fetch
  * @return returns the zero based index of the field that matches that proved string OR -1 upon failure
  */
  public int getFieldIndex(String fieldname){
    if (vFieldnames.size() == 0){ return -1;}
    String name = matchColumnName(fieldname);
    split();
    String[] a = (String[]) vFieldnames.get(0);;
    int index = -1;
    
    for (int i = 0; i< a.length;i++){
      //IJ.log(fieldname + " -> " + i + "=" + a[i].toString());
      if (a[i].equalsIgnoreCase(name)==true){
        index = i;
        break;
      }
    }
    return index;
  }
  
/**
  * Counts the number of columns
  * 
  * @return the number of columns as indicated by the number of fieldnames
  */
  public int countColumns(){
    if (vFieldnames.size() == 0){ return -1;}
    split();
    String[] a = (String[]) vFieldnames.get(0);
    return a.length;
  }

/**
  * Counts the number of columns
  * 
  * @return the number of columns as indicated by the number of fieldnames
  */
  public int ncol(){
    return countColumns();
  }
  
/**
  * Returns the number of elements in the data vector
  *
  * @return the number of data elements
  */
  public int size(){
    return vData.size();
  }
/**
  * Counts the number of rows
  * 
  * @return the number of rows
  */
  public int nrow(){
    return size();
  }  

/**
  * If the rows of data and the fieldnames are held as String Arrays
  * then this method will join them with the delimiter
  *
  * @return a boolean indicating success
  * @see split()
  */
  public boolean join(){
    //IJ.log("* join *");
    if (dataAsArray == false){return !dataAsArray;}
    String[] a;  //temporary string array
    String s;   //temporary string
    
    if (vFieldnames.size() !=0){
      a = (String[]) vFieldnames.get(0);
      s = a[0];
      for (int j = 1;j<a.length;j++){ s = s + delim + a[j];}
      vFieldnames.set(0, s);
    }
      
    if (vData.size() !=0){
      for (int i = 0; i<vData.size();i++){
          a = (String[])vData.get(i);
          s = a[0];
          if (a.length >= 1){
              for (int j=1; j<a.length;j++){
                  s = s +delim+ a[j];
              }
          }            
          vData.set(i, s);
      }
    }
    dataAsArray = false;
    //IJ.log("* isJoined *");
    return !dataAsArray;
  }//join
 
 
/**
  * Joins any given string array using the current delimiter
  *
  * @param arg A String array
  * @return a String of the input concatenated using the delimiter
  */
  public String join(String[] arg){
    String s = arg[0];
    for (int j = 1;j<arg.length;j++){ s = s + delim + arg[j];}    
    return s;
  } 
  
/** 
  * Splits the strings data and fieldnames into string arrays 
  * using the specified delimiter.  If the data are already split then it returns immediately,
  * with little overhead to call this method - call it often.
  *
  * @return a boolean indicating success
  * @see join()
  */
    public boolean split(){
      //IJ.log("* split *");
      if (dataAsArray == true) {return dataAsArray;}
      if (vFieldnames.size() != 0){ vFieldnames.set(0, Tools.split((String)vFieldnames.get(0), delim));}
      if (vData.size() != 0){
        for (int i = 0; i<vData.size();i++){ 
          vData.set(i, Tools.split((String)vData.get(i), delim));
        }
      }
      dataAsArray = true;
      //IJ.log("* isSplit *");
      return dataAsArray;
    }//split


    
/** Write the data to an ASCII text file, includes header and fieldnames.
  *
  * @param filename the fully qualified filename destination.
  * @return a boolean indicating success (true) or failure (false)
  */
  public boolean writeFile(String filename){
    return writeFile(filename, false, false);
  }
  
/** 
  * Writes the data to and ASCII text file, header and fieldnames are optional.
  * 
  * @param filename the fully qualified filename destination.
  * @param excludeHeader Set to true to exclude the header info (if any) from the output.
  * @param exlcudeFieldnames Set to true to exclude the fieldnames from the output.
  * @return a boolean indicating success (true) or failure (false)
  */
  public boolean writeFile(String filename, boolean excludeHeader, boolean excludeFieldnames){
    boolean OK = true;
    boolean isSplit = dataAsArray;
    if (dataAsArray==true){split();}
      try {
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        if ((excludeHeader == false) && (vHeader.size() != 0)){
          for (int i = 0; i<vHeader.size();i++){out.write( (String) vHeader.get(i) + "\n");}
        }
        if ((excludeFieldnames == false) && (vFieldnames.size() != 0)){
          out.write( (String) vFieldnames.get(0) + "\n");
        }
        if (vData.size() != 0) {
          for (int i = 0; i< vData.size(); i++){
            out.write( (String) vData.get(i) + "\n");
          }
        }
        out.close();
      } catch (IOException e) {
        IJ.log("Error writing to " + filename);
        OK = false;
      }
    if (isSplit==true){join();}
    return OK;
  }//writeFile

/** 
 * Reads data from a file and loads into the fieldnames and data vectors
 * Using this assumes the fieldnames are the first row
 * 
 * @param filename the fully qualified name of file to read
 * @return a boolean indicating success
*/ 
  public boolean readFile(String filename){ return readFile(filename, 0);}
  
/** 
 * Reads data from a file and loads into the fieldnames and data vectors,
 * using this skips the first "n" rows which are ready into the header vector.
 * 
 * @param filename the fully qualified name of file to read
 * @param nSkip the number of lines to skip before reding in the fieldnames row
 * @return a boolean indicating success
*/ 
  public boolean readFile(String filename, int nSkip){
    File file = new File( filename );
    String dummy = "";
    try {
      BufferedReader in = new BufferedReader(new FileReader(file));
      //empty out existing data
      vData.clear();
      vFieldnames.clear();
      vHeader.clear();
      
      //skip over these lines
      if (nSkip !=0 ){ 
        for (int i=0; i<nSkip; i++){
          if (in.ready() == true){vHeader.add(in.readLine());}
        }
      }
      //the next line should be the fieldnames
      if (in.ready()==true){ vFieldnames.add(in.readLine());}
        
      //the remaining lines are data
      while (in.ready() == true){vData.add(in.readLine());}
      
      in.close(); 
      
      } catch (FileNotFoundException e){
              IJ.showStatus("Error - file not found: " + filename);
              return false;
      } catch (IOException e){
              IJ.showStatus("Error - input output error: " + filename);
              return false;
      }    
      //return split();  2009-02-28 switched to simple true
      dataAsArray = false;
      String[] collagename = getDataColumn(matchColumnName("collage_file"));
      String[] collageslice = encodeSlicesFromString(collagename);
      boolean ok = appendDataColumn("collage_image", collageslice);
      return ok;
    } //readFile

/**
   Generate slice numbers from the names of collage_file column
   @param x a String array of names (collage_file usually)
   @return a String array of slice numbers, 1,2,3,...
   */
   public String[] encodeSlicesFromString(String[] x){
      String[] slice_name = new String[x.length];
      slice_name[0] = "1";
      int slice_num = 1;
      for (int i = 1; i < x.length; i++){
         if (x[i].equalsIgnoreCase(x[i-1])){
            slice_name[i] = slice_name[i-1];
         } else {
            slice_num++;
            slice_name[i] = String.valueOf(slice_num);
         }
      } //i-loop
      return slice_name;
   }

/**
  * Shows the contents in a new text window.
  *
  * @return An ImageJ TextWindow  or null.
  */
  public TextWindow showInTextWindow(String name){
    TextWindow tw = new TextWindow( name , "", 600, 400); 
    join();
    //tw.append("DELIM=" + (delim.equalsIgnoreCase("\t") ? "tab" : delim));
    tw.append((String)vFieldnames.get(0));
    for (int i = 0; i<vData.size();i++){tw.append((String) vData.get(i));}
    
    return tw;
  }

 
/**
  * Prints the contents of the vector to the ImageJ log window.
  * 
  */
  public void showInLog(){
    if (vHeader.size() != 0){
      for (int i = 0; i<vHeader.size();i++){IJ.log((String) vHeader.get(i));}
    }
    if (vFieldnames.size() != 0){
      String[] fieldnames = getFieldnames();
      String f = "";
      for (int i = 0; i < fieldnames.length;  i++){f = f + "," + fieldnames[i];}
      IJ.log(f);
    }
    if (vData.size() != 0){
    join();
      for (int i = 0; i < vData.size();  i++){ IJ.log((String) vData.get(i));}
    }
  }
  
//---------
//  Number Parsing
//---------
/** 
  * Converts a string to integer (ala Tools.parseDouble(arg)) 
  *
  * @param s the String to convert to integer
  * @return an long integer
  */
   public long parseLong(String s){
       long defaultValue = 0;
       try {
             Long d = new Long(s);
             defaultValue = d.longValue();
       } catch (NumberFormatException e) {}
       return defaultValue;
  } //end parseLong
/** 
  * Converts a string to integer (ala Tools.parseDouble(arg)) 
  *
  * @param s the Striing to convert to integer
  * @return an long integer
  */     
  public int parseInt(String s){
       int defaultValue = 0;
       try {
             Integer d = new Integer(s);
             defaultValue = (int) d.intValue();
       } catch (NumberFormatException e) {}
       return defaultValue;
  } //end parseInt   
}//end of class definition 

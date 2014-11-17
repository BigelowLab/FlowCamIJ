/**
   Use the FlowCam class as a container fot he various parts and peices that
   comprise a FlowCam run. 
   <p>
   <ul>
      <li> Context class for the context file
      <li> Spreadsheet class for the csv metadata
      <li> a single Calibration class shared by all images
      <li> Virtual stacks for collageImages, collageMaskImages, calImages, rawImages
   </ul>
   <p>
*/
import java.io.File;
import java.io.FilenameFilter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Exception;
import java.util.HashSet;
import java.util.Arrays;

import ij.IJ;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import ij.process.ByteProcessor;
import ij.process.BinaryProcessor;
import ij.ImagePlus;

public class FlowCam {
   
   public File inputDir;
   public String name;
   public File outputDir;

   public Context ctx;
   public Spreadsheet data;
   public ResultsTable rt;
   
   public String[] collageFilenames;
   public String[] collageMaskFilenames;
   public String[] calFilenames;
   public String[] rawFilenames;
   public String[] rawMaskFilenames;
   
   public Calibration cal;
   public FlowCamVirtualStackPlus collageImages;
   public FlowCamVirtualStackPlus rawImages;
   public FlowCamVirtualStackPlus calImages;
   public FlowCamVirtualStackPlus rawMaskImages;
   public FlowCamVirtualStackPlus collageMaskImages;
   
   /**
      Create an empty instance of FlowCam
   */
   public FlowCam(){
   }
   
   /**
      Create an instance of FlowCam configured to the specified path
      @param path the path to the FlowCam diretcory
   */
   public FlowCam(String path){
      boolean ok = init(path);
   } //FlowCam


   /*
      Initialize the basic components of FlowCam based upon the input path
      @param path the full path to the FlowCam run data
      @return boolean true is successful
   */
   public boolean init(String path){
      boolean ok = true;
      inputDir = new File(path);
      if (inputDir.exists()) {
         name = inputDir.getName();
         outputDir = new File(inputDir.getPath() + inputDir.separator + "_"+ this.name);
         if (!outputDir.exists()){
            if (!outputDir.mkdir()){
               IJ.log("Unable to create output directory: " + outputDir.getPath());
            }
         }
         try {
            ctx = new Context(inputDir.getPath() + File.separator + this.name + ".ctx");
         } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
         }
         data = new Spreadsheet(inputDir.getPath() + File.separator + this.name + ".csv",",");
         populateFileLists();
         setCalibration();
      } else {
         IJ.log("path not found: " + inputDir.getPath());
         ok = false;
      }
      if (ok){
         rt = new ResultsTable();
         rt.reset();
      }
      return ok;
   } //init
   
   /**
      Set the calibration (pixel size) for all loaded FlowCam images
      @param pixelWidth the pixel size in horizontal
      @param pixelHeight the pixel size in vertical
      @param unit the name of the unit (typically "um")
   */
   public void setCalibration(double pixelHeight, double pixelWidth, String unit){
      if (cal == null) cal = new Calibration();
      cal.pixelWidth = pixelWidth;
      cal.pixelHeight = pixelHeight;
      cal.setUnit(unit);
      if (rawImages != null) rawImages.setCalibration(cal.copy());
      if (calImages != null) calImages.setCalibration(cal.copy());
      if (collageImages != null) collageImages.setCalibration(cal.copy());
      if (collageMaskImages != null) collageMaskImages.setCalibration(cal.copy());
   } //setCalibration
   
   /*
    Set the calibration for all images based upon values in the Context file
   */
   public void setCalibration(){
      double pixelSize = ctx.getDouble("Fluid", "CalibrationConstant", 1.0);
      setCalibration(pixelSize, pixelSize, "um"); 
   }//setCalibration
   
   /**
      Load (but don't show) the image stack specified.  We don't auto load these for
      two reasons: (a) the Calibration may need to be set and (b) 
      there may be no need to load all - it depends upon the analysis.
      @param name the name of the image stack ["calImages","rawImages", "collageImages", "collageMaskImages", "all"]
   */
   public void loadStack(String iname){   
      if (iname.equalsIgnoreCase("rawImages")){
         rawImages = new FlowCamVirtualStackPlus(inputDir.getPath(), rawFilenames);
         rawImages.setCalibration(cal.copy());
      } else if (iname.equalsIgnoreCase("calImages")) {
         calImages = new FlowCamVirtualStackPlus(inputDir.getPath(), calFilenames);
         calImages.setCalibration(cal.copy());
      } else if (iname.equalsIgnoreCase("collageImages")) {
         collageImages = new FlowCamVirtualStackPlus(inputDir.getPath(), collageFilenames);
         collageImages.setCalibration(cal.copy());
      } else if (iname.equalsIgnoreCase("collageMaskImages")) {
         collageMaskImages = new FlowCamVirtualStackPlus(inputDir.getPath(), collageMaskFilenames);
         collageMaskImages.setCalibration(cal.copy());
      } else if (iname.equalsIgnoreCase("all")) {
         String[] inames = {"calImages","rawImages", "collageImages", "collageMasks"};
         for (int i = 0; i < inames.length ; i++){
            loadStack(inames[i]);
         }
      }
   } //loadStack
   
   /**
      Populate the file lists - self-explanatory? - for image files.  These are
      preloaded into calFilenames, rawFilenames, collageMaskFilenames and collageFilenames.
      We assume that the weight of preloading these is fairly light.  Be aware that
      if any are missing from the input directory then the string arrays may have
      zero length.
   */
   public void populateFileLists(){
     calFilenames = matchFiles(inputDir,"calImages"); 
     rawFilenames = matchFiles(inputDir, "rawImages"); 
     collageMaskFilenames = matchFiles(inputDir, "collageMaskImages"); 
     collageFilenames = getUnique(data.getDataColumn("collage_file"));
   } //poulateFileLists
   
   /*
      Match files given the specified regex pattern
      @see <a href= "http://stackoverflow.com/questions/14218412/regex-match-on-array-java" >hint from Evgeniy Dorofeevstack</a>
      @param dir the directory File object
      @param what the file types to match expression ["calImages", "rawImages", "collageImages", "collageMaskImages"]
      @return String array of zero or more matched filenames
   */
   public String[] matchFiles(File dir, String what){
   
      String[] files = {};
      
      if (what.equalsIgnoreCase("calImages")){
         files  = dir.list(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               return name.startsWith("cal_image_");
           }
         });      
      } else if (what.equalsIgnoreCase("rawImages")){
         files  = dir.list(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               return name.startsWith("rawfile_");
           }
         });       
      } else if (what.equalsIgnoreCase("collageMaskImages")){
          files  = dir.list(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               return name.endsWith("_bin.tif");
           }
         });      
      } else if (what.equalsIgnoreCase("collageImages")){
         //these are easiest to lift from the data file
         files = getUnique(data.getDataColumn("collage_file"));
      }
      
      return files;
   } //matchFiles
   
   /**
      Return a strng array of the unique elements in a string array
      @param array a String array
      @return a String array of the unique items in the input
   */
   public String[] getUnique(String[] array){
      String[] unique = {};
      HashSet<String> set = new HashSet<String>(Arrays.asList(array));
      unique = set.toArray( new String[0]);
      Arrays.sort(unique);
      return unique;
   } //getUnique
   
   /* 
      Retrieve the subset image coordinates for a subimage identified by 0-based index from stack identified by name ['collageImages', 'collageMaskImages', 'rawImages', 'calImages']
      @param index the subimage index, not that this is 0 based by FlowCam stores 1-based
      @param what the name of the image stack from which to retrieve the subimage 
      @return a four element int array of [x0,y0,w,h,slice]    
   */
   public int[] getSubsetCoords(int index, String what){
      int x0;
      int y0;
      int image_slice;
      int w = data.getDataElementInt("image_w", index);
      int h = data.getDataElementInt("image_h", index);
      if (what.startsWith("collage")){
         x0 = data.getDataElementInt("image_x", index);
         y0 = data.getDataElementInt("image_y", index);
         image_slice = data.getDataElementInt("collage_image", index);
      } else {
         x0 = data.getDataElementInt("scr_x", index);
         y0 = data.getDataElementInt("src_y", index);
         if (what.startsWith("raw")){
            image_slice = data.getDataElementInt("src_image", index);
         } else {
            image_slice = data.getDataElementInt("cal_image", index); 
         }
      }
      int[] r = {x0, y0, w, h, image_slice};
      return r;
   }
    
   /*
      Retrieve the image processor for a subimage identified by 0-based index from stack identified by name ['collageImages', 'collageMaskImages', 'rawImages', 'calImages']
      @param index the subimage index, not that this is 0 based by FlowCam stores 1-based
      @param what the name of the image stack from which to retrieve the subimage 
      @return a an image processor for the specified subset
   */
   public ImageProcessor createSubsetProcessor(int index, String what, int pad){
      int[] x = getSubsetCoords(index, what);
      ImageProcessor ip = null;
      if (what.equalsIgnoreCase("collageImages")){
         collageImages.setSlice(x[4]);
         ip = collageImages.createSubsetProcessor(x[0],x[1],x[2],x[3],pad);
      } else if (what.equalsIgnoreCase("collageMaskImages")){
         collageMaskImages.setSlice(x[4]);
         ip = collageMaskImages.createSubsetProcessor(x[0],x[1],x[2],x[3],pad);
      } else if (what.equalsIgnoreCase("rawImages")){
         rawImages.setSlice(x[4]);
         ip = rawImages.createSubsetProcessor(x[0],x[1],x[2],x[3],pad);
      } else if (what.equalsIgnoreCase("rawImages")){
         rawImages.setSlice(x[4]);
         ip = rawImages.createSubsetProcessor(x[0],x[1],x[2],x[3],pad);
      } 
      return ip;
   } //createSubsetProcessor
   /*
      Retrieve the image processor for a subimage identified by 0-based index from stack identified by name ['collageImages', 'collageMaskImages', 'rawImages', 'calImages']
      @param index the subimage index, not that this is 0 based by FlowCam stores 1-based
      @param what the name of the image stack from which to retrieve the subimage 
      @return a an image plus for the specified subset
   */
   public ImagePlus createSubsetImagePlus(int index, String what, int pad){
      ImageProcessor newIp = createSubsetProcessor(index, what, pad);
      if (newIp == null) { return null;}
      ImagePlus newImp = new ImagePlus("Subset-" + index, newIp);
      newImp.setCalibration(cal);
      return newImp; 
  }
   
   /*
      Retrieve the image processor for a subimage identified by 0-based index from stack identified by name ['collageImages', 'collageMaskImages', 'rawImages', 'calImages']
      @param index the subimage index, not that this is 0 based by FlowCam stores 1-based
      @param what the name of the image stack from which to retrieve the subimage 
      @return a an image plus for the specified subset
   */
   public BinaryProcessor createSubsetBinaryProcessor(int index, String what, int pad){
      ImageProcessor newIp = createSubsetProcessor(index, what, pad);
      if (newIp == null) { 
         IJ.log("newIp is null");
         return null;
      }
      ByteProcessor bytIp = new ByteProcessor(newIp, true);
      BinaryProcessor bIp = new BinaryProcessor(bytIp);
      return bIp; 
  }
  

   /*
     Write results to CSV file
   */
   public void saveTable(){
      String filename = outputDir.getPath() + File.separator + name + ".csv";
      saveTable(filename);
   }
   /*
      Write results to CSV file
     */
   public void saveTable(String filename){
   
      String t = "";
      String eol = "\n";
      int label = 0;
      int slice = 0;
      int n = rt.getCounter();
      try {
         BufferedWriter out = new BufferedWriter(new FileWriter(filename));
         t = (rt.getColumnHeadings());
         t = t.replace(" ", "");
         t = "id" + (t.substring(0, t.length()-1)).replaceAll("\t",",");
         out.write(t + eol);       
         for (int i = 0; i < n; i++){
            t = rt.getRowAsString(i);
            t = (t.substring(0, t.length()-1)).replaceAll("\t",",");
            out.write(t + eol);
         }
         out.close();
      } catch (IOException e) {
         IJ.log("Error writing to " + filename);
      }
   }//saveTable
   

} // class FlowCam

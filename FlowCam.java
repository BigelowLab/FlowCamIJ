// FlowCam

import java.io.File;
import java.io.FilenameFilter;
import java.lang.Exception;
import java.util.HashSet;
import java.util.Arrays;

import ij.IJ;

public class FlowCam {
   
   public Context ctx;
   public Spreadsheet data;
   
   public File inputDir;
   public String name;
   public File outputDir;
    
   String[] collageFilenames;
   String[] collageMaskFilenames;
   String[] calFilenames;
   String[] rawFilenames;
   String[] binFilenames;
   
   FlowCamVirtualStackPlus collageImages;
   FlowCamVirtualStackPlus rawImages;
   FlowCamVirtualStackPlus calImages;
   FlowCamVirtualStackPlus rawMaskImages;
   FlowCamVirtualStackPlus collageMaskImages;
   
   public FlowCam(){
   }
   
   /**
      Create an instance of FlowCam 
      @param path the path to the FlowCam diretcory
   */
   public FlowCam(String path){
      inputDir = new File(path);
      if (inputDir.exists()) {
         name = inputDir.getName();
         outputDir = new File(inputDir.getPath() + inputDir.separator + "_"+ this.name);
         if (!outputDir.exists()){
            if (!outputDir.mkdir()){
               IJ.log("Unbale to create output directory: " + outputDir.getPath());
            }
         }
         try {
            ctx = new Context(inputDir.getPath() + File.separator + this.name + ".ctx");
         } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
         }
         data = new Spreadsheet(inputDir.getPath() + File.separator + this.name + ".csv",",");

         populate_file_lists();
         collageImages = new FlowCamVirtualStackPlus(inputDir.getPath(), collageFilenames);
         
      } else {
         IJ.log("path not found: " + inputDir.getPath());
      }
      
   } //FlowCam

   /**
      Populate the file lists - self-explanatory?
   */
      
   private void populate_file_lists(){
     calFilenames = matchFiles(inputDir,"cal"); // "^cal_image.*\\.tif$");
     rawFilenames = matchFiles(inputDir, "raw"); //"^rawfile_.*\\.tif$");
     collageMaskFilenames = matchFiles(inputDir, "collageMask"); // "^.*_bin\\.tif$");
     collageFilenames = getUnique(data.getDataColumn("collage_file"));
   }
   
   /*
      Match files given the specified regex pattern
      @url http://stackoverflow.com/questions/14218412/regex-match-on-array-java
      @param dir the directory File object
      @param what the file types to match expression ['cal', 'raw', 'collageMask']
      @return String array of zero or more matched filenames
   */
   public String[] matchFiles(File dir, String what){
   
      String[] files = {};
      
      if (what.equalsIgnoreCase("cal")){
         files  = dir.list(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               //return name.matches("^cal_image.*\\.tif$");
               return name.startsWith("cal_image_");
           }
         });      
      } else if (what.equalsIgnoreCase("raw")){
         files  = dir.list(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               //return name.matches("^rawfile_.*\\.tif$");
               return name.startsWith("rawfile_");
           }
         });       
      } else if (what.equalsIgnoreCase("collageMask")){
          files  = dir.list(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               //return name.matches("^.*_bin\\.tif$");
               return name.endsWith("_bin.tif");
           }
         });      
      } else if (what.equalsIgnoreCase("collage_files")){
         files = getUnique(data.getDataColumn("collage_file"));
      }
      
      return files;
   }
   
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
   }
   
   private boolean load_calImages(){
   
      boolean ok = true;
   
      return ok;
   }

} // class FlowCam_context

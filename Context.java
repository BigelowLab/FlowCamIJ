//package windows.prefs;
/**
   Utility class and methods for reading the context file.
   
   Adapted from http://stackoverflow.com/questions/190629/what-is-the-easiest-way-to-parse-an-ini-file-in-java#190633
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Context {

   private Pattern  _section  = Pattern.compile( "\\s*\\[([^]]*)\\]\\s*" );
   private Pattern  _keyValue = Pattern.compile( "\\s*([^=]*)=(.*)" );
   private HashMap< String, HashMap< String, String >>  _entries  = new HashMap<String, HashMap<String, String>>();

/*
   * Creates an instance of a sectioned configuration
   * @param path the fully qualified ini filename
*/
   public Context(){
   }
/*
   * Creates an instance of a sectioned configuration
   * @param path the fully qualified ini filename
*/
   public Context( String path ) throws IOException {
      load( path );
   }

/* 
   * Load the data from file
   * @param path the fully qualified ini filename
*/
   public void load( String path ) throws IOException {
      
      try {
         BufferedReader br = new BufferedReader( new FileReader( path ));
         String line;
         String section = null;
         while(( line = br.readLine()) != null ) {
            Matcher m = _section.matcher( line );
            if( m.matches()) {
               section = m.group( 1 ).trim();
            }
            else if( section != null ) {
               m = _keyValue.matcher( line );
               if( m.matches()) {
                  String key   = m.group( 1 ).trim();
                  String value = m.group( 2 ).trim();
                  HashMap< String, String > kv = _entries.get( section );
                  if( kv == null ) {
                     _entries.put( section, kv = new HashMap<String, String>());   
                  }
                  kv.put( key, value );
               }
            }
         }
      } catch (IOException e) {
         System.err.println("Caught IOException: " + e.getMessage());
      }
   }

/**
   Retrieve a value as string
   @param section the name of the section
   @param key the key to the key value pair
   @param defaultvalue the default value if it is not defined
   @return a string value
**/
   public String getString( String section, String key, String defaultvalue ) {
      HashMap< String, String > kv = _entries.get( section );
      if( kv == null ) {
         return defaultvalue;
      }
      return kv.get( key );
   }
   public String getString( String section, String key){
      return getString( section, key, "");
   }

/**
   Retrieve a value as integer
   @param section the name of the section
   @param key the key to the key value pair
   @param defaultvalue the default value if it is not defined
   @return a integer value
**/   
   public int getInt( String section, String key, int defaultvalue ) {
      HashMap< String, String > kv = _entries.get( section );
      if( kv == null ) {
         return defaultvalue;
      }
      return Integer.parseInt( kv.get( key ));
   }
   public int getInt( String section, String key){
      return getInt( section, key, 0);
   }

/**
   Retrieve a value as float
   @param section the name of the section
   @param key the key to the key value pair
   @param defaultvalue the default value if it is not defined
   @return a float value
**/ 
   public float getFloat( String section, String key, float defaultvalue ) {
      HashMap< String, String > kv = _entries.get( section );
      if( kv == null ) {
         return defaultvalue;
      }
      return Float.parseFloat( kv.get( key ));
   }
   public float getFloat( String section, String key){
      return getFloat( section, key, 0.0f);
   }

/**
   Retrieve a value as double
   @param section the name of the section
   @param key the key to the key value pair
   @param defaultvalue the default value if it is not defined
   @return a double value
**/ 
   public double getDouble( String section, String key, double defaultvalue ) {
      HashMap< String, String > kv = _entries.get( section );
      if( kv == null ) {
         return defaultvalue;
      }
      return Double.parseDouble( kv.get( key ));
   }
   public double getDouble( String section, String key ) {
      return getDouble(section, key, 0.0);
   }

}  //Context

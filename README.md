#### FlowCam

[ImageJ](http://rsb.info.nih.gov/ij/) plugin for working with [FlowCam](http://www.fluidimaging.com/) data sets.  

### What is this repository for?

FlowCam runs produce a number of files: images (raw, binarized masks, background and collages), context/configuration, metadata  and optional classification files. This plugin provides facilities for working with each component of FlowCam output.  It does not do any processing of 

### How do I get set up?

Clone the repository to an ImageJ plugins directory.  

Within ImageJ navigate the 'Compile and Run..." menu and compile the FlowCam.java file.  Compile and run the plugin "FlowCam Simple" to run a barebones version.

### What is in it?

The FlowCam class is really just a container for common elements of a FlowCam run directory.

+ *Context* class for reading entries in the context file.
+ *Spreadsheet* class for reading the metadata file (.csv)
+ *FlowCamVirtualStack* and *FlowCamVirtualStackPlus* classes for navigating the images
+ *ResultsTable* class for storing and saving results in an [ImageJ](http://rsb.info.nih.gov/ij/developer/api/ij/measure/ResultsTable.html) friendly format.
+ *Calibration* class for managing pixels-to-microns [calibration](http://rsb.info.nih.gov/ij/developer/api/ij/measure/Calibration.html)
+ *Classification* class for user classification data stored in the .cla file


### Who do I talk to?
* Ben Tupper btupper@beigelow.org
* Nicole Poulton npoulton@bigelow.org
* Michael Sieracki msieracki@bigelow.org
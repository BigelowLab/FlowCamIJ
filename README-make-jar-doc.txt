A FlowCam.jar is provided in the repos, but if you wish to make your own so that other 
plugins can have easy access to the FlowCam class and its derivatives.

(1) Remove all the classes in the plugins/FlowCam directory

$ rm *.class

(2) Compile from ImageJ the FlowCam.java located in plugins/FlowCam directory

(3) Navigate to the FlowCam directory and build a jar for FlowCam and then remove the classes

$ cd <ImageJ-path>/plugins/FlowCam
$ jar cvfM FlowCam.jar *.class
$ rm *.class

(4) Compile FlowCam_Simple.java


Now you'll have in the BlobHandler directory...

   FlowCam_Simple.class
   FlowCam.jar



//----------
Make the documentation
//----------

$ javadoc -d /Applications/ImageJ/plugins/FlowCam/api /Applications/ImageJ/plugins/FlowCam/*.java
 
import ij.IJ;

import ij.gui.PlotWindow;
import ij.gui.Plot;
import ij.gui.Roi;

public class FlowCamPlot extends PlotWindow {
   public double[] xdata;
   public double[] ydata;
   public Roi roi;
   public PlotWindow window;
   
   public FlowCamPlot(){
      super();
   }
   
   public void addPoints(double[] x, double[] y, int shape){
      xdata = x;
      ydata = y;
      super.addPoints(x,y,shape);
   }
} //class
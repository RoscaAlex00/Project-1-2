package com.crazyputting.function;
//package DhbInterpolation;
import DhbInterfaces.OneVariableFunction;
import DhbInterfaces.PointSeries;



public class cubicspline extends NewtonInterpolator implements DhbInterfaces.OneVariableFunction{

   private double startPointDerivative= Double.NaN;
   private double endPointDerivative = Double.NaN;

   public cubicspline(PointSeries pts)
                                throws IllegalArgumentException {
       super(pts);
       for (int i = 1; i < pts.size(); i++) {
           if (pts.xValueAt(i - 1) >= pts.xValueAt(i))
               throw new IllegalArgumentException("Points must be sorted in increasing x value");
       }
   }
   private void computeSecondDerivatives()
   {
      int n = points.size();
      double w,s;
      double[]u= new diuble[n-1];
      coefficients= new diuble[n];
      if(Double.isNaN(startPointDerivative))
      {
          coefficients[0]=u[0]=0;
      }
      else
      {
          coefficients[0]=-0.5;
          u[0]=3.0/(points.xValueAt(1)-points.xValueAt(0))
                  *((points.yValueAt(1) -mpoints.yValueAt(0))
                  /(points.xValueAt(1)-points.xValueAt(0))-startPointDerivative;
      }


   }



}

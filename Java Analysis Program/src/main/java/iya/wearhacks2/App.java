package iya.wearhacks2;
import iya.wearhacks2.diff_match_patch.*;

import java.awt.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.parse4j.Parse;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;

public class App { 
   public static void main(String[] args) throws IOException { 
	   //String fileName = args[0];
	   String context = System.getProperty("myvar"); 
	   Reader in = new FileReader(context);
	   //Reader in = new FileReader("/Users/lukemcgartland/Desktop/Luke.csv");

	   Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
	   
	   ArrayList<Double> x_accelArrayList = new ArrayList<Double>();
	   ArrayList<Double> mag_accelArrayList = new ArrayList<Double>();
	   
	   
	   //Parse CSV file
	   for (CSVRecord record : records) {
		   String x_accel = record.get("x");
		   double y_accel = Double.parseDouble(record.get("y"));
		   double z_accel = Double.parseDouble(record.get("z"));
	       
		   double mag = Math.sqrt((y_accel*y_accel)+(z_accel*z_accel));
		  
		   x_accelArrayList.add(Double.parseDouble(x_accel));
		   mag_accelArrayList.add(mag);
	       
	   }
	   //Convert ArrayList to Array
	   double[] x_accelArray = new double[x_accelArrayList.size()];
	   x_accelArray = listToArray(x_accelArrayList);
	   double[] magArray = new double[mag_accelArrayList.size()];
	   magArray = listToArray(mag_accelArrayList);
	   
	   
	   //Apply Filters to X Array
	   x_accelArray = exponentialFilter(x_accelArray, .45);
	   x_accelArray = exponentialFilter(x_accelArray, .45);
	   x_accelArray = exponentialFilter(x_accelArray, .45);
	   x_accelArray = exponentialFilter(x_accelArray, .45);
	   
	   x_accelArray = movingAverageFilter(x_accelArray, 4);
	   
	 //Apply Filters to Mag Array
	   magArray = exponentialFilter(magArray, .45);
	   magArray = exponentialFilter(magArray, .45);
	   magArray = exponentialFilter(magArray, .45);
	   magArray = exponentialFilter(magArray, .45);
	   
	   magArray = movingAverageFilter(magArray, 4);
	   
	   String vector = convertToVectorString(magArray, x_accelArray);
	   
	   int numberOfPushups = checkForPushup(vector,"SW S W NW NW N N N N N N N NE NE");
	   System.out.println("Did "+ numberOfPushups+" pushups!");
	   
	   Parse.initialize("NEFFpiwuHOpezYaOYvDwccpLItrrVcS1La4IWOuI", "5pSZptDndNx4btI5NQMJSIEyw2ixdr9DIgN5wB7B");
	   
	   ParseObject workout = new ParseObject("Workout");
	   workout.put("number", numberOfPushups);
	   
	   Calendar cal = Calendar.getInstance();
	   String timestamp = "";
	   timestamp = ""+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.YEAR);
	   workout.put("date", timestamp);
	   
	   try {
		   workout.save();
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		String exceptionDetails = e.toString();
		System.out.println(exceptionDetails);
		e.printStackTrace();
		  
	}
   }
   public static int checkForPushup(String input, String phrase){
	   
	   int numberOfPushups = 0;
	   diff_match_patch matcher = new diff_match_patch();
	   //input = "SE NW W NW S S SE N S SW NW N E N S S S S NE SE SE E NE N S S SW N S S SE E SE N NW NW N S W NW NW NW N N S N N N N NE NE NE N S S S S W S N SE N NW SW S NW N N SW N N N N N SE SE SE SE S N NW SW S W NW NW NW N N N N N N N NE NE E S S S S NE NE NE N NW SW SW N N N N N N N N S N N SE SE SE SE NW NW SE S SW W NW NW NW N N N S N N N N NE NE SE S S SE SE SE SE N S W NW NW N S N N S S N N N N N SE SE S S NW SW S S SW W NW NW N N N S NE N N N N NE E SE S S SE";
	   //input = "E SE SE E S SW SW S S S S S N N S S N S NE N SW SW S S NW N N NE E N NW NW N N N NE N N N W N N N S N N NW SW S N N N N SE S W SW SW S SE E NE E N N N N NE S N N W NW NW N N N S NW N N N N N N N N NW NW SE NE N N S SW E E SE NW NW E NE N S N N N NW SW N NE E N W W W SW E E SE SE N W W SW N N N S S S S N N NE NE NE SW W NW S S S S S S SE S S S S S N N N SE NE N SW SW W NW SE E N SW NW N N N N NE N N N S W W S N NW SW N N NE N N NW N N S S N S N N NE NE S N N SE NE N SW W NE S N S S S NW SW S N NE S SW W N SE SE SE S N N N N S E NE N N N N N NW NW SW S N NE NE N SW W N S S SE S S S SW NW NW NW NE NE NE NE NW NW W SW N NE NE SE S S S N NW W N N NW E NE NE NE E S N N N S NE S SW SW W S NE N S W NW N S N N N N N N N N N S S NW N N N W S N SE N SW S S N E S S N N S S S SE E N SW SW S N SE NW N E E NE SW SW S N N S NW N S S N NW S N N N N S S SW S S S W SW W N N N NW N N NE N SW W NW W SW S S S W N N N N E N N N N E S N NW S S S NW S N N NW N N NW NW SW N N NE N N S SE SE W NW S SE NE N S S S N NW NW NW N S SE SE S N NW SW N NE NE NE S SW S S S N NE E NW NW S E SE S NW W W W S S SE SE NE S SW NW N NW N N S N W S N N N SW SW NW S S NW SW SW SW W NW N N N N N N N N S NW SW S N N S S N N N N N N NW W SW W S E NE NE NE N N N S S E NE S SW NW S SE SE N SW SW S NE SE S N W S N NE SE S N NW W SW S N N NE E NE N N S SW SW E SE S S S NW S SE S N NW SW S NE E SE N NW W S NE SE SE S NW SW SW SW SW W NW NW N N N SE N N N N SE N NW SW S N SE N N S N";
	   //phrase = "SW S W NW NW N N N N N N N NE NE";
	   matcher.Match_Threshold = (float) 0.2;
	   matcher.Match_Distance = 1000;
	  
	   Set<Integer> set = new HashSet<Integer>();
	   for(int testLocation = 0; testLocation<input.length(); testLocation+=50){
		   int loc = matcher.match_main(input, phrase, testLocation);
		   //System.out.println(testLocation+"\t"+loc);

		   if(loc!=-1){
			   set.add(loc);
		   }
		   
        }
	   
	  
	   
	   //System.out.println(set);
	   ArrayList<Integer> listOfIndices = new ArrayList<Integer>(new TreeSet(set));
	   for(int i = 0; i<listOfIndices.size()-1; i++){
		   //System.out.println(listOfIndices.get(i));

		   if((listOfIndices.get(i+1)-listOfIndices.get(i))<5){
			   listOfIndices.remove(i+1);
		   }
	   }
	    
	   
	   return listOfIndices.size();
   }
   public static String convertToVectorString(double[] xAxisArray, double[] yAxisArray){
	   String[] returnArray;
	   returnArray = new String[xAxisArray.length-1];
	   String vector = "";
	   for(int i = 0; i < returnArray.length; i++) {
		    double deltaX=xAxisArray[i]-xAxisArray[i+1];
		    double deltaY=yAxisArray[i]-yAxisArray[i+1];
		    //System.out.println(deltaX+"\t"+deltaY);
		    double theta = Math.atan2(deltaY, deltaX)*57.2958;
		    if(theta<0){
		    	theta=theta*-1;
		    	theta += 180;
		    }
		    //System.out.println(theta);
		    
		    if((theta>=0 && theta<22.5)||(theta>=337.5&&theta<360)){
		    	vector = vector+"N ";
		    }
		    if(theta>=22.5&&theta<67.5){
		    	vector = vector+"NE ";
		    }
		    if(theta>=67.5&&theta<112.5){
		    	vector = vector+"E ";
		    }
		    if(theta>=112.5&&theta<157.5){
		    	vector = vector+"SE ";
		    }
		    if(theta>=157.5&&theta<202.5){
		    	vector = vector+"S ";
		    }
		    if(theta>=202.5&&theta<247.5){
		    	vector = vector+"SW ";
		    }
		    if(theta>=247.5&&theta<292.5){
		    	vector = vector+"W ";
		    }
		    if(theta>=292.5&&theta<337.5){
		    	vector = vector+"NW ";
		    }
		    //System.out.println(vector);
		}
	   return vector;   
   }

   public static double[] movingAverageFilter(double[] givenArray, int sample){
	   double[] returnArray;
	   returnArray = new double[givenArray.length-sample+1];
	   
	   for(int i = 0; i < returnArray.length; i++) {
		    //returnArray[i]=givenArray[]
		    	double average = 0;
		    	for(int j = 0; j < sample; j++) {
		    		average = average + givenArray[i+j];
		    	}
		    	average = average/sample;
		    	returnArray[i]=average;
			    //System.out.println(returnArray[i]);

		}
	   return returnArray;
   }
   
   public static double[] exponentialFilter(double[] givenArray, double alpha){
	   double[] returnArray;
	   returnArray = new double[givenArray.length-1];
	   for(int i = 0; i < returnArray.length; i++) {
		    returnArray[i]=alpha*givenArray[i]+(1-alpha)*givenArray[i+1];
		    //System.out.println(returnArray[i]);
		}
	   return returnArray;
   }

   public static int size(Iterable<?> it) {
	   if (it instanceof Collection)
	     return ((Collection<?>)it).size();

	   // else iterate

	   int i = 0;
	   for (Object obj : it) i++;
	   return i;
	 }
   public static double[] listToArray(ArrayList<Double> arr){   
	    double[] result = new double[arr.size()];
	    int i = 0;
	    for(Double d : arr) {
	        result[i++] = d.doubleValue();
	    }
	    return result;
	}
  
}
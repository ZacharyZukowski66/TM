import java.util.*;
import java.io.*;
import java.time.*;

public class TM{
   TMModel tmModel = new TMModel();
   LocalDateTime timeStamp = LocalDateTime.now();
   String log = "TM.txt";
///////////////////////////////////////////////////////////////////////////////
   public static void main(String[]args){
      TM tm = new TM();
      tm.appMain(args);
   }
///////////////////////////////////////////////////////////////////////////////
   public void appMain(String[]args){
      int numArgs = args.length;
      String cmd = "";
      String nm = "";
      String des = "";
      String sz = "";
      String newNm = "";
      if(numArgs > 0 && numArgs < 5){
         cmd = args[0];
         if(numArgs > 1){
            nm = args[1];
         }
         switch(cmd){
            case("Start"): if(numArgs < 2 || numArgs > 2){                             
                              usage();
                              break;
                           }
                           tmModel.startTask(nm);
                           break;
            case("Stop"):  if(numArgs < 2 || numArgs > 2){
                              usage();
                              break;
                           }
                           tmModel.stopTask(nm);
                           break;
            case("Describe"): if(numArgs < 3){
                                 usage();
                                 break;
                              }
                              des = args[2];
                              tmModel.describeTask(nm, des);
                              if(numArgs > 3){
                                 sz = args[3];
                                 tmModel.sizeTask(nm, sz);
                              }
                              break;
            case("Size"):  if(numArgs < 3 || numArgs > 4){
                              usage();
                              break;
                           }
                           sz = args[2];
                           tmModel.sizeTask(nm, sz);
                           break;
            case("Delete"): if(numArgs < 2 || numArgs > 2){
                                 usage();
                                 break;
                            }
                            tmModel.deleteTask(nm);
                            break;
            case("Rename"): if(numArgs < 3 || numArgs > 3){
                                 usage();
                                 break;
                            } 
                            newNm = args[2];
                            tmModel.renameTask(nm, newNm);
                            break;
            case("Summary"): try{
                                 cmdSummary(nm);
                             }
                             catch(IOException e){
                                 usage();
                             }
                             break;
            default: usage();
                     break;
         }
      }
      else{
         usage();
      }
   }
///////////////////////////////////////////////////////////////////////////////
   public void cmdSummary(String nm) throws IOException{
      Task current;
      try{
         if(nm.equals("")){
            sumAll();
         }
         else if(taskExists(nm) == true){
            current = new Task(nm);
            current.sum();
         }
      }
      catch(IOException e){
         usage();
      }
   }
///////////////////////////////////////////////////////////////////////////////
   public String getNm(String line){
      StringTokenizer stk = new StringTokenizer(line, "\t");
      return stk.nextToken();
   }
///////////////////////////////////////////////////////////////////////////////
   public String getCmd(String line){
      StringTokenizer stk = new StringTokenizer(line, "\t");
      stk.nextToken(); //skips nm
      return stk.nextToken();
   }
///////////////////////////////////////////////////////////////////////////////
   public String getDat(String line){
      StringTokenizer stk = new StringTokenizer(line, "\t");
      stk.nextToken(); //skips nm
      stk.nextToken(); //skips cmd
      return stk.nextToken();
   }
///////////////////////////////////////////////////////////////////////////////
   public boolean taskExists(String nm) throws IOException{
      Set<String> names = tmModel.taskNames();
      if(names.contains(nm)){
         return true;
      }
      return false;
   }
///////////////////////////////////////////////////////////////////////////////
   public void sumAll() throws IOException{
      Set<String> names = tmModel.taskNames();
      Set<Task> tasks = new HashSet<>();
      if(names.size() > 0){
         System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
         names.forEach(i -> {
            try{
               tasks.add(new Task(i));
            }
            catch(IOException e){
               usage();
            }
         });
         tasks.forEach(i -> {
            i.sum();
         });
         sumSize();
         System.out.println("Total Time Spent on all Task: " + tmModel.elapsedTimeForAllTasks());
         System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
      }
   }
///////////////////////////////////////////////////////////////////////////////
   public void sumSize() throws IOException{
      Set<String> names = new HashSet<>();
      Set<String> sizes = tmModel.taskSizes();
      sizes.forEach(i -> {
         tmModel.taskNamesForSize(i).forEach(j -> {
            names.add(j);
         });
         if(names.size() > 1){
            System.out.println("Stats on " + i + " sized tasks:");
            System.out.println("Min: " + tmModel.minTimeForSize(i));
            System.out.println("Max: " + tmModel.maxTimeForSize(i));
            System.out.println("Avg: " + tmModel.avgTimeForSize(i));
         }
         names.clear();
      });
   }
///////////////////////////////////////////////////////////////////////////////
    public void usage(){
      System.out.println("Please use the following format for commands:\n\n" +
                         "java TM Start <task name>\n" +
                         "java TM Stop <task name>\n" +
                         "java TM Describe <task name> <description in quotation marks>\n" +
                         "java TM Describe <task name> <description in quotation marks> <size(Ex:S, M, L, XL)>\n" +
                         "java TM Size <task name> <size(Ex:S, M, L, XL)>\n" +
                         "java TM Rename <old task name> <new task name that isn't in TM.txt>\n" +
                         "java TM Delete <task name>\n" +
                         "java TM Summary <task name>\n" +
                         "java TM Summary\n" +
                         "Log is named TM.txt");
   } 
///////////////////////////////////////////////////////////////////////////////
   public String timeStampFormat(long timeStamp){
      long hours, mins;
      hours = timeStamp/3600; //gets hours
      timeStamp %= 3600;      //leaves only minutes and seconds
      mins = timeStamp/60;    //gets minutes
      timeStamp %= 60;        //leaves only seconds
      return (hours + " hours, " + mins + " minutes, and " + timeStamp + " seconds.");
   }
} 

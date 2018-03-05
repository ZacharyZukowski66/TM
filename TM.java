import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TM{
   public void appMain(String[]args){
      TaskLog log = new TaskLog("TM.log");
      ArrayList<Task> tasks = new ArrayList<Task>();
      String command;
      LocalDateTime t;
      try{
         command = args[0];
         switch(command){
            case "Start": t = LocalDateTime.now();
                          if(args.length == 2){
                               log.writeLine((t + "\t" + args[1] + "\tStart"), "TM.log");
                          }
                          else{
                              this.usage();
                          }
                          break;
            case "Stop": t = LocalDateTime.now();
                         if(args.length == 2){
                              log.writeLine((t + "\t" + args[1] + "\tStop"),"TM.log");
                         }
                         else{
                             this.usage();
                         }
                         break;
             case "Describe": t = LocalDateTime.now();
                              if(args.length == 3){
                                 log.writeLine((t + "\t" + args[1] + "\tDescribe\t" + args[2]),"TM.log");
                              }
                              else if(args.length == 4){
                                 log.writeLine((t + "\t" + args[1] + "\tDescribe\t" + args[2] + "\t" + args[3]),"TM.log");
                              }
                              else{
                                 this.usage();
                              }
                              break;
             case "Size": t = LocalDateTime.now();
                          if(args.length == 3){
                              log.writeLine((t + "\t" + args[1] + "\tSize\t" + args[2]),"TM.log");
                          }
                          else{
                              this.usage();
                          }
                          break;
             case "Summary": t = LocalDateTime.now();
                             tasks = log.read("TM.log", tasks);
                             System.out.println("\t     Summary\n\t_________________");
                             if(args.length == 2){
                                 for(Task tsk: tasks){
                                    if((tsk.name).equals(args[1])){
                                       System.out.println(tsk);
                                       break;
                                    }
                                 }
                             }
                             else if(args.length == 1){
                                 for(Task tsk: tasks){
                                    System.out.println(tsk);
                                 }
                             }
                             else{
                                  this.usage();
                             }
                             break;
            default: this.usage();
         }
      }
      catch(Exception e){
         this.usage();
      }
   }
//////////////////////////////////////////////////////////////////////////////
   public static void main(String[]args){
      TM tm = new TM();
      tm.appMain(args);
   }
   public void usage(){
      System.out.println("Please use the following format for commands:\n\n" +
                         "java TM Start <task name>\n" +
                         "java TM Stop <task name>\n" +
                         "java TM Describe <task name> <description in quotation marks>\n" +
                         "java TM Describe <task name> <description in quotation marks> <size>\n" +
                         "java TM Size <task name> <size>\n" +
                         "java TM Rename <old task name> <new task name that isn't in TM.log>\n" +
                         "java TM Delete <task name>\n" +
                         "java TM Summary <task name>\n" +
                         "java TM Summary\n" +
                         "Log is named TM.log");
   }
//////////////////////////////////////////////////////////////////////////////
   private class TaskLog{
      TaskLog(String name){
         File f = new File(name);
      }
      public void writeLine(String s, String n){
         try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(n, true));
            writer.append(s);
            writer.newLine();
            writer.close();
         }catch(IOException e){
            e.printStackTrace(); 
         }
      }
      public ArrayList<Task> read(String n, ArrayList<Task> ta){
          ArrayList<Task> t = ta;
          String nm = null;
          String cmd = null;
          String data1 = null;
          String data2 = null;
          LocalDateTime tm = null;
          boolean changed = false;
          try{
             BufferedReader br = new BufferedReader(new FileReader(n));
             String strLine;
             while((strLine = br.readLine()) != null){
                StringTokenizer st = new StringTokenizer(strLine, "\t");
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                if(st.hasMoreTokens()){
                   tm = LocalDateTime.parse((st.nextToken()), formatter);
                }
                if(st.hasMoreTokens()){
                   nm = st.nextToken();
                }
                if(st.hasMoreTokens()){    
                   cmd = st.nextToken(); 
                }
                if(st.hasMoreTokens()){                 
                   data1 = st.nextToken();
                }
                if(st.hasMoreTokens()){
                   data2 = st.nextToken();
                }
                data2 = "";
                changed = false;
                for(int i = 0; i < t.size(); i++){
                   if(((t.get(i)).name).equals(nm)){
                      if(cmd.equals("Start")){
                           ((t.get(i)).latestStart) = tm;
                      }
                      else if(cmd.equals("Stop")){
                           ((t.get(i)).stop) = tm;
                           if((t.get(i)).latestStart != null){
                              ((t.get(i)).total) += ChronoUnit.SECONDS.between(((t.get(i)).latestStart),((t.get(i)).stop));
                              ((t.get(i)).latestStart) = null;
                              ((t.get(i)).stop) = null;
                           }
                      }
                      else if(cmd.equals("Describe")){
                           if(data2.equals(null)){
                              ((t.get(i)).description) += ("\n\t     " + data1);
                           }
                           else{
                              ((t.get(i)).description) += ("\n\t     " + data1);
                              ((t.get(i)).size) = data2;
                           }
                      }
                      else if(cmd.equals("Size")){
                           ((t.get(i)).size) = data1;
                      }
                      changed = true;
                   }
                }
                if(changed == false || t.isEmpty()){
                     if(cmd.equals("Start")){
                        t.add(new Task(nm, " ", " ", tm, null));
                     }
                     else if(cmd.equals("Stop")){
                        t.add(new Task(nm, " ", " ", null, null));
                     }
                     else if(cmd.equals("Describe")){
                        if(data2.equals(null)){
                           t.add(new Task(nm, data1, " ", null, null));
                        }
                        else{
                           t.add(new Task(nm, data1, data2, null, null));
                        }
                     }
                     else if(cmd.equals("Size")){
                           t.add(new Task(nm, " ", data1, null, null));
                     }
                }      
             }
             br.close();
          }catch(IOException e){
             e.printStackTrace();
          }       
          return t;
      }
   }
//////////////////////////////////////////////////////////////////////////////
   private class Task{
      String name;
      String description;
      String size;
      long total;
      LocalDateTime latestStart;
      LocalDateTime stop;
      Task(String n, String d, String s, LocalDateTime lstar, LocalDateTime sto){
         name = n;
         description = d;
         size = s;
         latestStart = lstar;
         stop = sto;
      }
      public String getTotal(long t){
         long hours = t/3600;
         long minutes = (t%3600)/60;
         long seconds = (t%60);
         return (String.format("%02d:%02d:%02d", hours, minutes, seconds));
      }
      public String toString(){
         return ("Name: " + name + "\nSize: " + size + "\nDescription: " + 
                  description + "\nTotal Time: " + this.getTotal(total) + "\n");
      }
   }
}
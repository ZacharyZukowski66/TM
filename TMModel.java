import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import java.nio.file.*;

public class TMModel implements ITMModel{   
   String log = "TM.txt";
   LocalDateTime timeStamp = LocalDateTime.now();
///////////////////////////////////////////////////////////////////////////////
   public boolean startTask(String name){
      TaskEntry newEntry = new TaskEntry(name, "Start", timeStamp.toString());
      try{
         newEntry.writeLine();
         return true;
      }
      catch(FileNotFoundException e){
         usage();
      }
      catch(IOException e){
         usage();
      }
      return false;
   }
///////////////////////////////////////////////////////////////////////////////
   public boolean stopTask(String name){
      TaskEntry newEntry = new TaskEntry(name, "Stop", timeStamp.toString());
      try{
         newEntry.writeLine();
         return true;
      }
      catch(FileNotFoundException e){
         usage();
      }
      catch(IOException e){
         usage();
      }
      return false;
   }
///////////////////////////////////////////////////////////////////////////////
   public boolean describeTask(String name, String description){
      TaskEntry newEntry = new TaskEntry(name, "Describe", description);
      try{
         newEntry.writeLine();
         return true;
      }
      catch(FileNotFoundException e){
         usage();
      }
      catch(IOException e){
         usage();
      }
      return false;
   }
///////////////////////////////////////////////////////////////////////////////
   public boolean sizeTask(String name, String size){
      TaskEntry newEntry = new TaskEntry(name, "Size", size);
      try{
         newEntry.writeLine();
         return true;
      }
      catch(FileNotFoundException e){
         usage();
      }
      catch(IOException e){
         usage();
      }
      return false;
   }
///////////////////////////////////////////////////////////////////////////////
   public boolean deleteTask(String name){
      Stream<String> line;
      List<TaskEntry> tasks = new ArrayList<>();
      File log = new File("TM.txt");
      try{
         line = Files.lines(Paths.get("TM.txt"));
         line.forEach(i -> {
            if(!getNm(i).equals(name)){
               tasks.add(new TaskEntry(getNm(i), getCmd(i), getDat(i)));
            }
         });
         line.close();
         log.delete();
         tasks.forEach(i -> {
            try{
               i.writeLine();
            }
            catch(IOException e){
               usage();
            }
         });
         return true;
      }
      catch(IOException e){
         usage();
      }
      return false;
   }
///////////////////////////////////////////////////////////////////////////////
   public boolean renameTask(String oldName, String newName){
      Stream<String> line;
      List<TaskEntry> tasks = new ArrayList<>();
      File log = new File("TM.txt");
      try{
         line = Files.lines(Paths.get("TM.txt"));
         line.forEach(i -> {
            if(!getNm(i).equals(oldName)){
               tasks.add(new TaskEntry(getNm(i), getCmd(i), getDat(i)));
            }
            else{
               tasks.add(new TaskEntry(newName, getCmd(i), getDat(i)));
            }
         });
         line.close();
         log.delete();
         tasks.forEach(i -> {
            try{
               i.writeLine();
            }
            catch(IOException e){
               usage();
            }
         });
         return true;
      }
      catch(IOException e){
         usage();
      }
      return false;
   }
///////////////////////////////////////////////////////////////////////////////
   public String taskElapsedTime(String name){
      String dur = "";
      Task current;
      try{
         current = new Task(name);
         dur = current.duration;
      }
      catch(IOException e){
         usage();
      }
      return dur;
   }
///////////////////////////////////////////////////////////////////////////////
   public String taskSize(String name){
      String size = "";
      Task current;
      try{
         current = new Task(name);
         size = current.sz;
      }
      catch(IOException e){
         usage();
      }
      return size;
   }
///////////////////////////////////////////////////////////////////////////////
   public String taskDescription(String name){
      String description = "";
      Task current;
      try{
         current = new Task(name);
         description = current.des;
      }
      catch(IOException e){
         usage();
      }
      return description;
   }
///////////////////////////////////////////////////////////////////////////////
   public String minTimeForSize(String size){
      Set<String> names = taskNamesForSize(size);
		Set<Task> tasks = new HashSet<Task>();
		HashSet<Long> times = new HashSet<Long>();
		names.forEach(i -> {
			try { 
            tasks.add(new Task(i)); 
         }
			catch(IOException e){ 
            usage(); 
         }
		});
		tasks.forEach(i -> {
         times.add(i.secElapsed);
      });
		return (timeStampFormat(Collections.min(times)));
   }
///////////////////////////////////////////////////////////////////////////////
   public String maxTimeForSize(String size){
      Set<String> names = taskNamesForSize(size);
		Set<Task> tasks = new HashSet<Task>();
		HashSet<Long> times = new HashSet<Long>();
		names.forEach(i -> {
			try { 
            tasks.add(new Task(i)); 
         }
			catch(IOException e){ 
            usage(); 
         }
		});
		tasks.forEach(i -> {
         times.add(i.secElapsed);
      });
		return (timeStampFormat(Collections.max(times)));
   }
///////////////////////////////////////////////////////////////////////////////
   public String avgTimeForSize(String size){
      Set<String> names = taskNamesForSize(size);
		Set<Task> tasks = new HashSet<Task>();
		List<Long> times = new ArrayList<Long>();
		names.forEach(i -> {
			try { 
            tasks.add(new Task(i)); 
         }
			catch(IOException e){ 
            usage(); 
         }
		});
		tasks.forEach(i -> {
         times.add(i.secElapsed);
      });
      long total = times.stream().mapToLong(Long::longValue).sum();
		total/= times.size();
      return (timeStampFormat(total));
   }
///////////////////////////////////////////////////////////////////////////////
   public Set<String> taskNamesForSize(String size){
      Set<String> names = new HashSet<>();
      Stream<String> line;
      try{
         line = Files.lines(Paths.get("TM.txt"));
         line.forEach(i -> {
            if(size.equals(getDat(i))){
               names.add(getNm(i));
            }
         });
         line.close();
      }
      catch(IOException e){
         usage();
      }
      return names;
   }
///////////////////////////////////////////////////////////////////////////////
   public String elapsedTimeForAllTasks(){
      Set<String> names = taskNames();
      Set<Task> tasks = new HashSet<>();
      List<Long> times = new ArrayList<>();
      names.forEach(i -> {
         try{
            tasks.add(new Task(i));
         }
         catch(IOException e){
            usage();
         }
      });
      tasks.forEach(i -> {
         times.add(i.secElapsed);
      });
      long total = times.stream().mapToLong(Long::longValue).sum();
      return (timeStampFormat(total));
   }
///////////////////////////////////////////////////////////////////////////////
   public Set<String> taskNames(){
      Set<String> names = new HashSet<>();
      Stream<String> line;
      try{
         line = Files.lines(Paths.get("TM.txt"));
         line.forEach(i -> {
            names.add(getNm(i));
         });
         line.close();
      }
      catch(IOException e){
         usage();
      }
      return names; 
   }
///////////////////////////////////////////////////////////////////////////////
   public Set<String> taskSizes(){
      Set<String> sizes = new HashSet<>();
      Stream<String> line;
      try{
         line = Files.lines(Paths.get("TM.txt"));
         line.forEach(i -> {
            if(getCmd(i).equals("Size")){
               sizes.add(getDat(i));
            }
         });
         line.close();
      }
      catch(IOException e){
         usage();
      }
      return sizes;
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
///////////////////////////////////////////////////////////////////////////////
   public String getNm(String line){
      String nm;
      StringTokenizer stk = new StringTokenizer(line, "\t");
      nm = stk.nextToken();
      return nm;
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
}
///////////////////////////////////////////////////////////////////////////////
class TaskEntry{
   String cmd, nm;
   String log = "TM.txt";
   String data = "";
   TaskEntry next = null;
   TaskEntry(String name, String command, String data){
      this.nm = name;
      this.cmd = command;
      this.data = data;
   }
   public void writeLine() throws IOException, FileNotFoundException{
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("TM.txt", true)));
      out.append(this.nm + "\t" + this.cmd + "\t" + this.data + "\r\n");
      out.close();
   }
}
///////////////////////////////////////////////////////////////////////////////
class Task{
   Stream<String> current;
   String log = "TM.txt";
   String nm = "";
   String des = "";
   String sz = "";
   TaskEntry head;
   long secElapsed = 0;
   String duration = "";
   LocalDateTime timeStamp = LocalDateTime.MIN;
   Task(String name) throws IOException{
      this.nm = name;
      current = Files.lines(Paths.get("TM.txt"));
      current.filter(i -> i.startsWith(this.nm))
                          .forEachOrdered(i -> add(i));
      current.close();
   }
   public void add(String line){
      StringTokenizer stk = new StringTokenizer(line, "\t");
      TaskEntry tsk;
      String nm, cmd, data;
      long hrs, mins, secs;
      nm = stk.nextToken();
      cmd = stk.nextToken();
      data = stk.nextToken();
      if(this.head != null){
         for(tsk = this.head; tsk.next != null; tsk = tsk.next);
         tsk.next = new TaskEntry(nm, cmd, data);
         tsk = tsk.next;
      }
      else{
         tsk = new TaskEntry(nm, cmd, data);
         this.head = tsk;
      }
      if(tsk.cmd.equals("Describe")){
         this.des += "\n\t\t" + tsk.data;
      }
      if(tsk.cmd.equals("Size")){
         this.sz = tsk.data;
      }
      if(tsk.cmd.equals("Start")){
         this.timeStamp = LocalDateTime.parse(tsk.data);
      }
      if(tsk.cmd.equals("Stop")){
         if(this.timeStamp != LocalDateTime.MIN){
            this.secElapsed += timeStamp.until(LocalDateTime.parse(tsk.data), ChronoUnit.SECONDS);
            secs = this.secElapsed;
            hrs = secs/3600;
            secs%=3600;
            mins = secs/60;
            secs%=60;
            this.duration = (hrs + " hours, " + mins + " minutes, and " + secs + " seconds.");
         }
      }
   }
   public void sum(){
      System.out.println("Summary of " + this.nm + ":");
      if(!this.duration.equals("")){
         System.out.println("Total Duration:" + this.duration);
      }
      if(!this.sz.equals("")){
         System.out.println("Size: " + this.sz);
      }
      if(!this.des.equals("")){
         System.out.println("Description: " + this.des);
      }
      System.out.println();
   }
}

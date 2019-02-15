import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

//class that runs second phase of TPMMS
public final class PhaseTwo {
	//variables
	
	static List<BufferedReader> filePointers = new ArrayList<BufferedReader>();//used to hold pointers to file
	static List<String> buffer =  new ArrayList<String>(); //hold a string value from each file
	static int passes = 0;//hold number of passes phase two will do
	static int endFileNo = 0;//used to hold the end index value of the buffer files
	static int end = PhaseOne.fileCounter<=Main.maxFiles ? PhaseOne.fileCounter : Main.maxFiles;//used to hold the end index value of the buffer files
	
	//private constructor and final class all methods are static to simulate static class like C++
	private PhaseTwo() {
	}
	
	public static void start() 
	{//holds a pointer to each file and write one big file in sorted order
		System.out.println("Phase two started");
		try {
			xFiles();
			
		}catch(IOException io) {
		       io.printStackTrace();
		    }	

	}//end of start
	
	private static void xFiles() throws IOException
	{
		boolean flag = true; //used to see if all files created were merged or not
		while(flag)
		{//create pointer to each file created in phase one up to heap size
			for(int i=0; i<end;i++)
			{
				filePointers.add(Files.newBufferedReader(Paths.get("f"+(i+endFileNo))));
				buffer.add(filePointers.get(i).readLine());
			}
			//merge files from buffer using filePointers
			//merge();
			mergeIntoOneFile();
			//will go around again if not fully merged			
			passes++;//increase pass number 			
			endFileNo = passes * Main.maxFiles  ; //this calculates the last file index	
			end = Main.maxFiles < (PhaseOne.fileCounter+passes) - endFileNo ? Main.maxFiles:  (PhaseOne.fileCounter+passes) - endFileNo ; //get the end of the next forloop
			flag =  end>0 ? true:false;//this will end loop if greater then number of files	
			buffer.clear();//clear buffer ready for next round
			filePointers.clear();//clear the file pointers ready for next round
		}
	
		System.out.println("Phase two took "+passes+ " Passes to complete.");
	}
	//merges files writes to new file 
	private static void merge() throws IOException 
	{//variables
		int leastCID ; //holds the max value in the buffer
        int leastIndex ;	//the index of the buffer where the max value exists
        boolean elementsLeft=true;//the loop continues while this is true
        FileWriter file = new FileWriter("f"+(PhaseOne.fileCounter+passes));
        BufferedWriter writer = new BufferedWriter(file);
        
		while(elementsLeft)
		{//for statement to find max value and index of max value
			leastCID = Integer.parseInt(buffer.get(0).substring(0,9));
			leastIndex = 0;
	        for (int i = 1; i < buffer.size(); i++) {
	            if (Integer.parseInt(buffer.get(i).substring(0,9)) < leastCID) {//enters if only if value is greater
	                leastCID = Integer.parseInt(buffer.get(i).substring(0,9));
	                leastIndex = i;
	            }
	    	}//end of for loop
            //write max to new file and numbered at end
            writer.write(buffer.get(leastIndex)+"\n");
            //get next round ready
            String newValue =filePointers.get(leastIndex).readLine();//get new value from the specific file
            if (newValue==null)//check to see if endof file is reached
            {//if reached remove value and pointer
            	filePointers.remove(leastIndex); //remove pointer from filePointer
            	buffer.remove(leastIndex); //remove value from index
            }
            else//put in new value in buffer
            {	
            	buffer.set(leastIndex, newValue);
            }
            if (buffer.size()==0)//if all values are removed end while loop
            	elementsLeft = false;
	        
		}//endof while loop
		writer.close();
		PhaseOne.fileCounter=PhaseOne.fileCounter+passes;
	}
	
	//This method is intended to test if merging into a single file is faster.
	private static void mergeIntoOneFile() throws IOException {
		int leastCID ; //holds the max value in the buffer
        int leastIndex ;	//the index of the buffer where the max value exists
        boolean elementsLeft=true;//the loop continues while this is true
        File file = new File("/home/negar/Desktop/phaseTwo");
        FileWriter fileToWrite = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(fileToWrite);
		
        while(elementsLeft) {
        	//for statement to find max value and index of max value
        	//if the single file is empty read from the buffer
        	if(file.length() == 0) {
        		//this means that the file is empty and we are in the first round of merging
        		//and we need to read from the buffer for the first time
        		leastCID = Integer.parseInt(buffer.get(0).substring(0,9));
        	}
        	else {
        		BufferedReader br = new BufferedReader(new FileReader(file));
        		leastCID = Integer.parseInt(br.readLine().substring(0,9));	
        	}
			
			leastIndex = 0;
			for (int i = 1; i < buffer.size(); i++) {
				// in the next if I should just check the next value
				//with the least value in the single file
	            if (Integer.parseInt(buffer.get(i).substring(0,9)) < leastCID) {//enters if only if value is greater
	                leastCID = Integer.parseInt(buffer.get(i).substring(0,9));
	                leastIndex = i;
	            }
	    	}//endof for loop
			//write max to new file and numbered at end
            writer.write(buffer.get(leastIndex)+"\n");
            //get next round ready
            String newValue =filePointers.get(leastIndex).readLine();//get new value from the specific file
            if (newValue==null)//check to see if endof file is reached
            {//if reached remove value and pointer
            	filePointers.remove(leastIndex); //remove pointer from filePointer
            	buffer.remove(leastIndex); //remove value from index
            }
            else//put in new value in buffer
            {	
            	buffer.set(leastIndex, newValue);
            }
            if (buffer.size()==0)//if all values are removed end while loop
            	elementsLeft = false;
            
        }//end of while loop
        writer.close();
		PhaseOne.fileCounter=PhaseOne.fileCounter+passes;
	}
		
}//end of class

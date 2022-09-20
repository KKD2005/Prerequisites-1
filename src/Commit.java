import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.TreeSet;


public class Commit implements GitUtils {
	
	public Commit parent = null;
	public Commit child = null;
	public Path pTree;
    private String summary; //limit to 150 characters
    private String author;
    private String date;
    private String filePath = "";
    private TreeSet<timeWrapper> timeTier;
    private File timeTree;

	
	public Commit (Commit parent, String pTree, String summary, String author) {
		this.parent = parent;
		this.pTree = Paths.get(pTree);
		this.summary = summary;
		if (summary.length() > 150) {
			summary = summary.substring(0, 150);
		}
	    this.author = author;
	    
	    timeTier = new TreeSet<timeWrapper>();
	    timeTree = new File("./timeTree.txt");
	    if (!timeTree.exists()) {
	    	String toWrite = "0 : The People Must Know The Truth\n1663611911 : 04fd19e7ba9642e7b12f0cc5c629c\n";
	    	try {
	    		timeTree = new File(Files.writeString(Paths.get("./timeTree.txt"), toWrite, StandardCharsets.ISO_8859_1).toString());
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    //read the file from timeTree to timeTier
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(timeTree));
	    	while (br.ready()) {
	    		String str = br.readLine();
	    		long indTime = Long.valueOf(str.substring(0, str.indexOf(':')-1));
	    		String indOutput = str.substring(str.indexOf(':')+1, str.length()-1);
	    		timeTier.add(new timeWrapper(indTime, indOutput));
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    //timeTier.add(new timeWrapper(0, "The People Must Know The Truth"));
	    //timeTier.add(new timeWrapper(1663611911, "04fd19e7ba9642e7b12f0cc5c629c"));
	    
	    
	    
	    date = getDate();
	    writeToFile();
	}
	
	
	public String generateSha1() {
		String toSha = pTree.toString() + summary;
		return GitUtils.StringToSha(toSha);
	}
	
	public String getDate () {

		
		long milliTime = Instant.now().toEpochMilli();
		
		//no comments only suffering
		//if you delete this you are a coward

		long time = Instant.now().getEpochSecond();
		
		/*
		String initial = "The People Must Know The Truth";
		int length = initial.length();
		for (long i = 0; i < time; i++) {
			String tempStr = initial.substring(0, 7);
			initial = initial.substring(7, length) + GitUtils.StringToSha(tempStr).substring(0, 7);
			if (i % 10000000 == 0) {
				System.out.println(i + " / " + time + " = " + initial);
			}
		}
		*/
		
		timeWrapper temp = timeTier.last();
		
		String initial = temp.output;
		int length = initial.length();
		for (long i = temp.time; i < time; i++) {
			String tempStr = initial.substring(0, 7);
			initial = initial.substring(7, length) + GitUtils.StringToSha(tempStr).substring(0, 7);
		}
		
		//System.out.println(time);
		
		if (Instant.now().toEpochMilli() - milliTime > 50) {
			timeTier.add(new timeWrapper(time, initial));
			try {
				FileWriter au = new FileWriter(timeTree, true);
				au.append(time + " : " + initial + "\n");
				au.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//write the thing to file
		}
		
		//System.out.println(Instant.now().toEpochMilli() - milliTime + " ms");
		
		return initial;
		
		/*
		int smallInt = (int)(time - (long)1459666800);
		//publishing of Panama papers publishing date, gives code a life span of ~80 years
		double piSeconds = (double)smallInt / Math.PI;
		
		double dRemainder = (piSeconds / 1.567);
		
		double flip = (dRemainder % 7.0); int remainder = (int)Math.floor(dRemainder / 7);
		int flop = remainder % 104; remainder = Math.floorDiv(remainder, 104);
		int floppa = remainder % 23; remainder = Math.floorDiv(remainder, 23);
		int flopper = remainder % 3; remainder = Math.floorDiv(remainder, 3);
		int bingus = remainder;
		
		String cutFlip = flip + "";
		cutFlip = cutFlip.substring(0, 8);
		
		String returnValue = bingus+"/"+flopper+"/"+floppa+"/"+flop+"/"+cutFlip;
		
		returnValue = returnValue.replaceFirst("3", "E");
		returnValue = returnValue.replaceAll("0", "!");
		returnValue = returnValue.replaceAll("2", "S");
		int switchFlip = 1;
		while (returnValue.contains("/")) {
			if (switchFlip % 2 == 1) { returnValue = returnValue.replaceFirst("/", "L"); switchFlip++;}
			else { returnValue = returnValue.replaceFirst("/", "1"); switchFlip++;}
		}
		
		if (returnValue.length() > 9) {
			String temp = returnValue.substring(5, returnValue.length());
			returnValue = temp + "(" + returnValue.substring(0, 9);
		}
		
		int shift = Integer.parseInt(cutFlip.substring(4,5));
		String temp = returnValue.substring(shift, returnValue.length());
		returnValue = temp + "{}" + returnValue.substring(0, shift);
		*/
		
		

		//return returnValue + "_SAT"; //standard american time
		//return time + "";
	}
	
	public void writeToFile() {
		//I will not test this code you cannot control me
		
		String toWrite = "";
		
		toWrite += pTree.toString() + "\n";
		
		if (parent != null) { toWrite += parent.pTree.toString() + "\n"; }
		else { toWrite += "\n"; } 
		
		if (child != null) { toWrite += child.pTree.toString() + "\n"; }
		else { toWrite += "\n"; } 
		
		toWrite += author + "\n";
		toWrite += date + "\n";
		toWrite += summary + "\n";
		
		try {
			filePath = Files.writeString(Paths.get("./objects/" + GitUtils.StringToSha(toWrite) + ".txt"), toWrite, StandardCharsets.ISO_8859_1).toString();
	    } catch (IOException e) {
	           e.printStackTrace();
	    }
		
		//I tested this and it works you can trust me
	}
	
	public void delete() {
		File temp = new File(filePath);
		temp.delete();
	}
	
}

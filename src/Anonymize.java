import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import com.csvreader.CsvWriter;



public class Anonymize {

	public Hashtable<String,String> routes; //Keys: real names, Object: anonymized names
	public Hashtable<String,String> tours;
	public Hashtable<String,String> locations;
	public Hashtable<String,String> commodities;
	int countRoutes = 0;
	int countTours =0;
	int countOrders = 0;
	int countCustomer =0;
	
	public static void main(String[] args) {
		Anonymize ano = new Anonymize();
		ano.routes("C:/Master/routes.csv");
		ano.ltlrates("C:/Master/ltlrates.csv");
		ano.sections("C:/Master/sections.csv");
		ano.orders("C:/Master/orders2.csv");

	}
	
	public Anonymize(){
		
	}
	
	public void routes(String input){
	
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		File file = new File("C:/Master/anoRoutes.csv");
		
		
		try{
			if (!file.exists()){
				file.createNewFile();
			}
			CsvWriter csvOutput = new CsvWriter(new FileWriter(file, true), ';');
			br = new BufferedReader(new FileReader(input));
			while((line = br.readLine()) != null){
				String[] route = line.split(csvSplit);
				if(this.routes.containsKey(route[0])){
					csvOutput.write(this.routes.get(route[0]));
				}else{
					this.routes.put(route[0], "Route"+countRoutes);
					csvOutput.write(this.routes.get(route[0]));
					this.countRoutes++;
				}
				for (int i = 1; i < route.length; i++) {
					if(this.tours.containsKey(route[i])){
						csvOutput.write(this.tours.get(route[i]));
					}else{
						this.tours.put(route[i], "Tour"+countTours);
						csvOutput.write(this.tours.get(route[i]));
						this.countTours++;
					}
				}
				csvOutput.endRecord();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sections(String input){
		
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		File file = new File("C:/Master/anoSections.csv");
		try{
			if (!file.exists()){
				file.createNewFile();
			}
			CsvWriter csvOutput = new CsvWriter(new FileWriter(file, true), ';');
			br = new BufferedReader(new FileReader(input));
			while((line = br.readLine()) != null){
				String[] section = line.split(csvSplit);
				if(this.tours.containsKey(section[0])){
					csvOutput.write(this.tours.get(section[0]));
				}else{
					this.tours.put(section[0], "Route"+this.countTours);
					csvOutput.write(this.tours.get(section[0]));
					this.countTours++;
				}
				for (int i = 2; i < section.length; i++) {
					csvOutput.write(section[i]);
				}
				csvOutput.endRecord();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void orders(String input){
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		File file = new File("C:/Master/anoOrders.csv");
		try{
			if (!file.exists()){
				file.createNewFile();
			}
			CsvWriter csvOutput = new CsvWriter(new FileWriter(file, true), ';');
			br = new BufferedReader(new FileReader(input));
			while((line = br.readLine()) != null){
				String[] order = line.split(csvSplit);
				if(this.tours.containsKey(order[0])){
					csvOutput.write(this.tours.get(order[0]));
				}else{
					this.tours.put(order[0], "Route"+this.countTours);
					csvOutput.write(this.tours.get(order[0]));
					this.countTours++;
				}
				if(this.tours.containsKey(order[1])){
					csvOutput.write(this.tours.get(order[1]));
				}else{
					this.tours.put(order[1], "Route"+this.countTours);
					csvOutput.write(this.tours.get(order[1]));
					this.countTours++;
				}
				
				for (int i = 2; i < 6; i++) {
					csvOutput.write(order[i]);
				}
				for (int i = 6; i < order.length; i++) {
					if(this.routes.containsKey(order[i])){
						csvOutput.write(this.routes.get(order[i]));
					}else{
						this.routes.put(order[i], "Route"+countRoutes);
						csvOutput.write(this.routes.get(order[i]));
						this.countRoutes++;
					}
				}
				csvOutput.endRecord();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void ltlrates(String input){
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		try{
			br = new BufferedReader(new FileReader(input));
			while((line = br.readLine()) != null){
				String[] path = line.split(csvSplit);
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

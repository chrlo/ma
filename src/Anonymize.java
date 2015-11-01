import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;



public class Anonymize {

	public Hashtable<String,String> routes; //Keys: real names, Object: anonymized names
	public Hashtable<String,String> tours;
	public Hashtable<String,String> locations;
	public Hashtable<String,String> commodities;
	public Hashtable<String,String> ltlrates;
	int countRoutes = 0;
	int countTours = 0;
	int countOrders = 0;
	int countLocations = 0;
	int countLTLrates = 0;
	int countCommodities = 0;
	
	public static void main(String[] args) {
		Anonymize ano = new Anonymize();
		ano.routes = new Hashtable<String,String>();
		ano.tours = new Hashtable<String,String>();
		ano.locations = new Hashtable<String,String>();
		ano.commodities = new Hashtable<String,String>();
		ano.ltlrates = new Hashtable<String,String>();
		ano.routes("C:/Users/Christopher/new_workspace/routes.csv");
		ano.ltlrates("C:/Users/Christopher/new_workspace/ltlrates.csv");
		ano.sections("C:/Users/Christopher/new_workspace/sections.csv");
		ano.orders("C:/Users/Christopher/new_workspace/orders2.csv");

	}
	
	public Anonymize(){
		
	}
	
	public void routes(String input){
		
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		File file = new File("C:/Users/Christopher/new_workspace/anoRoutes.csv");
		
		
		try{
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter csvOutput = new FileWriter(file, true);
			br = new BufferedReader(new FileReader(input));
			int lineCount = 0; // to see in which line of the read file we are
			while((line = br.readLine()) != null){ // read new line
				String[] route = line.split(csvSplit);
				if(lineCount!=0){ // line 0 only contains headers
					
					if(this.routes.containsKey(route[0])){ // route already has a anonymized name. use it!
						csvOutput.append(this.routes.get(route[0]));
						csvOutput.append(";");
					}else{ // route gets an anonymized name, which is written into the hashtable and csv
						this.routes.put(route[0], "Route"+countRoutes);
						csvOutput.append(this.routes.get(route[0]));
						csvOutput.append(";");
						this.countRoutes++;
					}
					for (int i = 1; i < route.length; i++) { // columns 1-... are the tours of the route
						if(this.tours.containsKey(route[i])){ // tour already has a anonymized name. use it!
							csvOutput.append(this.tours.get(route[i]));
							csvOutput.append(";");
						}else{ // tour gets an anonymized name, which is written into the hashtable and csv
							this.tours.put(route[i], "Tour"+countTours);
							csvOutput.append(this.tours.get(route[i]));
							csvOutput.append(";");
							this.countTours++;
						}
					}
					csvOutput.append("\n");
					csvOutput.flush();
				}else{
					for(int i = 0; i<route.length; i++){
						csvOutput.append(route[i]);
						csvOutput.append(";");
					}
					csvOutput.append("\n");
					csvOutput.flush();
				}
				lineCount++;
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
		File file = new File("C:/Users/Christopher/new_workspace/anoSections.csv");
		try{
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter csvOutput = new FileWriter(file, true);
			br = new BufferedReader(new FileReader(input));
			int lineCount = 0; // to see in which line of the read file we are
			while((line = br.readLine()) != null){
				String[] section = line.split(csvSplit);
				if(lineCount!=0){ // line 0 only contains headers
					
					if(this.tours.containsKey(section[0])){ // tour already has a anonymized name. use it!
						csvOutput.append(this.tours.get(section[0]));
						csvOutput.append(";");
					}else{ // tour gets an anonymized name, which is written into the hashtable and csv
						this.tours.put(section[0], "Route"+this.countTours);
						csvOutput.append(this.tours.get(section[0]));
						csvOutput.append(";");
						this.countTours++;
					}
					
					for (int i = 1; i < 3; i++) { // copy capacities and costs
						csvOutput.append(section[i]);
						csvOutput.append(";");
					}
				
					if(!section[3].equals("-")){
						if(this.tours.containsKey(section[3])){ // ltl rate already has a anonymized name. use it!
							csvOutput.append(this.ltlrates.get(section[3]));
							csvOutput.append(";");
						}else{ // ltl rate gets an anonymized name, which is written into the hashtable and csv
							this.tours.put(section[3], "Route"+this.countLTLrates);
							csvOutput.append(this.ltlrates.get(section[3]));
							csvOutput.append(";");
							this.countLTLrates++;
						}
					}else{
						csvOutput.append("-");
						csvOutput.append(";");
					}
					
					for (int i = 4; i < section.length; i++) { // copy capacities and costs
						csvOutput.append(section[i]);
						csvOutput.append(";");
					}
					csvOutput.append("\n");
					csvOutput.flush();
				}else{
					for(int i = 0; i<section.length; i++){
						csvOutput.append(section[i]);
						csvOutput.append(";");
					}
					csvOutput.append("\n");
					csvOutput.flush();
				}
				lineCount++;
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
		File file = new File("C:/Users/Christopher/new_workspace/anoOrders.csv");
		try{
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter csvOutput = new FileWriter(file, true);
			br = new BufferedReader(new FileReader(input));
			int lineCount = 0; // to see in which line of the read file we are
			while((line = br.readLine()) != null){
				String[] order = line.split(csvSplit);
				if(lineCount!=0){ // line 0 only contains headers
					
					if(this.commodities.containsKey(order[0])){ // commodity already has a anonymized name. use it!
						csvOutput.append(this.commodities.get(order[0]));
						csvOutput.append(";");
					}else{ // commodity gets an anonymized name, which is written into the hashtable and csv
						this.commodities.put(order[0], "Order"+this.countCommodities);
						csvOutput.append(this.commodities.get(order[0]));
						csvOutput.append(";");
						this.countCommodities++;
					}
					if(!this.locations.containsKey(order[0].substring(order[0].indexOf("-")))){ // start location gets an anonymized name, which is written into the hashtable and csv
						this.locations.put(order[0].substring(order[0].indexOf("-")), "Location"+this.countLocations);
						this.countLocations++;
					}
					if(this.locations.containsKey(order[1])){ // end location already has a anonymized name. use it!
						csvOutput.append(this.locations.get(order[1]));
						csvOutput.append(";");
					}else{ // end location gets an anonymized name, which is written into the hashtable and csv
						this.locations.put(order[1], "Location"+this.countLocations);
						csvOutput.append(this.locations.get(order[1]));
						csvOutput.append(";");
						this.countLocations++;
					}
				
					for (int i = 2; i < 6; i++) { // copy capacities and costs
						csvOutput.append(order[i]);
						csvOutput.append(";");
					}
					for (int i = 6; i < order.length; i++) { // anonymize routes of the commodity
						if(this.routes.containsKey(order[i])){  // route already has a anonymized name. use it!
							csvOutput.append(this.routes.get(order[i]));
							csvOutput.append(";");
						}else{ // route gets an anonymized name, which is written into the hashtable and csv
							this.routes.put(order[i], "Route"+countRoutes);
							csvOutput.append(this.routes.get(order[i]));
							csvOutput.append(";");
							this.countRoutes++;
						}
					}
					csvOutput.append("\n");
					csvOutput.flush();
				}else{
					for(int i = 0; i<order.length; i++){
						csvOutput.append(order[i]);
						csvOutput.append(";");
					}
					csvOutput.append("\n");
					csvOutput.flush();
				}
				lineCount++;
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
		File file = new File("C:/Users/Christopher/new_workspace/anoLTLRates.csv");
		try{
			FileWriter csvOutput = new FileWriter(file, true);
			br = new BufferedReader(new FileReader(input));
			int lineCount = 0; // to see in which line of the read file we are
			while((line = br.readLine()) != null){
				
				String[] rate = line.split(csvSplit);
				if(lineCount!=0){ // line 0 only contains headers
					if(this.ltlrates.containsKey(rate[0])){ // ltl rate already has a anonymized name. use it!
						csvOutput.append(this.ltlrates.get(rate[0]));
						csvOutput.append(";");
					}else{ // ltl rate gets an anonymized name, which is written into the hashtable and csv
						this.ltlrates.put(rate[0], "LTLRate"+this.countLTLrates);
						csvOutput.append(this.ltlrates.get(rate[0]));
						csvOutput.append(";");
						this.countLTLrates++;
					}
					for(int i = 1; i<rate.length; i++){
						csvOutput.append(rate[i]);
						csvOutput.append(";");
					}
					csvOutput.append("\n");
					csvOutput.flush();
				}else{
					for(int i = 0; i<rate.length; i++){
						csvOutput.append(rate[i]);
						csvOutput.append(";");
					}
					csvOutput.append("\n");
					csvOutput.flush();
				}
				lineCount++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

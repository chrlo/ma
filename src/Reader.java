import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Reader {
	
	//create capacity and weight vector
//	public Hashtable<Integer,Double[]> commodities;  // first double= origin, second= destination, third= weight
//	public Hashtable<Integer,Double[]> services; // first double= capacity, second= fixed cost, third= variable cost coefficient
//	public Hashtable<Integer,ArrayList<String>> paths; // first element on list= commodity, rest= services on path
//	public Hashtable<String, Integer> pathIDs; // needed for mapping the correct commodity to each path
//	public Hashtable <String,Integer> serviceMap; // Maps Service Number to Name (used for variables y_i)
//	public Hashtable <String,Double> locationMap; // Maps Location Number to Name 
//	public Hashtable <String,Double> commodityMap; // Maps Location Number to Name 
//	public Hashtable <String, Double> LTLRates; // maps names of LTL Tariffs to their cost rates
	
	public Reader(){

	}
	
	public static void main(String[] args) {

	}
	
	public Instance read(Instance instance){
	 
		readLTLRates(instance, "C:/Master/ltlrates.csv");		
		readPaths(instance, "C:/Master/routes.csv");
		readServices(instance, "C:/Master/sections.csv");
		readCommodities(instance, "C:/Master/orders2.csv");
		
		return instance;
	}
	
	public void readPaths(Instance instance, String input){
		
		Hashtable<Integer,ArrayList<String>> paths = new Hashtable<Integer,ArrayList<String>>();
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		int count = 0;
		try{
			br = new BufferedReader(new FileReader(input));
			while((line = br.readLine()) != null){
				
					ArrayList<String> list = new ArrayList<String>();
					paths.put(count, list);
					String[] path = line.split(csvSplit);
					if(count!= 0){
						instance.getPathIDs().put(path[0],count);
						for (int i = 0; i < path.length; i++) {
							paths.get(count).add(i, path[i]);
						}
					}
				count++;
			}
			instance.setPaths(paths);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readServices(Instance instance, String input1){
		Hashtable <String, Double> lTLRates = new Hashtable <String, Double>();
		Hashtable <String,Integer> serviceMap = new Hashtable <String,Integer>();
		Hashtable<Integer,Double[]> services = new Hashtable<Integer,Double[]>();		
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		int count = 0;
		try{
			br = new BufferedReader(new FileReader(input1));
			while((line = br.readLine()) != null){
				if(count!=0){
					String[] service = line.split(csvSplit);
					serviceMap.put(service[0], count);
					Double[] attributes;
					attributes = new Double[3];
					attributes[0] = Double.parseDouble(service[6]);
					attributes[1] = Double.parseDouble(service[5]);
					if(!service[3].equals("-")){ //"-" means, we're having an ftl tariff because it's the column "ID LTL"
						attributes[2] = lTLRates.get(service[3]);
					}else{
						attributes[2] = 0.;
					}
					

					services.put(count, attributes);
					}
				count++;
			}
			instance.setServices(services);
			instance.setLTLRates(lTLRates);
			instance.setServiceMap(serviceMap);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readLTLRates(Instance instance, String source){ // for now, LTL rates will be the average value of all LTL rates for one tariff
		Hashtable <String, Double> lTLRates = new Hashtable <String, Double>();
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		int count = 0;
		int numberOfEqualIDs = 0;
		try{
			br = new BufferedReader(new FileReader(source));
			String ltlName = null;
			while((line = br.readLine()) != null){
				if(count == 1){
					String[] ltlRate = line.split(csvSplit);
					ltlName = ltlRate[0];
					lTLRates.put(ltlRate[0], Double.parseDouble(ltlRate[3].replaceAll(",", ".")));
					numberOfEqualIDs = 1;
					
				}else if(count!=0){
						String[] ltlRate = line.split(csvSplit);
						String newltlName = ltlRate[0];
						if(newltlName.equals(ltlName)){
							Double dummy = lTLRates.get(ltlRate[0]); // add new cost rate
							dummy += Double.parseDouble(ltlRate[3].replaceAll(",", "."));
							lTLRates.put(ltlRate[0], dummy);
							numberOfEqualIDs++;
						}else{
							
							Double dummy = lTLRates.get(ltlName); // divide value of sum of previous cost rates by their number to get the average cost rate
							dummy = dummy/numberOfEqualIDs;
							lTLRates.put(ltlName, dummy);
							ltlName = ltlRate[0]; // set new LTL tariff ID
							lTLRates.put(ltlRate[0], Double.parseDouble(ltlRate[3].replaceAll(",", "."))); // set value of new cost rate
							numberOfEqualIDs = 1;
						}
					
					
				}
				instance.setLTLRates(lTLRates);
				count++;
			}
			
			
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
public void readCommodities(Instance instance, String input1){
		Hashtable <String,Double> locationMap = new Hashtable <String,Double>(); 
		Hashtable <String,Double> commodityMap = new Hashtable <String,Double>();
		Hashtable<Integer,ArrayList<String>> paths = instance.getPaths();
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		int count = 0;
		int countCom = 1;
		try{
			br = new BufferedReader(new FileReader(input1));
			while((line = br.readLine()) != null){
				if(count!=0){ // first line contains only the names of the columns
					String[] com = line.split(csvSplit);
					Double[] commodity = new Double[3]; // array for each commodity containing source, sink (each as id) and weight
					
					if(!locationMap.containsKey(com[0].substring(0, com[0].indexOf("-")))){ // if (start) location comes up for the first time, an id is created						
						locationMap.put(com[0].substring(0, com[0].indexOf("-")),(double)countCom);
						commodity[0] = (double) (countCom); // start location id of commodity is set
						countCom++;
					}else{ 
						commodity[0] = locationMap.get(com[0].substring(0, com[0].indexOf("-")-1)); // if (start) location already has an id, the id is put into the array
					}
					if(!locationMap.containsKey(com[1])){
						locationMap.put(com[1],(double) countCom); //if (end) location comes up for the first time, an id is created
						commodity[1] = (double) (countCom); // end location id of commodity is set
						countCom++;
					}else{
						commodity[1] = locationMap.get(com[1]); //if (end) location already has an id, the id is put into the array
					}
					
					commodity[2] = Double.parseDouble(com[3].replaceAll(",", ".")); // set the weight of the commodity
					instance.getCommodities().put(count, commodity); //add commodity to list
					for (int i = 6; i < com.length; i++) { // all paths which belong to the commodity (od-pair) get the information that they do
						paths.get(instance.getPathIDs().get(com[i])).set(0, com[0]);
					}
					commodityMap.put(com[0], (double) count);
				}				
				count++;
			}
			instance.setPaths(paths);
			instance.setCommodityMap(commodityMap);
			instance.setLocationMap(locationMap);
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
}

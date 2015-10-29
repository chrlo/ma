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
		Reader reader = new Reader();
		Instance instance = new Instance();
		instance = reader.read(instance);
	}
	
	public Instance read(Instance instance){
	 
		instance = readLTLRates(instance, "C:/Master/ltlrates.csv");		
		instance = readServices(instance, "C:/Master/sections.csv");
		instance =readPaths(instance, "C:/Master/routes.csv");
		instance = readCommodities(instance, "C:/Master/orders2.csv");
		
		return instance;
	}
	
	public Instance readPaths(Instance instance, String input){
		
		Hashtable<Integer,ArrayList<String>> paths = new Hashtable<Integer,ArrayList<String>>();
		Hashtable<Integer,Double[]> services = instance.getServices();
		Hashtable <String,Integer> serviceMap = instance.getServiceMap();
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		int count = 0; //for path IDs
		try{
			br = new BufferedReader(new FileReader(input));
			while((line = br.readLine()) != null){
				
					ArrayList<String> list = new ArrayList<String>(); // List for new path
					paths.put(count, list);
					String[] path = line.split(csvSplit);
					if(count!= 0){// line 0 contains headers of the columns
						instance.getPathIDs().put(path[0],count); // give new path an ID
						for (int i = 0; i < path.length; i++) { // add services to list which belong to path and put "unused"(case i=0), where commodity will appear later
							if(i==0){
								paths.get(count).add(i, "unused");
							}else{
								paths.get(count).add(i, path[i]);
								services.get(serviceMap.get(path[i]))[3]++; // one more path is assigned to service now
								
							}							
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
		return instance;
	}
	
	public Instance readServices(Instance instance, String input1){
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
				if(count!=0){ // line 0 contains headers of the columns
					String[] service = line.split(csvSplit);
					serviceMap.put(service[0], count); // connect Service ID to a unique number
					Double[] attributes; // array for attributes of the service (capacity, fix and variable costs,number of assigned paths)
					attributes = new Double[4]; 
					attributes[3] = 0.; // initialize number of assigned paths with 0 
					attributes[0] = Double.parseDouble(service[6]); // Capacity (weight)
					attributes[1] = Double.parseDouble(service[5]); // fix costs
					if(!service[3].equals("-")){ //"-" means, we're having an ftl tariff because it's the column "ID LTL"
						attributes[2] = lTLRates.get(service[3]); //variable costs
					}else{
						attributes[2] = 0.; //variable costs
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
		return instance;
	}
	
	public Instance readLTLRates(Instance instance, String source){ // for now, LTL rates will be the average value of all LTL rates for one tariff
		Hashtable <String, Double> lTLRates = new Hashtable <String, Double>();
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		int count = 0;
		int numberOfEqualIDs = 0; // counts number of LTL steps for one ID to calculate the average by dividing by that number
		try{
			br = new BufferedReader(new FileReader(source));
			String ltlName = null; // ID to compare the current line with, to know whether it differs from the previous one (because we want to calculate the average of all with the same IDs)
			while((line = br.readLine()) != null){
				if(count == 1){ // induces numberOfEqualIDs with 1 and set the ID for comparison with the next line
					String[] ltlRate = line.split(csvSplit);
					ltlName = ltlRate[0];
					lTLRates.put(ltlRate[0], Double.parseDouble(ltlRate[3].replaceAll(",", ".")));
					numberOfEqualIDs = 1;
					
				}else if(count!=0){ // line 0 contains headers of the columns
						String[] ltlRate = line.split(csvSplit);
						String newltlName = ltlRate[0]; // current ID
						if(newltlName.equals(ltlName)){ // current ID equals previous ID(s)
							Double dummy = lTLRates.get(ltlRate[0]); // add new cost rate
							dummy += Double.parseDouble(ltlRate[3].replaceAll(",", "."));
							lTLRates.put(ltlRate[0], dummy);
							numberOfEqualIDs++;
						}else{ // current ID doesnt equal previous ID(s)
							
							Double dummy = lTLRates.get(ltlName); // divide value of sum of previous cost rates by their number to get the average cost rate
							dummy = dummy/numberOfEqualIDs;
							lTLRates.put(ltlName, dummy);
							ltlName = ltlRate[0]; // set new LTL tariff ID
							lTLRates.put(ltlRate[0], Double.parseDouble(ltlRate[3].replaceAll(",", "."))); // set value of new cost rate
							numberOfEqualIDs = 1; // start with new ltl tariff
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
		return instance;
	}
	
public Instance readCommodities(Instance instance, String input1){
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
				if(count!=0){ // line 0 contains names of the columns
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
		
		return instance;
		}	
}

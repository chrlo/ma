import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Reader {
	
	//create capacity and weight vector
	public Hashtable<Integer,Double[]> commodities;  // first double= origin, second= destination, third= weight
	public Hashtable<Integer,Double[]> services; // first double= capacity, second= fixed cost, third= variable cost coefficient
	public Hashtable<Integer,ArrayList<String>> paths; // first element on list= commodity, rest= services on path
	public Hashtable<String, Integer> pathIDs; // needed for mapping the correct commodity to each path
	public Hashtable <String,Integer> serviceMap; // Maps Service Number to Name (used for variables y_i)
	public Hashtable <String,Double> commodityMap; // Maps Commodity Number to Name 
	public Hashtable <String, Double> LTLRates; // maps names of LTL Tariffs to their cost rates
	
	public Reader(){
		this.paths = new Hashtable<Integer,ArrayList<String>>();
		this.pathIDs = new Hashtable<String, Integer>();
		this.serviceMap = new Hashtable<String,Integer>();
		this.services = new Hashtable<Integer,Double[]>();
		this.LTLRates = new Hashtable<String,Double>();
		this.commodities = new Hashtable<Integer,Double[]>();
		this.commodityMap = new Hashtable<String,Double>();
	}
	
	public static void main(String[] args) {
		Reader reader = new Reader(); 
		reader.readLTLRates("C:/Master/ltlrates.csv");		
		reader.readPaths("C:/Master/routes.csv");
		reader.readServices("C:/Master/sections.csv");
		reader.readCommodities("C:/Master/orders2.csv");
		System.out.println(reader.paths.get(28864).get(0));

	}
	
	public void readPaths(String input){
		
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		int count = 0;
		try{
			br = new BufferedReader(new FileReader(input));
			while((line = br.readLine()) != null){
				
					ArrayList<String> list = new ArrayList<String>();
					this.paths.put(count, list);
					String[] path = line.split(csvSplit);
					if(count!= 0){
						this.pathIDs.put(path[0],count);
						for (int i = 0; i < path.length; i++) {
							this.paths.get(count).add(i, path[i]);
						}
					}
				count++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readServices(String input1){
		
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		int count = 0;
		try{
			br = new BufferedReader(new FileReader(input1));
			while((line = br.readLine()) != null){
				if(count!=0){
					String[] service = line.split(csvSplit);
					this.serviceMap.put(service[0], count);
					Double[] attributes;
					attributes = new Double[3];
					attributes[0] = Double.parseDouble(service[6]);
					attributes[1] = Double.parseDouble(service[5]);
					if(!service[3].equals("-")){
						attributes[2] = LTLRates.get(service[3]);
					}else{
						attributes[2] = 0.;
					}
					

					this.services.put(count, attributes);
					}
				count++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readLTLRates(String source){ // for now, LTL rates will be the average value of all LTL rates for one tariff
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
					this.LTLRates.put(ltlRate[0], Double.parseDouble(ltlRate[3].replaceAll(",", ".")));
					numberOfEqualIDs = 1;
					
				}else if(count!=0){
						String[] ltlRate = line.split(csvSplit);
						String newltlName = ltlRate[0];
						if(newltlName.equals(ltlName)){
							Double dummy = this.LTLRates.get(ltlRate[0]); // add new cost rate
							dummy += Double.parseDouble(ltlRate[3].replaceAll(",", "."));
							this.LTLRates.put(ltlRate[0], dummy);
							numberOfEqualIDs++;
						}else{
							
							Double dummy = this.LTLRates.get(ltlName); // divide value of sum of previous cost rates by their number to get the average cost rate
							dummy = dummy/numberOfEqualIDs;
							this.LTLRates.put(ltlName, dummy);
							ltlName = ltlRate[0]; // set new LTL tariff ID
							this.LTLRates.put(ltlRate[0], Double.parseDouble(ltlRate[3].replaceAll(",", "."))); // set value of new cost rate
							numberOfEqualIDs = 1;
						}
					
					
				}
				
				count++;
			}
			
			
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
public void readCommodities(String input1){
		
		BufferedReader br = null;
		String line = "";
		String csvSplit = ";";
		int count = 0;
		int countCom = 1;
		try{
			br = new BufferedReader(new FileReader(input1));
			while((line = br.readLine()) != null){
				if(count!=0){
					String[] com = line.split(csvSplit);
					Double[] commodity = new Double[3];
					if(!this.commodityMap.containsKey(com[0].substring(0, com[0].indexOf("-")))){						
						this.commodityMap.put(com[0].substring(0, com[0].indexOf("-")),(double)countCom);
						commodity[0] = (double) (countCom);
						countCom++;
					}else{
						commodity[0] = commodityMap.get(com[0].substring(0, com[0].indexOf("-")-1));
					}
					if(!this.commodityMap.containsKey(com[1])){
						this.commodityMap.put(com[1],(double) countCom);
						commodity[1] = (double) (countCom);
						countCom++;
					}else{
						commodity[1] = commodityMap.get(com[1]);
					}
					
					commodity[2] = Double.parseDouble(com[3].replaceAll(",", "."));
					this.commodities.put(count, commodity);
					for (int i = 6; i < com.length; i++) {
						paths.get(this.pathIDs.get(com[i])).set(0, com[0]);
					}
				}
				count++;
			}
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
}

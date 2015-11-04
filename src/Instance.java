import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;


public class Instance {
	
	private Hashtable<Integer,Double[]> commodities;  // first double= origin, second= destination, third= weight
	private Hashtable<Integer,Double[]> services; // first double= capacity, second= fixed cost, third= variable cost coefficient
	private Hashtable<Integer,ArrayList<String>> paths; // first element on list= commodity, rest= services on path
	private Hashtable<String, Integer> pathIDs; // needed for mapping the correct commodity to each path
	private Hashtable <String,Integer> serviceMap; // Maps Service Number to Name (used for variables y_i)
	private Hashtable <String,Double> locationMap; // Maps Location Number to Name 
	private Hashtable <String,Double> commodityMap; // Maps Location Number to Name 
	private Hashtable <String, Double> LTLRates; // maps names of LTL Tariffs to their cost rates
	
	
	public Instance(){
		this.paths = new Hashtable<Integer,ArrayList<String>>();
		this.pathIDs = new Hashtable<String, Integer>();
		this.serviceMap = new Hashtable<String,Integer>();
		this.services = new Hashtable<Integer,Double[]>();
		this.LTLRates = new Hashtable<String,Double>();
		this.commodities = new Hashtable<Integer,Double[]>();
		this.locationMap = new Hashtable<String,Double>();
		this.commodityMap = new Hashtable<String,Double>();
	}
	
	
	
	public Hashtable<Integer, Double[]> getCommodities() {
		return commodities;
	}
	public void setCommodities(Hashtable<Integer, Double[]> commodities) {
		this.commodities = commodities;
	}
	public Hashtable<Integer, Double[]> getServices() {
		return services;
	}
	public void setServices(Hashtable<Integer, Double[]> services) {
		this.services = services;
	}
	public Hashtable<Integer, ArrayList<String>> getPaths() {
		return paths;
	}
	public void setPaths(Hashtable<Integer, ArrayList<String>> paths) {
		this.paths = paths;
	}
	public Hashtable<String, Integer> getPathIDs() {
		return pathIDs;
	}
	public void setPathIDs(Hashtable<String, Integer> pathIDs) {
		this.pathIDs = pathIDs;
	}
	public Hashtable<String, Integer> getServiceMap() {
		return serviceMap;
	}
	public void setServiceMap(Hashtable<String, Integer> serviceMap) {
		this.serviceMap = serviceMap;
	}
	public Hashtable<String, Double> getLocationMap() {
		return locationMap;
	}
	public void setLocationMap(Hashtable<String, Double> locationMap) {
		this.locationMap = locationMap;
	}
	public Hashtable<String, Double> getCommodityMap() {
		return commodityMap;
	}
	public void setCommodityMap(Hashtable<String, Double> commodityMap) {
		this.commodityMap = commodityMap;
	}
	public Hashtable<String, Double> getLTLRates() {
		return LTLRates;
	}
	public void setLTLRates(Hashtable<String, Double> lTLRates) {
		this.LTLRates = lTLRates;
	}
	public void printServiceCapacities(){
		Enumeration enu = this.serviceMap.keys();
		while(enu.hasMoreElements()){
			Object element = enu.nextElement();
			System.out.println("Service: "+element+" Capacity: "+ services.get(serviceMap.get(element))[0]);
		}
	}
	

}

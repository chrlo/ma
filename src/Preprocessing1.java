import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class Preprocessing1 {
/*
 * This preprocessing  is supposed to to delete all routes and services which are definitely not used by the transport orders. 
 * As the routes are permanent and the orders come in regularly, there are many paths in the network which are useless for certain periods and can therefore be deleted.
 */
	public Preprocessing1(){
		
	}
	
	public Instance preprocess(Instance instance){
		Hashtable<Integer,Double[]> commodities = instance.getCommodities();  // first double= origin, second= destination, third= weight
		Hashtable<Integer,Double[]> services = instance.getServices(); // first double= capacity, second= fixed cost, third= variable cost coefficient
		Hashtable<Integer,ArrayList<String>> paths = instance.getPaths(); // first element on list= commodity, rest= services on path
		Hashtable<String, Integer> pathIDs = instance.getPathIDs(); // needed for mapping the correct commodity to each path
		Hashtable <String,Integer> serviceMap = instance.getServiceMap(); // Maps Service Number to Name (used for variables y_i)
		Hashtable <String,Double> locationMap = instance.getLocationMap(); // Maps Location Number to Name 
		Hashtable <String,Double> commodityMap = instance.getCommodityMap(); // Maps Location Number to Name 
		Hashtable <String, Double> LTLRates = instance.getLTLRates();
		Enumeration enuPath = paths.keys();
		while(enuPath.hasMoreElements()){ //iterate over all paths
			
			for (int i = 1; i < paths.get(enuPath).size(); i++) { // iterate over all services on the path
			
					
				}
			
		}
		return instance;
	}
}

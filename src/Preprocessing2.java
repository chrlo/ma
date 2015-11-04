import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class Preprocessing2 {
	/*
	 * This preprocessing  is supposed to to adjust upper bounds of the services. 
	 * As we know all possible flows on a service, we can determine, whether its upper bound might never be reached and can be reduced.
	 */
	public static void main(String[] args) {
		Reader reader = new Reader();
		Instance instance = new Instance();
		instance = reader.read(instance);
		Preprocessing2 prepro = new Preprocessing2();
		instance = prepro.preprocess(instance);
		instance.printServiceCapacities();

	}
	public Preprocessing2(){
		
	}
	
	public Instance preprocess(Instance instance){
		
		Hashtable<Integer, Double[]> commodities = instance.getCommodities(); // first int= origin, second= destination, third= weight
		Hashtable<Integer,Double[]> services = instance.getServices(); // first double= capacity, second= fixed cost, third= variable cost coefficient
		Hashtable<Integer,ArrayList<String>> paths = instance.getPaths(); // first element on list= commodity, rest= services on path
		Hashtable <String,Integer> serviceMap = instance.getServiceMap(); // Maps Service Number to Name (used for variables y_i)
		Hashtable<String,Double> commodityMap = instance.getCommodityMap(); // maps commodity names to a unique number
	
		Hashtable <String, Double> maxNecessaryCapacity = new Hashtable<String,Double>();
		Hashtable <String, ArrayList<String>> alreadyUsedCommodities = new Hashtable <String, ArrayList<String>>(); // to remember for every service, which commodities have already been used to count its mac necessary capacity, as every commoditiy only needs to be counted once
		
		Enumeration pathEnu = paths.keys();
		while(pathEnu.hasMoreElements()){ // calculate max possible flow for every service 
			Object path = pathEnu.nextElement();
			for (int i = 1; i < paths.get(path).size(); i++) {
				if(paths.get(path).get(i).equals("Tour2")){
					System.out.println("HAAAAAAAAAAAAALT");
				}
				if(!maxNecessaryCapacity.containsKey(paths.get(path).get(i))){ // create new entry if service appears for the first time
					ArrayList<String> alreadyUsed = new ArrayList<String>();
					alreadyUsed.add(paths.get(path).get(0));
					alreadyUsedCommodities.put(paths.get(path).get(i), alreadyUsed);
					maxNecessaryCapacity.put(paths.get(path).get(i), commodities.get((int) Math.round(commodityMap.get(paths.get(path).get(0))))[2]);
				}else{ // if entry already exists, add commodity weight to max possible flow value so far
					if(!alreadyUsedCommodities.get(paths.get(path).get(i)).contains(paths.get(path).get(0))){
						double dummy = maxNecessaryCapacity.get(paths.get(path).get(i));
						dummy += commodities.get((int) Math.round(commodityMap.get(paths.get(path).get(0))))[2];
						maxNecessaryCapacity.put(paths.get(path).get(i), dummy);
						alreadyUsedCommodities.get(paths.get(path).get(i)).add(paths.get(path).get(0));
					}
				}
			}
		}
		
		Enumeration serviceEnu = serviceMap.keys();
		while(serviceEnu.hasMoreElements()){
			Object element = serviceEnu.nextElement();
			System.out.println("Service " + element + " hat Kapazität: " + services.get(serviceMap.get(element))[0] + " vs. Max nötige Kapazität: " + maxNecessaryCapacity.get(element));
			if(services.get(serviceMap.get(element))[0] > maxNecessaryCapacity.get(element)){
				services.get(serviceMap.get(element))[0] = maxNecessaryCapacity.get(element);
			}
		}
		instance.setServices(services);
		return instance;
	}

}

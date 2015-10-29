import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
public class MIP {
	
	public static void main(String[] args) {
		try{
			GRBEnv env = new GRBEnv("mip.log");
			GRBModel model = new GRBModel(env);
			
			//Read input 
			Instance instance = new Instance();
			Reader reader = new Reader();
			instance = reader.read(instance);
			
			//preprocessing
			//.
			//.
			//.
			
			//create capacity and weight vector
			Hashtable<Integer, Double[]> commodities = instance.getCommodities();  // first int= origin, second= destination, third= weight
			Hashtable<Integer,Double[]> services = instance.getServices(); // first double= capacity, second= fixed cost, third= variable cost coefficient
			Hashtable<Integer, ArrayList<String>> paths = instance.getPaths(); // first element on list= commodity, rest= services on path
			Hashtable<String,Double> commodityMap = instance.getCommodityMap(); // maps commodity names to a unique number
			// fill them with data:
			
//			commodities.put(0,new Integer[] {1,1,1});
//			commodities.put(1,new Integer[] {2,2,1});
//			commodities.put(2,new Integer[] {3,3,1});
//			
//			services.put(0,new Double[] {1.,0.,1.});
//			services.put(1,new Double[] {1.,0.,1.});
//			services.put(2,new Double[] {1.,0.,1.});
//			services.put(3,new Double[] {1.,4.,0.});
//			services.put(4,new Double[] {1.,4.,0.});
//			services.put(5,new Double[] {1.,3.,0.});
//			services.put(6,new Double[] {2.,2.,0.});
//			services.put(7,new Double[] {1.,0.,1.});
//			services.put(8,new Double[] {1.,0.,1.});
//			services.put(9,new Double[] {1.,0.,1.});
//			ArrayList<Integer> list1 = new ArrayList<Integer>();
//			list1.add(0);
//			list1.add(3);
//			paths.put(0, list1);
//			ArrayList<Integer> list2 = new ArrayList<Integer>();
//			list2.add(0);
//			list2.add(0);
//			list2.add(6);
//			list2.add(7);
//			paths.put(3, list2);
//			ArrayList<Integer> list3 = new ArrayList<Integer>();
//			list3.add(1);
//			list3.add(4);
//			paths.put(1, list3);
//			ArrayList<Integer> list4 = new ArrayList<Integer>();
//			list4.add(1);
//			list4.add(1);
//			list4.add(6);
//			list4.add(8);
//			paths.put(4, list4);
//			ArrayList<Integer> list5 = new ArrayList<Integer>();
//			list5.add(2);
//			list5.add(5);
//			paths.put(2, list5);
//			ArrayList<Integer> list6 = new ArrayList<Integer>();
//			list6.add(2);
//			list6.add(2);
//			list6.add(6);
//			list6.add(9);
//			paths.put(5, list6);
			
			
			
			
			//create variables
			GRBVar[] y;
			y = new GRBVar[services.size()];
			for (int i = 0; i < y.length; i++) {
				
				y[i]  = model.addVar(0.0, 4.0, 0.0, GRB.BINARY, "y"+(i+1));
			}
			
			Hashtable<Double,Hashtable<Double,GRBVar>> x = new Hashtable<Double,Hashtable<Double,GRBVar>>(); // key= commodities, element= hashtable of services, matching service number to variable
			GRBVar[] z;
			z = new GRBVar[paths.size()]; //one variable for each path
			
			for (int k = 0; k < z.length; k++) {
				
				z[k]  = model.addVar(0.0, 3.0, 0.0, GRB.BINARY, "z"+(k+1));
	
			}
//			z[0]  = model.addVar(0.0, 0.0, 0.0, GRB.BINARY, "z"+(1));
//			z[1]  = model.addVar(0.0, 0.0, 0.0, GRB.BINARY, "z"+(2));
//			z[2]  = model.addVar(1.0, 1.0, 0.0, GRB.BINARY, "z"+(3));
//			z[3]  = model.addVar(1.0, 1.0, 0.0, GRB.BINARY, "z"+(4));
//			z[4]  = model.addVar(1.0, 1.0, 0.0, GRB.BINARY, "z"+(5));
//			z[5]  = model.addVar(0.0, 0.0, 0.0, GRB.BINARY, "z"+(6));
			
			for (int l = 0; l < paths.size(); l++) { // every service on a path may create an x variable, as every path is uniquely assigned to a commodity (one service might be in multiple paths for the same commodity)
				if(x.get(commodityMap.get(paths.get(l).get(0))) == null){ //create new hashmap for a commodity if commodity appears for the first time
					Hashtable<Double,GRBVar> hash = new Hashtable<Double,GRBVar>();
					x.put(commodityMap.get(paths.get(l).get(0)), hash);
				}
				for (int m = 1; m < paths.get(l).size(); m++){
					if(!x.get(commodityMap.get(paths.get(l).get(0))).contains(commodityMap.get(paths.get(l).get(m)))){// if variable doesnt already exist, create it
						GRBVar var = model.addVar(0.0, 3.0, 0.0, GRB.BINARY, "x"+(commodityMap.get(paths.get(l).get(0))+1)+","+(commodityMap.get(paths.get(l).get(m))+1));;
						x.get(commodityMap.get(paths.get(l).get(0))).put(commodityMap.get(paths.get(l).get(m)), var);
					}
				}
				
			}	
			
			model.update();
			
			//set objective
			GRBLinExpr expr = new GRBLinExpr();
			for (int i = 0; i < y.length; i++) {
			
					expr.addTerm(services.get(i)[1], y[i]); // fixed cost times y_i
					//System.out.println(services.get(i)[1]+","+y[i].get(GRB.StringAttr.VarName));
			
			}
			Enumeration e = x.keys();
			int countX = 0; //count the number of elements in x (x-variables)
			while(e.hasMoreElements()) {
				Object element = e.nextElement(); // Commodity
				Enumeration f = x.get(element).keys();
				while(f.hasMoreElements()) {
					countX++;
					Object element2 = f.nextElement(); // Service
					expr.addTerm(services.get(element2)[2]*commodities.get(element)[2], x.get(element).get(element2)); //variable costs times weight times x_s^k
				//	System.out.println(services.get(element2)[2]*commodities.get(element)[2]+","+x.get(element).get(element2).get(GRB.StringAttr.VarName));
				}
			}
			
				
			model.setObjective(expr,GRB.MINIMIZE);
			
			//Add constraints
			
			GRBLinExpr[] beta;
			beta = new GRBLinExpr[y.length];
			GRBLinExpr[] delta;
			delta = new GRBLinExpr[countX];
			GRBLinExpr[] epsilon;
			epsilon = new GRBLinExpr[countX];
			GRBLinExpr[] zeta;
			zeta = new GRBLinExpr[commodities.size()];
			
			for (int i = 0; i < y.length; i++) {
				
					beta[i] = new GRBLinExpr();
					beta[i].addTerm(services.get(i)[0], y[i]); //u_w(s)y_s
					//System.out.println(services.get(i)[0]+""+y[i].get(GRB.StringAttr.VarName));
					Enumeration enu = x.keys(); 
					while(enu.hasMoreElements()){ //iterate over commodities
						Object element = enu.nextElement();
						if (x.get(element).get(i) != null) {// x_s^{k} exists: s could be used for k and the relevant term is added 
							beta[i].addTerm(-(commodities.get(element)[2]), x.get(element).get(i)); //-d_w(k)x_s^k
						//	System.out.println(-(commodities.get(element)[2])+","+ x.get(element).get(i).get(GRB.StringAttr.VarName));
						}
						
					}
					
					model.addConstr(beta[i], GRB.GREATER_EQUAL, 0, "beta"+(i+1));
	
			}
			
			Enumeration enu2 = x.keys();
			int countDelta = 0; 
			while (enu2.hasMoreElements()) { // enumerate over commodities
					Object element = enu2.nextElement();
					Enumeration enu22 = x.get(element).keys(); 
					while(enu22.hasMoreElements()){ // enumerate over services
						Object element2 = enu22.nextElement();
						delta[countDelta] = new GRBLinExpr();
						delta[countDelta].addTerm(1, y[(int)element2]);
						//System.out.println(y[(int)element2].get(GRB.StringAttr.VarName));
						delta[countDelta].addTerm(-1, x.get(element).get(element2));
						//System.out.println("-"+ x.get(element).get(element2).get(GRB.StringAttr.VarName));

						model.addConstr(delta[countDelta], GRB.GREATER_EQUAL, 0, "delta"+(countDelta+1)); // for every existing x_s^k variable there is one inequality y_s - x_s^k >= 0
						countDelta++;
					}
			}
			//System.out.println(countDelta);
			Enumeration enu3 = x.keys();
			int countEpsilon = 0;
			while (enu3.hasMoreElements()) { // iterate over commodities
					Object element = enu3.nextElement();
					Enumeration enu32 = x.get(element).keys(); 
					while(enu32.hasMoreElements()){ // iterate over services
						Object element2 = enu32.nextElement();
						epsilon[countEpsilon] = new GRBLinExpr();
						epsilon[countEpsilon].addTerm(1, x.get(element).get(element2)); // for every existing x_s^k variable there is one inequality
						//System.out.println(x.get(element).get(element2).get(GRB.StringAttr.VarName));
						Enumeration pathEnu = paths.keys();
						while(pathEnu.hasMoreElements()){ //iterate over all paths
							Object element3 = pathEnu.nextElement();
							// create an array list dummy as a copy of paths.get(element3) and change the 0th element, such that it doesnt equal element2. (otherwise the next if statement might be true for wrong reasons)
							ArrayList<Double> dummy = new ArrayList<Double>();
							for (int i = 0; i < paths.get(element3).size() ; i++) {
								if(i ==0){
									
									dummy.add(-1.);
									
								}else{
										dummy.add(commodityMap.get(paths.get(element3).get(i)));
									 }
								
								
							}
							
							if ( dummy.contains(element2) && commodityMap.get(paths.get(element3).get(0)).equals(element)) { // if current service is on the current path and the current path belongs to the current commodity: add - z_p^k to the inequality
								epsilon[countEpsilon].addTerm(-1, z[(int)element3]);
								//System.out.println("-"+z[(int)element3].get(GRB.StringAttr.VarName));
							}
							
							
						}
						model.addConstr(epsilon[countEpsilon], GRB.GREATER_EQUAL, 0, "epsilon"+(countEpsilon+1));
						countEpsilon++;
					}
			}
					
					
					
			
			for (int l = 0; l < commodities.size(); l++) { // iterate over all commodities
				
				Enumeration pathEnu = paths.keys();
				zeta[l] = new GRBLinExpr();
				while(pathEnu.hasMoreElements()){ //iterate over all paths
					Object element = pathEnu.nextElement();
					if(commodityMap.get(paths.get(element).get(0)) == (double)l){
						zeta[l].addTerm(1, z[(int)element]); // if path belongs to current commodity, add it to the inequality 
						System.out.println(z[(int)element].get(GRB.StringAttr.VarName));
					}
				}
				System.out.println(l+"stop");
				model.addConstr(zeta[l], GRB.GREATER_EQUAL, 1., "zeta"+(l+1));
				
			}
			
			
			
			
			// optimize model
			
			model.optimize();
			
			//print result
			
			for (int i = 0; i < y.length; i++) {
				System.out.println(y[i].get(GRB.StringAttr.VarName)+" "+y[i].get(GRB.DoubleAttr.X));
			}
			Enumeration en = x.keys();
			while (en.hasMoreElements()) { // enumerate over commodities
					Object element = en.nextElement();
					Enumeration en2 = x.get(element).keys(); 
					while(en2.hasMoreElements()){ // enumerate over services
						Object element2 = en2.nextElement();
						System.out.println(x.get(element).get(element2).get(GRB.StringAttr.VarName)+" "+x.get(element).get(element2).get(GRB.DoubleAttr.X));
					}
			}
			for (int k = 0; k < z.length; k++) {
				System.out.println(z[k].get(GRB.StringAttr.VarName)+" "+z[k].get(GRB.DoubleAttr.X));
			}	
			
			
			// Dispose of model and environment
			
			model.dispose();
			env.dispose();
			
		}catch (GRBException e){
			System.out.println("Error code: " + e.getErrorCode() + "." + e.getMessage());
		}
		
	}	
}


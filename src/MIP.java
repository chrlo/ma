import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
public class MIP {
	
	public static void main(String[] args) {
		try{
			final long timeStart = System.currentTimeMillis();
			GRBEnv env = new GRBEnv("mip.log");
			GRBModel model = new GRBModel(env);
			
			//Read input 
			Instance instance = new Instance();
			Reader reader = new Reader();			
			instance = reader.read(instance);
			System.out.println("FINISHED READING");
			System.out.println((System.currentTimeMillis()-timeStart));
			
			//preprocessing
			Preprocessing1 prepro1 = new Preprocessing1();
			instance = prepro1.preprocess(instance);
			System.out.println("FINISHED PREPROCESSING1");
			System.out.println((System.currentTimeMillis()-timeStart));
			Preprocessing2 prepro2 = new Preprocessing2();
			instance = prepro2.preprocess(instance);
			System.out.println("FINISHED PREPROCESSING2");
			System.out.println((System.currentTimeMillis()-timeStart));
			
			
			
			
			//create capacity and weight vector
			Hashtable<Integer, Double[]> commodities = instance.getCommodities();  // first int= origin, second= destination, third= weight
			Hashtable<Integer,Double[]> services = instance.getServices(); // first double= capacity, second= fixed cost, third= variable cost coefficient
			Hashtable<Integer, ArrayList<String>> paths = instance.getPaths(); // first element on list= commodity, rest= services on path
			Hashtable<String,Double> commodityMap = instance.getCommodityMap(); // maps commodity names to a unique number
			Hashtable <String,Integer> serviceMap = instance.getServiceMap();
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
			
			Hashtable<Integer, GRBVar> y = new Hashtable<Integer, GRBVar>();
			Enumeration yEnu = services.keys();
			while (yEnu.hasMoreElements()) {
				Object element = yEnu.nextElement();
				GRBVar var = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "y"+((int)element));
				y.put((int)element, var);
			}
			
//			Enumeration yEnume = serviceMap.keys();
//			while (yEnume.hasMoreElements()) {
//				Object element = yEnume.nextElement();
//				System.out.println("Service: "+element+" | Variable: y :"+serviceMap.get(element));
//				
//			}
			
			Hashtable<Double,Hashtable<Double,GRBVar>> x = new Hashtable<Double,Hashtable<Double,GRBVar>>(); // key= commodities, element= hashtable of services, matching service number to variable
			Hashtable<Integer, GRBVar> z = new Hashtable<Integer, GRBVar>(); //one variable for each path
			Enumeration zEnu = paths.keys();
			while (zEnu.hasMoreElements()) {
				Object element = zEnu.nextElement();
				GRBVar var;
//				if((double)(int)element == 1.0||(double)(int)element == 14.0||(double)(int)element == 21.0||(double)(int)element == 4.0||(double)(int)element == 17.0||(double)(int)element == 12.0){
//					var = model.addVar(1.0, GRB.INFINITY, 0.0, GRB.INTEGER, "z"+((int) element ));
//				}else{
					var = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "z"+((int) element ));
//				}
				z.put((int) element, var);
			}
			//
//			Enumeration zEnume = paths.keys();
//			while (zEnume.hasMoreElements()) {
//				Object element = zEnume.nextElement();
//				System.out.println("Path: " + paths.get(element).toString() + " | Variable: z :" + (int) element);
//				
//			}
			
//			z[0]  = model.addVar(0.0, 0.0, 0.0, GRB.BINARY, "z"+(1));
//			z[1]  = model.addVar(0.0, 0.0, 0.0, GRB.BINARY, "z"+(2));
//			z[2]  = model.addVar(1.0, 1.0, 0.0, GRB.BINARY, "z"+(3));
//			z[3]  = model.addVar(1.0, 1.0, 0.0, GRB.BINARY, "z"+(4));
//			z[4]  = model.addVar(1.0, 1.0, 0.0, GRB.BINARY, "z"+(5));
//			z[5]  = model.addVar(0.0, 0.0, 0.0, GRB.BINARY, "z"+(6));
			Enumeration xEnu = paths.keys();
			while (xEnu.hasMoreElements()) { // every service on a path may create an x variable, as every path is uniquely assigned to a commodity (one service might be in multiple paths for the same commodity)
				Object element = xEnu.nextElement();
				
				if(x.get(commodityMap.get(paths.get(element).get(0))) == null){ //create new hashmap for a commodity if commodity appears for the first time
					Hashtable<Double,GRBVar> hash = new Hashtable<Double,GRBVar>();
					x.put(commodityMap.get(paths.get(element).get(0)), hash);
				}
				for (int m = 1; m < paths.get(element).size(); m++){
					if(!x.get(commodityMap.get(paths.get(element).get(0))).containsKey((double)serviceMap.get(paths.get(element).get(m)))){// if variable doesnt already exist, create it
						GRBVar var = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x"+(commodityMap.get(paths.get(element).get(0)))+","+(serviceMap.get(paths.get(element).get(m))));;
						x.get(commodityMap.get(paths.get(element).get(0))).put((double) serviceMap.get(paths.get(element).get(m)), var);
//						
					}
				}
				
			}	
			
//			Enumeration xEnume = x.keys();
//			while (xEnume.hasMoreElements()) {
//				Object element = xEnume.nextElement();
//				Enumeration xEnume2 = x.get(element).keys();
//				while(xEnume2.hasMoreElements()){
//					Object element2 = xEnume2.nextElement();
//					System.out.println("Commodity: " + paths.get((int) Math.round((double)element)).toString() + " | Service: " + " | Variable: x :" + (int) Math.round((double)element) + ";" + element2);
//							
//				}
//			}
//			
			model.update();
			System.out.println("FINISHED CREATING VARIABLES");
			System.out.println((System.currentTimeMillis()-timeStart));
			
			//set objective
			GRBLinExpr expr = new GRBLinExpr();
			Enumeration serviceEnu = services.keys();
			int serviceCount = 0;
			while (serviceEnu.hasMoreElements()) {
				Object element = serviceEnu.nextElement();
				
					expr.addTerm(services.get(element)[1], y.get(element)); // fixed cost times y_i
					serviceCount++;
//					System.out.println(services.get(element)[1]+","+y.get(element).get(GRB.StringAttr.VarName));
			
			}
			Enumeration e = x.keys();
			int countX = 0; //count the number of elements in x (x-variables)
			while(e.hasMoreElements()) {
				Object element = e.nextElement(); // Commodity
				Enumeration f = x.get(element).keys();
				while(f.hasMoreElements()) {
					countX++;
					
					Object element2 = f.nextElement(); // Service
					
					expr.addTerm((services.get((int)Math.round((double)element2))[2]*commodities.get((int)Math.round((double)element))[2]), x.get(element).get(element2)); //variable costs times weight times x_s^k
//					System.out.println(services.get((int)Math.round((double)element2))[2]*commodities.get((int)Math.round((double)element))[2]+","+x.get(element).get(element2).get(GRB.StringAttr.VarName));
				}
			}
			
				
			model.setObjective(expr,GRB.MINIMIZE);
			
			System.out.println("FINISHED SETTING OBJECTIVE");
			System.out.println((System.currentTimeMillis()-timeStart));
			//Add constraints
			
			Hashtable<Integer, GRBLinExpr> beta = new Hashtable<Integer, GRBLinExpr>();
			Hashtable<Integer, GRBLinExpr> delta = new Hashtable<Integer, GRBLinExpr>();
			Hashtable<Integer, GRBLinExpr> epsilon = new Hashtable<Integer, GRBLinExpr>();
			Hashtable<Integer, GRBLinExpr> zeta = new Hashtable<Integer, GRBLinExpr>();
//			GRBLinExpr[] beta;
//			beta = new GRBLinExpr[y.size()];
//			GRBLinExpr[] delta;
//			delta = new GRBLinExpr[countX];
//			GRBLinExpr[] epsilon;
//			epsilon = new GRBLinExpr[countX];
//			GRBLinExpr[] zeta;
//			zeta = new GRBLinExpr[commodities.size()];
			
			Enumeration betaEnu = y.keys();
			while (betaEnu.hasMoreElements()) {
					Object i = betaEnu.nextElement();
					beta.put((int) i, new GRBLinExpr());
					beta.get((int) i).addTerm(services.get((int)i)[0], y.get((int) i)); //u_w(s)y_s
//					System.out.println(services.get(i)[0]+""+y.get(i).get(GRB.StringAttr.VarName));
					Enumeration enu = x.keys(); 
					while(enu.hasMoreElements()){ //iterate over commodities
						Object element = enu.nextElement();
						if (x.get(element).get((double)(int)i) != null) {// x_s^{k} exists: s could be used for k and the relevant term is added 
							beta.get((int) i).addTerm(-(commodities.get((int)Math.round((double)element))[2]), x.get(element).get((double)(int)i)); //-d_w(k)x_s^k
//							System.out.println(-(commodities.get((int)Math.round((double)element))[2])+","+ x.get(element).get((double)(int)i).get(GRB.StringAttr.VarName));
						}
						
					}
//					System.out.println("STOP");
					model.addConstr(beta.get((int) i), GRB.GREATER_EQUAL, 0, "beta"+((int) i + 1));
	
			}
			
			System.out.println("FINISHED CREATING VARIABLES BETA");
			System.out.println((System.currentTimeMillis()-timeStart));
			
			Enumeration enu2 = x.keys();
			int countDelta = 0; 
			while (enu2.hasMoreElements()) { // enumerate over commodities
					Object element = enu2.nextElement();
					Enumeration enu22 = x.get(element).keys(); 
					while(enu22.hasMoreElements()){ // enumerate over services
						Object element2 = enu22.nextElement();
						delta.put(countDelta, new GRBLinExpr()); 
						delta.get(countDelta).addTerm(1, y.get((int) Math.round((double)element2)));
//						System.out.println(y.get((int) Math.round((double)element2)).get(GRB.StringAttr.VarName));
						delta.get(countDelta).addTerm(-1, x.get(element).get(element2));
//						System.out.println("-"+ x.get(element).get(element2).get(GRB.StringAttr.VarName));

						model.addConstr(delta.get(countDelta), GRB.GREATER_EQUAL, 0, "delta"+(countDelta+1)); // for every existing x_s^k variable there is one inequality y_s - x_s^k >= 0
						countDelta++;
					}
			}
			System.out.println("FINISHED CREATING VARIABLES DELTA");
			System.out.println((System.currentTimeMillis()-timeStart));
			//System.out.println(countDelta);
			Enumeration enu3 = x.keys();
			int countEpsilon = 0;
			while (enu3.hasMoreElements()) { // iterate over commodities
					Object element = enu3.nextElement();
					Enumeration enu32 = x.get(element).keys(); 
					while(enu32.hasMoreElements()){ // iterate over services
						Object element2 = enu32.nextElement();
						epsilon.put(countEpsilon, new GRBLinExpr());
						epsilon.get(countEpsilon).addTerm(1, x.get(element).get(element2)); // for every existing x_s^k variable there is one inequality
//						System.out.println(x.get(element).get(element2).get(GRB.StringAttr.VarName));
						Enumeration pathEnu = paths.keys();
						while(pathEnu.hasMoreElements()){ //iterate over all paths
							Object element3 = pathEnu.nextElement();
							// create an array list dummy as a copy of paths.get(element3) and change the 0th element, such that it doesnt equal element2. (otherwise the next if statement might be true for wrong reasons)
							ArrayList<Double> dummy = new ArrayList<Double>();
							for (int i = 0; i < paths.get(element3).size() ; i++) {
								if(i ==0){
									
									dummy.add(-1.);
									
								}else{
										dummy.add((double) serviceMap.get(paths.get(element3).get(i)));
									 }
								
								
							}
							
							if ( dummy.contains(element2) && commodityMap.get(paths.get(element3).get(0)).equals(element)) { // if current service is on the current path and the current path belongs to the current commodity: add - z_p^k to the inequality
								epsilon.get(countEpsilon).addTerm(-1, z.get((int)element3));
//								System.out.println("-"+z.get((int)element3).get(GRB.StringAttr.VarName));
							}
							
							
						}
//						System.out.println("STOP");
						model.addConstr(epsilon.get(countEpsilon), GRB.GREATER_EQUAL, 0, "epsilon"+(countEpsilon+1));
						countEpsilon++;
					}
			}
					
					
			System.out.println("FINISHED CREATING VARIABLES EPSILON");	
			System.out.println((System.currentTimeMillis()-timeStart));
			
			Enumeration zetaEnu = commodities.keys();
			
			while (zetaEnu.hasMoreElements()) { // iterate over all commodities
				Object l = zetaEnu.nextElement();
				Enumeration pathEnu = paths.keys();
				zeta.put((int) l, new GRBLinExpr());
				while(pathEnu.hasMoreElements()){ //iterate over all paths
					Object element = pathEnu.nextElement();
					if(commodityMap.get(paths.get(element).get(0)) == (double)(int)l){
						zeta.get((int) l).addTerm(1, z.get((int)element)); // if path belongs to current commodity, add it to the inequality 
//						System.out.println(z.get((int)element).get(GRB.StringAttr.VarName));
					}
				}
//				System.out.println("STOP");
				model.addConstr(zeta.get((int)l), GRB.GREATER_EQUAL, 1., "zeta"+((int) l+1));
				
			}
			
			
			System.out.println("FINISHED ADDING CONSTRAINTS");
			System.out.println((System.currentTimeMillis()-timeStart));
			
			// optimize model
			
			model.optimize();
			
			//print result
			
			Enumeration yPrint = y.keys();
			while (yPrint.hasMoreElements()) {
				Object i = yPrint.nextElement();
				if(y.get((int) i).get(GRB.DoubleAttr.X)!=0){
					System.out.println(y.get((int) i).get(GRB.StringAttr.VarName)+" "+y.get((int) i).get(GRB.DoubleAttr.X));
				}
				
			}
			Enumeration en = x.keys();
			while (en.hasMoreElements()) { // enumerate over commodities
					Object element = en.nextElement();
					Enumeration en2 = x.get(element).keys(); 
					while(en2.hasMoreElements()){ // enumerate over services
						Object element2 = en2.nextElement();
						if(x.get(element).get(element2).get(GRB.DoubleAttr.X)!=0.){
							System.out.println(x.get(element).get(element2).get(GRB.StringAttr.VarName)+" "+x.get(element).get(element2).get(GRB.DoubleAttr.X));
						}
						
					}
			}
			
			Enumeration zPrint = z.keys();
			while (zPrint.hasMoreElements()) {
				Object k = zPrint.nextElement();
				if(z.get((int) k).get(GRB.DoubleAttr.X)!=0.){
					System.out.println(z.get((int) k).get(GRB.StringAttr.VarName)+" "+z.get((int) k).get(GRB.DoubleAttr.X));
				}
				
			}	
			
			System.out.println((System.currentTimeMillis()-timeStart));
			// Dispose of model and environment
			
			model.dispose();
			env.dispose();
			
		}catch (GRBException e){
			System.out.println("Error code: " + e.getErrorCode() + "." + e.getMessage());
		}
		
	}	
}


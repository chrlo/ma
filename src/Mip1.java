import java.lang.reflect.Array;

import gurobi.*;
public class Mip1 {
	
	public static void main(String[] args) {
		try{
			GRBEnv env = new GRBEnv("mip.log");
			GRBModel model = new GRBModel(env);
			
			//create capacity and weight vector
			Double[] capacities;
			Double[] fixWeights;
			Double[] variableWeights;
			
			capacities = new Double[10];
			fixWeights = new Double[10];
			variableWeights = new Double[12];
			
			for(int i = 0; i < 10; i++){
				if(i==6){
					capacities[i] = 2.0;
				}else{
					capacities[i] = 1.0;
				}
				
			}
			for(int j = 0; j < 12; j++){
				if(j==0 || j==1 || j==2 || j==7 || j==8 || j==9){
					variableWeights[j] = 1.0;
				}else{
					variableWeights[j] = 0.0;
				}
				
			}
			for(int j = 0; j < 10; j++){
				if(j==0 || j==1 || j==2 || j==7 || j==8 || j==9){
					fixWeights[j] = 0.0;
				}else if(j==3 || j==4){
					fixWeights[j] = 4.0;
				}else if(j==5){
					fixWeights[j] = 3.0;
				}else{
					fixWeights[j] = 2.0;
				}
				
			}
			//create variables
			GRBVar[] s;
			s = new GRBVar[10];
			for (int i = 0; i < s.length; i++) {
				
				s[i]  = model.addVar(0.0, 4.0, 0.0, GRB.INTEGER, "y"+(i+1));
			}
			GRBVar[] x;
			x = new GRBVar[12];
			for (int j = 0; j < x.length; j++) {
				
				x[j]  = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x"+(j+1));
			}
			GRBVar[] z;
			z = new GRBVar[6];
			for (int k = 0; k < z.length; k++) {
				
				z[k]  = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z"+(k+1));
			}
			
			//integrate variables
			model.update();
			
			//set objective
			GRBLinExpr expr = new GRBLinExpr();
			for (int i = 0; i < s.length; i++) {
				
				if(i==0 || i==1 || i==2 || i==7 || i==8  || i==9){
					
					expr.addTerm(0, s[i]);
					
				}else if(i!=6 && i!=5){
					
					expr.addTerm(4, s[i]);
					
				}else if(i==5){
					
					expr.addTerm(3, s[i]);
					
				}else{
					expr.addTerm(2, s[i]);
				}
				
			}
			for (int j = 0; j < x.length; j++) {
				
				if(j==3 ||  j==4 || j==5 || j==6 || j==10 || j==11){
					
					expr.addTerm(0, x[j]);
					
				}else{
					
					expr.addTerm(1, x[j]);
					
				}
			}
			
			for (int k = 0; k < z.length; k++) {
		
						expr.addTerm(0, z[k]);
				
			}
				
			model.setObjective(expr,GRB.MINIMIZE);
			
			//Add constraints
			
			GRBLinExpr[] beta;
			beta = new GRBLinExpr[s.length];
			GRBLinExpr[] delta;
			delta = new GRBLinExpr[12];
			GRBLinExpr[] epsilon;
			epsilon = new GRBLinExpr[12];
			GRBLinExpr[] zeta;
			zeta = new GRBLinExpr[3];
			
			for (int i = 0; i < s.length; i++) {
				if(i!=6){
					beta[i] = new GRBLinExpr();
					beta[i].addTerm(capacities[i], s[i]);
					beta[i].addTerm(-1, x[i]);
					model.addConstr(beta[i], GRB.GREATER_EQUAL, 0, "beta"+(i+1));
				}else{	
					beta[i] = new GRBLinExpr();
					beta[i].addTerm(capacities[i], s[i]);
					beta[i].addTerm(-1, x[i]);
					beta[i].addTerm(-1, x[10]);
					beta[i].addTerm(-1, x[11]);
					model.addConstr(beta[i], GRB.GREATER_EQUAL, 0, "beta"+(i+1));
				}
				
			}
			
			for (int j = 0; j < 12; j++) {
				if(j!=10 && j!=11){
					delta[j] = new GRBLinExpr();
					delta[j].addTerm(-1, x[j]);
					delta[j].addTerm(1, s[j]);
					model.addConstr(delta[j], GRB.GREATER_EQUAL, 0, "delta"+(j+1));
				}else{		
					delta[j] = new GRBLinExpr();
					delta[j].addTerm(1, s[6]);
					delta[j].addTerm(-1, x[j]);
					model.addConstr(delta[j], GRB.GREATER_EQUAL, 0, "delta"+(j+1));
				}
				
			}
			
			for (int k = 0; k < 12; k++) {
				
				if(k==0 || k==6 || k==7){
					epsilon[k] = new GRBLinExpr();
					epsilon[k].addTerm(1, x[k]);
					epsilon[k].addTerm(-1, z[5]);
					model.addConstr(epsilon[k], GRB.GREATER_EQUAL, 0, "epsilon"+(k+1));
				}else if(k==1 || k==10 || k==8){
					epsilon[k] = new GRBLinExpr();
					epsilon[k].addTerm(1, x[k]);
					epsilon[k].addTerm(-1, z[4]);
					model.addConstr(epsilon[k], GRB.GREATER_EQUAL, 0, "epsilon"+(k+1));
				}else if(k==2 || k==11 || k==9){
					epsilon[k] = new GRBLinExpr();
					epsilon[k].addTerm(1, x[k]);
					epsilon[k].addTerm(-1, z[3]);
					model.addConstr(epsilon[k], GRB.GREATER_EQUAL, 0, "epsilon"+(k+1));
				}else{
					epsilon[k] = new GRBLinExpr();
					epsilon[k].addTerm(1, x[k]);
					epsilon[k].addTerm(-1, z[k-3]);
					model.addConstr(epsilon[k], GRB.GREATER_EQUAL, 0, "epsilon"+(k+1));
				}
			}
			
			for (int l = 0; l < 3; l++) {
					zeta[l] = new GRBLinExpr();
					zeta[l].addTerm(1, z[l]);
					zeta[l].addTerm(1, z[l+3]);
					model.addConstr(zeta[l], GRB.GREATER_EQUAL, 1, "zeta"+(l+1));
				
			}
			
			// optimize model
			model.optimize();
			for (int i = 0; i < s.length; i++) {
				System.out.println(s[i].get(GRB.StringAttr.VarName)+" "+s[i].get(GRB.DoubleAttr.X));
			}
			for (int j = 0; j < 12; j++) {
				System.out.println(x[j].get(GRB.StringAttr.VarName)+" "+x[j].get(GRB.DoubleAttr.X));
			}
			for (int k = 0; k < 6; k++) {
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

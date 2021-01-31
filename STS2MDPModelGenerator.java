package demos;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import demos.MDPModelGenerator.GridModel;
import parser.State;
import parser.VarList;
import parser.ast.Declaration;
import parser.ast.DeclarationBool;
import parser.ast.DeclarationInt;
import parser.ast.DeclarationType;
import parser.ast.Expression;
import parser.type.Type;
import parser.type.TypeBool;
import parser.type.TypeInt;
import prism.DefaultModelGenerator;
import prism.ModelGenerator;
import prism.ModelType;
import prism.Prism;
import prism.PrismDevNullLog;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;

public class STS2MDPModelGenerator {
	static double w = 2.718;
	final static int randomSampleVaueGen = 3;
	public static void main(String[] args) throws IOException
	{
		Instant start = Instant.now();
		  
		new STS2MDPModelGenerator().run();
		
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");
	}
	public void run() throws FileNotFoundException
	{
		try {
			// Create a log for PRISM output (hidden or stdout)	
			PrismLog mainLog = new PrismFileLog("stdout");

			// Initialise PRISM engine 
			Prism prism = new Prism(mainLog);
			prism.initialise();
			
			           // Create a model generator to specify the model that PRISM should build
			           // Maximum value for mechselected =4, maxPPE =600 and maxPollution =400
						
						MDPModel modelGen = new MDPModel(4,600,400);
						
						// Load the model generator into PRISM,
						// export the model to a dot file (which triggers its construction)
						prism.loadModelGenerator(modelGen);
						prism.exportTransToFile(true, Prism.EXPORT_DOT_STATES, new File("mdp.dot"));
						//Export state transition matrix, states and labels 
						 prism.exportTransToFile(true,Prism.EXPORT_PLAIN, new File("States.tra"));
						// prism.exportTransToFile(true,Prism.EXPORT_ROWS, new File("StateTransitionRowWise.tra"));
						 prism.exportStatesToFile(Prism.EXPORT_PLAIN, new File("States.sta"));
						 prism.exportLabelsToFile(null, Prism.EXPORT_PLAIN, new File("States.lab"));
						// Close down PRISM
						prism.closeDown();
			
		} catch (FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}catch (PrismException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}
	
	class MDPModel implements ModelGenerator
	{
		private int selected_mechanism;
		private int maxselected_mechanism;
		private int PPE;
		private int maxPPE;
		private int maxPollution;
		private int minPollution;
		private int pollution;
		private State exploreState;
		
				private String actions[] = { "mSelection", "manufacture_100", "manufacture_200","manufacture_300","manufacture_400" };
				/***
				 * 
				 * Extract Number of mechanisms/ actions are in our STS specification
				 * 
				 */
				private int numOfMech=(actions.length-1);
				
				
				public MDPModel(int maxselected_mechanism,int maxPPE, int maxPollution )
				{
					this.maxselected_mechanism=maxselected_mechanism;
					this.maxPPE = maxPPE;
					this.maxPollution = maxPollution;
					
				}

		@Override
		public ModelType getModelType() {
			return ModelType.MDP;
		}

		@Override
		public List<String> getVarNames() {
			return Arrays.asList("selected_mechanism","PPE", "pollution");
		}

		@Override
		public List<Type> getVarTypes() {
			return Arrays.asList(TypeInt.getInstance(), TypeInt.getInstance(),TypeInt.getInstance());
		}

		@Override
		public State getInitialState() throws PrismException {
			// Initialize (mechselected, PPE, pollution) =(0,10,0)
			return new State(3).setValue(0, 0).setValue(1, 10).setValue(2, 0);
		}
		@Override
		public DeclarationType getVarDeclarationType(int i) throws PrismException
		{
			 Type type = getVarType(i);
			switch (i) {
			// selected_mechanism
			case 0:
				return new DeclarationInt(Expression.Int(0), Expression.Int(4));
				// PPE range
			case 1:
				return new DeclarationInt(Expression.Int(0), Expression.Int(1000));
				// pollution range
			case 2:
				return new DeclarationInt(Expression.Int(0), Expression.Int(700));	
			
			default:
				throw new PrismException("No default declaration avaiable for type " + type);
			}
		}
		@Override
	public List<String> getLabelNames()
		{
			return Arrays.asList("PPE","pollution");
		}
		
		
		@Override
		public void exploreState(State exploreState) throws PrismException {
			// Store the state (for reference, and because will clone/copy it later)
						this.exploreState = exploreState;
						selected_mechanism = ((Integer) exploreState.varValues[0]).intValue();
						PPE = ((Integer) exploreState.varValues[1]).intValue();	
						pollution = ((Integer) exploreState.varValues[2]).intValue();
						
						
			
		}

		@Override
		public int getNumChoices() throws PrismException {
			
			int totalActions = actions.length;
			//System.out.println("Number of choice "+totalActions );
			return totalActions;
	
		}

		@Override
		public int getNumTransitions(int i) throws PrismException {
			/*
			 * 
			 * Number of transitions for action m-selection dependences on the number of actions are available in our MAS
			 */
			
			if(i==0) {
				
				return numOfMech;
			} else {
				
				return randomSampleVaueGen;
			}
			
			
		}

		@Override
		/**
		 * Get the action label of a transition within a choice, specified by its index/offset.
		 * The label can be any Object, but will often be treated as a string, so it should at least
		 * have a meaningful toString() method implemented. Absence of an action label is denoted by null.
		 * Note: For most types of models, the action label will be the same for all transitions within
		 * the same nondeterministic choice (i.e. for each different value of {@code offset}),
		 * but for Markov chains this may not necessarily be the case.
		 * @param i Index of the nondeterministic choice
		 * @param offset Index of the transition within the choice */
		
		public Object getTransitionAction(int i, int offset) throws PrismException {
			 /*

           selected_mechanism is decision variable, if selected_mechanism is 0 in current expolred state is 0 then mSelection mechanism will be executed;
            Otherwise any mechanism from MAS will be executed based onselected_mechanism value such as if selected_mechanism =2 then manufacture_200 will be executed.


        */
		        
		        switch(i) {
		        case 0:
		        	if(selected_mechanism==0) {
		        		//System.out.println("Transit action "+actions[i] );
		        		return actions[i];
		        	}
		        	break;
		        	
		        case 1:
		        	if(selected_mechanism==1) {
		        		//System.out.println("Transit action "+actions[i] );
		        		return actions[i];
		        	}
		        	break;
		        case 2:	
		        	if(selected_mechanism==2) {
		        		//System.out.println("Transit action "+actions[i] );
		        		return actions[i];
		        	}
		        	break;
		        case 3:	
		        	if(selected_mechanism==3) {
		        		//System.out.println("Transit action "+actions[i] );
		        		return actions[i];
		        	}
		        	break;
		        case 4:	
		        	if(selected_mechanism==4) {
		        		//System.out.println("Transit action "+actions[i] );
		        		return actions[i];
		        	}
		        	break;
		        }
				return null;		
		}
		
		@Override
		public double getTransitionProbability(int i, int offset) throws PrismException {
			Map<String, Map<String, String>> norm = new HashMap<String, Map<String, String>>();
			Map<String, String> normPreconditions = new HashMap<String,String>();
			/*  
            Reading norms 
            here we calculate the mechanism satisfaction probability towards set of norms based on current state.

			*/
			NormsReading nr = new NormsReading();
			norm =	nr.getNormsData();
			normPreconditions = nr.getNormsPreconditions();
			double Proba =0.0;
			// Reading the mechanism execution probability
			ReadingStateTranProb rstp = new ReadingStateTranProb();
			String actionName = actions[i];
			String tranProb = null;
			
			switch(i) {
			case 0: // mSelection
				/*

                getMechLiklihoodValue() function is used to calculate the probability distribution for each mechanism in MAS
                This function is implementation of equation 1 in the paper.

				*/
				Map<String, Double> finalProbMech = getMechLiklihoodValue(norm,normPreconditions);
				
				 Map<String, Double> MechLiklihood = new HashMap<String, Double>();
				 MechLiklihoodComputation mlc = new MechLiklihoodComputation();
				 MechLiklihood = mlc.reNormalizedMechProb(finalProbMech);
				
				 double sum1 = MechLiklihood.values().stream().collect(Collectors.summingDouble(Double::doubleValue));
					sum1 = Double.parseDouble(new DecimalFormat("##.##").format(sum1));;
					double diffsum = (1.0-sum1);
					diffsum = Double.parseDouble(new DecimalFormat("##.##").format(diffsum));;
					List<String> keyList = new ArrayList<String>(MechLiklihood.keySet());
					MechLiklihood.put(keyList.get(0), MechLiklihood.get(keyList.get(0)) + diffsum);
					double sum = MechLiklihood.values().stream().collect(Collectors.summingDouble(Double::doubleValue));
					sum = Double.parseDouble(new DecimalFormat("##.##").format(sum));
					if (sum == 1.0) {
						if(offset==0 && selected_mechanism==0) {
							String key = keyList.get(0);
							Double finalProb = MechLiklihood.get(key);
							
							return finalProb;
						}
						if(offset==1 && selected_mechanism==0) {
							String key = keyList.get(1);
							Double finalProb = MechLiklihood.get(key);
							
							return finalProb;
						}
						if(offset==2 && selected_mechanism==0) {
							String key = keyList.get(1);
							Double finalProb = MechLiklihood.get(key);
						
							return finalProb;
						}
						if(offset==3 && selected_mechanism==0) {
							String key = keyList.get(1);
							Double finalProb = MechLiklihood.get(key);
						
							return finalProb;
						}
						
					} else {
						System.out.println(
								"Please normalize mechanims's likihood value in such way that sum of all probabilties should be 1");
					}
				
			break;
			case 1: 
				if(offset==0 && selected_mechanism==1) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
								
					return Proba;
				}
				if(offset==1 && selected_mechanism==1) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
								
					return Proba;
				}
				if(offset==2 && selected_mechanism==1) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
							
					return Proba;
				}

				
			break;	
			case 2: 
				if(offset==0 && selected_mechanism==2) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
									
					return Proba;
				}
				if(offset==1 && selected_mechanism==2) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
									
					return Proba;
				}
				if(offset==2 && selected_mechanism==2) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
									
					return Proba;
				}

				
				
			break;
			case 3: 
				if(offset==0 && selected_mechanism==3) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);				
					return Proba;
				}
				if(offset==1 && selected_mechanism==3) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);				
					return Proba;
				}
				if(offset==2 && selected_mechanism==3) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
									
					return Proba;
				}
			
				
			break;
			case 4: 
				if(offset==0 && selected_mechanism==4) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
									
					return Proba;
				}
				if(offset==1 && selected_mechanism==4) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
								
					return Proba;
				}
				if(offset==2 && selected_mechanism==4) {
					try {
						tranProb= rstp.getTransitionProb(offset,actionName);
						 
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					Proba= Double.parseDouble(tranProb);
									
					return Proba;
				}
			
				
			break;
		
			}
			
			
			return Proba;
		}
		
		/*   
        The implementation of equation 1 in the paper

	   */
		private Map<String, Double> getMechLiklihoodValue(Map<String, Map<String, String>> norm,Map<String,  String> normPreconditions) {
			
			Map<String, Map<String, Double>> wValueOfMech = new HashMap<String, Map<String, Double>>();
			Map<String, Double> finalProbMech = new HashMap<String, Double>();
		
			for(int a=1; a<actions.length;a++) {
				
				Map<String, Double> normCorrW = new HashMap<String, Double>();
			
				try {
					File myMech = new File("D:\\prism-master\\data\\"+ actions[a]);
					Scanner myMechReader = new Scanner(myMech);
					while (myMechReader.hasNextLine()) {
						Double minRange = 0.0;
						Double maxRange = 0.0;
						Double singleRange = 0.0;
						String data = myMechReader.nextLine();
						
						 String[] label = data.split(":");
						 String varM = label[0].trim();
						
					     String rangeM = label[1].trim();
					     if (rangeM.contains("#")) {
								String[] minMax = rangeM.split("#");
								minRange = Double.parseDouble((minMax[0].trim()));
								maxRange = Double.parseDouble(minMax[1].trim());
								
							} else {

								singleRange = Double.parseDouble(rangeM.trim());
								
							}
					     
					     for (Entry<String, Map<String, String>> n : norm.entrySet()) {
								String normId = n.getKey(); // like P1 ,C1
								String normPre="";
								String preVar ="";
								Integer prevalue;
								if(normPreconditions.get(normId).contains(">=")) {
									
									String[] preExp = normPreconditions.get(normId).split(">=");
									 preVar = preExp[0].trim();
									 prevalue =Integer.parseInt(preExp[1].trim());
								}else {
									 normPre = normPreconditions.get(normId);
								}
								
								
								String normType = normId.replaceAll("[^A-Z]", "");
								
								/****
								 * 
								 * For Prohibition P
								 * 
								 */
								if ((normType.equalsIgnoreCase("P"))) {
									
										if (n.getValue().containsKey(varM)) {
											double prob = 0.0;
											String AtomName = varM;
										
											int nConsquentValue = Integer.parseInt(n.getValue().get(AtomName));
											
											double currentTarget = nConsquentValue - pollution;
											//System.out.println("currentTarget "+ currentTarget);
											//System.out.println("Upper bound "+ maxRange);
											//System.out.println("Lower bound "+ minRange);
											
											if (maxRange <= currentTarget) {
												
												prob = 1.0;
												
											} else if (minRange >= currentTarget) {
												
												prob = 0.0;
												

											} else {

												prob = Double.parseDouble(new DecimalFormat("##.##").format(
														(double) (currentTarget - minRange) / (maxRange - minRange)));	
												
											}
											
											
											 
											 double result = Double
														.parseDouble(new DecimalFormat("##.#").format(Math.pow(w, prob)));
												
												normCorrW.put(normId, result);
										}
									
									
								} else if(normType.equalsIgnoreCase("C")) {
									
									if (n.getValue().containsKey(varM)) {
										if((normPre.equalsIgnoreCase("true"))){
										
										double prob = 0.0;
										String AtomName = varM;
										
										int nConsquentValue = Integer.parseInt(n.getValue().get(AtomName));
										
										double currentTarget = nConsquentValue - PPE;
										//System.out.println("currentTarget "+ currentTarget);
										if (maxRange <= currentTarget) {
											prob = 0.0;
											
										} else if (minRange >= currentTarget) {

											prob = 1.0;

										} else {

											prob = Double.parseDouble(new DecimalFormat("##.##").format(
													(double) (maxRange -currentTarget ) / (maxRange - minRange)));
											
										}
										
										;
										 
										 double result = Double
													.parseDouble(new DecimalFormat("##.#").format(Math.pow(w, prob)));
									
											normCorrW.put(normId, result);
									}
									if((!(normPre.equalsIgnoreCase("true")))&&(pollution>=200)) { // C2 in revised MAS
										
										double prob = 0.0;
										String AtomName = varM;
										int nConsquentValue = Integer.parseInt(n.getValue().get(AtomName));
										double currentTarget = nConsquentValue - pollution;
										
										if (maxRange <= currentTarget) {
											prob = 0.0;
											
										} else if (minRange >= currentTarget) {

											prob = 1.0;

										} else {

											prob = Double.parseDouble(new DecimalFormat("##.##").format(
													(double) (maxRange -currentTarget ) / (maxRange - minRange)));
											
										}
										
										
										 
										 double result = Double
													.parseDouble(new DecimalFormat("##.#").format(Math.pow(w, prob)));
										
											normCorrW.put(normId, result);
									}
								}
								}
								
								  
							   }
					 
					   wValueOfMech.put((String) actions[a], normCorrW);
					}
					myMechReader.close();
				}catch (FileNotFoundException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
				
				
			}
			
			 for (Entry<String, Map<String, Double>> w : wValueOfMech.entrySet()) {
					double finalMProb = 1.0;
					Set<String> normKeys = w.getValue().keySet();
					String[] normkeysArray = normKeys.toArray(new String[normKeys.size()]);
					
					for (int f = 0; f < normkeysArray.length; f++) {
						
						double sumOfw = 0.0;
						for (Entry<String, Map<String, Double>> w1 : wValueOfMech.entrySet()) {
							
							sumOfw = sumOfw + w1.getValue().get(normkeysArray[f]);
						}

						
						double nProb = w.getValue().get(normkeysArray[f]) / sumOfw;
						

						finalMProb = Double.parseDouble(new DecimalFormat("##.##").format(finalMProb * nProb));

					}
					

					finalProbMech.put(w.getKey(), finalMProb);

				}
			return finalProbMech;
		}

		@Override
		/**
		 * Get the target (as a new State object) of a transition within a choice, specified by its index/offset.
		 * @param i Index of the nondeterministic choice
		 * @param offset Index of the transition within the choice
		 */
		public State computeTransitionTarget(int i, int offset) throws PrismException {
		
			State target = new State(exploreState);
			String actionName = actions[i];
			ReadingStateTransition rst = new ReadingStateTransition();
			String[] varValue = null;
			
			
			if((PPE<=maxPPE) && (pollution<=maxPollution)) {
				switch (i) {
				case 0:
					if((offset==0)&&(selected_mechanism==0)) {
						//System.out.println("current state "+ target);
						target.setValue(0, 1);
						//System.out.println("new state "+ target);
						return target;
						
					}
					if((offset==1)&&(selected_mechanism==0)) {
						//System.out.println("current state "+ target);
						target.setValue(0, 2);
						//System.out.println("new state "+ target);
						return target;
					}
					if((offset==2)&&(selected_mechanism==0)) {
						//System.out.println("current state "+ target);
						target.setValue(0, 3);
						//System.out.println("new state "+ target);
						return target;
					}
					if((offset==3)&&(selected_mechanism==0)) {
						//System.out.println("current state "+ target);
						target.setValue(0, 4);
						//System.out.println("new state "+ target);
						return target;
					}
				
					
					break;
				
				case 1:
					
					if((offset==0)&&(selected_mechanism==1)) {
						try {
                     	   varValue = rst.getTransitionValue(offset,actionName);
                        } catch(IOException e) {
                     	   e.printStackTrace();
                        }
						//System.out.println("current state"+ target);
						
						String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
						String[] parts = currentTarget.split(",",3);
						Integer varPPE = Integer.parseInt(parts[1].trim());
						Integer varpollution = Integer.parseInt(parts[2].trim());
						target.setValue(0, 0);
						target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
						target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
						//System.out.println("new state "+ target);
						return target;
					}
					if((offset==1)&&(selected_mechanism==1)) {
						try {
	                     	   varValue = rst.getTransitionValue(offset,actionName);
	                        } catch(IOException e) {
	                     	   e.printStackTrace();
	                        }
							//System.out.println("current state"+ target);
							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
							String[] parts = currentTarget.split(",",3);
							Integer varPPE = Integer.parseInt(parts[1].trim());
							Integer varpollution = Integer.parseInt(parts[2].trim());
							target.setValue(0, 0);
							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
							//System.out.println("new state "+ target);
							return target;
							
					}
					if((offset==2)&&(selected_mechanism==1)) {
						try {
	                     	   varValue = rst.getTransitionValue(offset,actionName);
	                        } catch(IOException e) {
	                     	   e.printStackTrace();
	                        }
							//System.out.println("current state"+ target);
							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
							String[] parts = currentTarget.split(",",3);
							Integer varPPE = Integer.parseInt(parts[1].trim());
							Integer varpollution = Integer.parseInt(parts[2].trim());
							target.setValue(0, 0);
							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
							//System.out.println("new state "+ target);
							return target;
							
					}

					
					break;
                  case 2:
					
					if((offset==0)&&(selected_mechanism==2)) {
						try {
	                     	   varValue = rst.getTransitionValue(offset,actionName);
	                        } catch(IOException e) {
	                     	   e.printStackTrace();
	                        }
							//System.out.println("current state"+ target);
							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
							String[] parts = currentTarget.split(",",3);
							Integer varPPE = Integer.parseInt(parts[1].trim());
							Integer varpollution = Integer.parseInt(parts[2].trim());
							target.setValue(0, 0);
							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
							//System.out.println("new state "+ target);
							return target;
					}
					if((offset==1)&&(selected_mechanism==2)) {
						try {
	                     	   varValue = rst.getTransitionValue(offset,actionName);
	                        } catch(IOException e) {
	                     	   e.printStackTrace();
	                        }
							//System.out.println("current state"+ target);
							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
							String[] parts = currentTarget.split(",",3);
							Integer varPPE = Integer.parseInt(parts[1].trim());
							Integer varpollution = Integer.parseInt(parts[2].trim());
							target.setValue(0, 0);
							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
							//System.out.println("new state "+ target);
							return target;
					}
					if((offset==2)&&(selected_mechanism==2)) {
						try {
	                     	   varValue = rst.getTransitionValue(offset,actionName);
	                        } catch(IOException e) {
	                     	   e.printStackTrace();
	                        }
							//System.out.println("current state"+ target);
							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
							String[] parts = currentTarget.split(",",3);
							Integer varPPE = Integer.parseInt(parts[1].trim());
							Integer varpollution = Integer.parseInt(parts[2].trim());
							target.setValue(0, 0);
							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
							//System.out.println("new state "+ target);
							return target;
					}

					
					break;
                  case 3:
  					
  					if((offset==0)&&(selected_mechanism==3)) {
  						try {
  	                     	   varValue = rst.getTransitionValue(offset,actionName);
  	                        } catch(IOException e) {
  	                     	   e.printStackTrace();
  	                        }
  							//System.out.println("current state"+ target);
  							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
  							String[] parts = currentTarget.split(",",3);
  							Integer varPPE = Integer.parseInt(parts[1].trim());
  							Integer varpollution = Integer.parseInt(parts[2].trim());
  							target.setValue(0, 0);
  							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
  							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
  							//System.out.println("new state "+ target);
  							return target;
  					}
  					if((offset==1)&&(selected_mechanism==3)) {
  						try {
  	                     	   varValue = rst.getTransitionValue(offset,actionName);
  	                        } catch(IOException e) {
  	                     	   e.printStackTrace();
  	                        }
  							//System.out.println("current state"+ target);
  							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
  							String[] parts = currentTarget.split(",",3);
  							Integer varPPE = Integer.parseInt(parts[1].trim());
  							Integer varpollution = Integer.parseInt(parts[2].trim());
  							target.setValue(0, 0);
  							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
  							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
  							//System.out.println("new state "+ target);
  							return target;
  					}
  					if((offset==2)&&(selected_mechanism==3)) {
  						try {
  	                     	   varValue = rst.getTransitionValue(offset,actionName);
  	                        } catch(IOException e) {
  	                     	   e.printStackTrace();
  	                        }
  							//System.out.println("current state"+ target);
  							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
  							String[] parts = currentTarget.split(",",3);
  							Integer varPPE = Integer.parseInt(parts[1].trim());
  							Integer varpollution = Integer.parseInt(parts[2].trim());
  							target.setValue(0, 0);
  							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
  							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
  							//System.out.println("new state "+ target);
  							return target;
  					}

  					break;
                  case 4:
  					
  					if((offset==0)&&(selected_mechanism==4)) {
  						try {
  	                     	   varValue = rst.getTransitionValue(offset,actionName);
  	                        } catch(IOException e) {
  	                     	   e.printStackTrace();
  	                        }
  							//System.out.println("current state"+ target);
  							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
  							String[] parts = currentTarget.split(",",3);
  							Integer varPPE = Integer.parseInt(parts[1].trim());
  							Integer varpollution = Integer.parseInt(parts[2].trim());
  							target.setValue(0, 0);
  							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
  							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
  							//System.out.println("new state "+ target);
  							return target;
  					}
  					if((offset==1)&&(selected_mechanism==4)) {
  						try {
  	                     	   varValue = rst.getTransitionValue(offset,actionName);
  	                        } catch(IOException e) {
  	                     	   e.printStackTrace();
  	                        }
  							//System.out.println("current state"+ target);
  							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
  							String[] parts = currentTarget.split(",",3);
  							Integer varPPE = Integer.parseInt(parts[1].trim());
  							Integer varpollution = Integer.parseInt(parts[2].trim());
  							target.setValue(0, 0);
  							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
  							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
  							//System.out.println("new state "+ target);
  							return target;
  					}
  					if((offset==2)&&(selected_mechanism==4)) {
  						try {
  	                     	   varValue = rst.getTransitionValue(offset,actionName);
  	                        } catch(IOException e) {
  	                     	   e.printStackTrace();
  	                        }
  							//System.out.println("current state"+ target);
  							String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)","");
  							String[] parts = currentTarget.split(",",3);
  							Integer varPPE = Integer.parseInt(parts[1].trim());
  							Integer varpollution = Integer.parseInt(parts[2].trim());
  							target.setValue(0, 0);
  							target.setValue(1, varPPE < maxPPE ? (PPE + Integer.parseInt(varValue[0])) : PPE);
  							target.setValue(2, varpollution < maxPollution ? (pollution + Integer.parseInt(varValue[1])) : pollution);
  							//System.out.println("new state "+ target);
  							return target;
  					}
 					
  					
  					break;
                  
					
				}	
			} 
			
			// Never happens
			//System.out.println("never happen "+ target);
			return target;
		}
		
		@Override
		public boolean isLabelTrue(int i) throws PrismException
		{
			switch (i) {
			// achievement
			case 0:
				
				return PPE>15;
			// maintain			
			case 1:
				
				return pollution<7;
			
			
			default:
				throw new PrismException("Label number \"" + i + "\" not defined");
			}
		}
		
	}
}

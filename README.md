# ReNO-Framework
Probabilistic Verification of multiagent system (MAS) in sociotechnical terms using PRISM model checker

***** Description about files ********

Input : Set of mechanisms
File Name : Mechanism
Input : Set of Norms
File Name : Norms
Input : Mechanism Execution Probabilities
Folder data : Conatins conseuqences of a each mechanism in Mechanism file.
Folder StateTranProb : Contains mechanism execution probabilities for each mechanism in Mechanism file.

***** Code Run Instruction *********


1. Build Model :

Our algorithm has been implemented on top of PRISM model checker (https://www.prismmodelchecker.org/).
Therefore, you will need to separately download a copy of PRISM to connect to run our implentation.
Then create a java package inside prism/src and upload our all java files.

Finally, Run STS2MDPModelGenerator.java to build the model.

As an Output:  You will get a model dot file mdp.dot to visulaize the model and also get three files states.sta, states.tra and states.lab as part of model constraction.
states.tra = Model Transition probabilities
states.sta = Model states
states.lab = Model labes

2. Model Checking

For requirement verification, access PRISM through command line version :

Such as : prism -importtrans states.tra -importstates states.sta -importlabels states.lab -mdp -pf "Pmax=? [F<=14 PPE>=150  ]"

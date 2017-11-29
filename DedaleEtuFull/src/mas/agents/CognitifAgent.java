package mas.agents;


import env.Environment;
import princ.Node;
import princ.BackpackInfo;
import mas.abstractAgent;
import mas.behaviours.*;
import jade.core.behaviours.FSMBehaviour;
import java.util.ArrayList;
import java.util.List;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class CognitifAgent extends abstractAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1784844593772918359L;
	private List<Node> noeudsVus;
	private int typeT; // 0 = unknown ; 1 = treasure ; 2 = diamonds
	//private Hashtable<String, BackpackInfo> bagInfos;
	private List<BackpackInfo> bagInfos;
	
	private static final String STATE_Strategy = "A";
	private static final String STATE_Hello = "B";
	private static final String STATE_Carte = "C";
	private static final String STATE_Hunt = "E";
	private static final String STATE_Interblocage = "F";

	
	private static final String STATE_HelloHunt = "G";
	private static final String STATE_CarteHunt = "H";

	private static final String STATE_HelloInter = "G1";
	private static final String STATE_CarteInter = "H1";
	
	private static final String STATE_HelloEnd = "G2";
	private static final String STATE_CarteEnd = "H2";
	
	private static final String STATE_END = "I";


	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */

	public void setCarte(List<Node> carte){
		noeudsVus = carte;
	}
	
	public void setTypeT(int typeT){
		this.typeT = typeT;
	}
	
	public int getTypeT(){
		return typeT;
	}
	
	protected void setup(){

		super.setup();

	    // Register the book-selling service in the yellow pages
	    DFAgentDescription dfd = new DFAgentDescription();
	    dfd.setName(getAID());
	    ServiceDescription sd = new ServiceDescription();
	    sd.setType("explorer");
	    sd.setName(getLocalName());
	    //sd.setName("JADE-fosyma-project");
	    dfd.addServices(sd);
	    try {
	      DFService.register(this, dfd);
	    }
	    catch (FIPAException fe) {
	      fe.printStackTrace();
	    }
		
		noeudsVus = new ArrayList<Node>();
		//bagInfos = new Hashtable<String, BackpackInfo>();
		bagInfos = new ArrayList<BackpackInfo>();
		typeT = 0;

		//get the parameters given into the object[]. In the current case, the environment where the agent will evolve
		final Object[] args = getArguments();
		if(args[0]!=null){

			deployAgent((Environment) args[0]);

		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}
		
		doWait(200);
		
		FSMBehaviour fsm = new FSMBehaviour(this) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -5093210948342805502L;

			public int onEnd() {
				System.out.println("FSM behaviour completed.");
				myAgent.doDelete();
				return super.onEnd();
			}
		};
		
		fsm.registerFirstState(new Strategy(this, noeudsVus, typeT), STATE_Strategy);
		
		fsm.registerState(new SayHello(this, noeudsVus, bagInfos), STATE_Hello);
		
		fsm.registerState(new SendMap(this, noeudsVus), STATE_Carte);
		
		//fsm.registerState(new TestBehaviour(this, noeudsVus), "E"); // A RETIRER

		fsm.registerState(new TreasureHunt(this, noeudsVus, typeT, bagInfos), STATE_Hunt);
		fsm.registerState(new Interblocage(this, noeudsVus), STATE_Interblocage);
		
		fsm.registerState(new SayHello(this, noeudsVus, bagInfos), STATE_HelloHunt);
		fsm.registerState(new SendMap(this, noeudsVus), STATE_CarteHunt);
		
		fsm.registerState(new SayHello(this, noeudsVus, bagInfos), STATE_HelloInter);
		fsm.registerState(new SendMap(this, noeudsVus), STATE_CarteInter);
		
		fsm.registerState(new SayHello(this, noeudsVus, bagInfos), STATE_HelloEnd);
		fsm.registerState(new SendMap(this, noeudsVus), STATE_CarteEnd);
		
		fsm.registerLastState(new EndBehaviour(this), STATE_END);
		

		fsm.registerTransition(STATE_Strategy, STATE_Hello, 0);
		fsm.registerTransition(STATE_Strategy, STATE_Carte, 1);		
		fsm.registerTransition(STATE_Strategy, STATE_Strategy, 2);
		fsm.registerTransition(STATE_Strategy, STATE_Hunt, 3);

		fsm.registerTransition(STATE_Hunt, STATE_Hunt, 4);
		fsm.registerTransition(STATE_Hunt, STATE_Interblocage, 5);
		fsm.registerTransition(STATE_Hunt, STATE_HelloHunt, 9);
		fsm.registerTransition(STATE_Hunt, STATE_CarteHunt, 10);
		fsm.registerTransition(STATE_Hunt, STATE_END, 8);
		
		fsm.registerTransition(STATE_Interblocage, STATE_Interblocage, 6);
		fsm.registerTransition(STATE_Interblocage, STATE_Hunt, 7);
		fsm.registerTransition(STATE_Interblocage, STATE_HelloInter, 11);
		fsm.registerTransition(STATE_Interblocage, STATE_CarteInter, 12);

		
		fsm.registerDefaultTransition(STATE_Hello, STATE_Strategy);
		fsm.registerDefaultTransition(STATE_Carte, STATE_Strategy);

		
		
		fsm.registerDefaultTransition(STATE_HelloHunt, STATE_Hunt);
		fsm.registerDefaultTransition(STATE_CarteHunt, STATE_Hunt);
	
		fsm.registerDefaultTransition(STATE_HelloInter, STATE_Interblocage);
		fsm.registerDefaultTransition(STATE_CarteInter, STATE_Interblocage);
		
		
//		fsm.registerDefaultTransition(STATE_HelloEnd, STATE_END);
//		fsm.registerDefaultTransition(STATE_CarteEnd, STATE_END);
		
		
/*		fsm.registerTransition(STATE_END, STATE_END, 13);
		fsm.registerTransition(STATE_END, STATE_HelloEnd, 14);
		fsm.registerTransition(STATE_END, STATE_CarteEnd, 15);*/


		//Add the behaviours
		//addBehaviour(new Strategy(this, noeudsVus));
		//addBehaviour(new SayHello(this));
		addBehaviour(fsm);

		System.out.println("the agent "+this.getLocalName()+ " is started");

	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Agent "+getAID().getLocalName()+ " is terminated");
	}
	
}

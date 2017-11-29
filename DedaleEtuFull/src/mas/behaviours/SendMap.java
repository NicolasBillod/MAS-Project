package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import princ.Node;
import princ.BackpackInfo;
import java.util.List;
import java.io.IOException;
import java.io.Serializable;


public class SendMap extends Behaviour{


	private static final long serialVersionUID = -2058134622078521998L;
	private List<Node> noeudsVus;


	
	
	
	/*
	 * L'agent regarde s'il a des messages "Hello".
	 * Il répond en envoyant la carte et les infos sur son sac et son type de tresors
	 */
	public SendMap (final Agent myagent, List<Node> noeudsVus) {
		super(myagent);
		this.noeudsVus = noeudsVus;
	}

	
	
	
	
	
	@Override
	public void action() {

		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=""){
			
			MessageTemplate mt;
			mt = MessageTemplate.MatchConversationId("Hello");
			boolean test=true;
			
			
			// Tant qu'il y a des messages "Hello" dans la boite au lettre.
			while(test){
				
				ACLMessage reply = myAgent.receive(mt); 
				
				// Si on trouve un message "Hello", on envoie un reponse avec la carte et une autre avec BackpackInfo
				if(reply != null){
				
					
					 // Premier message avec la carte
					try{
						ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
						msg.addReceiver(reply.getSender());
						msg.setSender(this.myAgent.getAID());
						msg.setConversationId("Carte");
						msg.setContentObject((Serializable) noeudsVus);						
						
						((mas.abstractAgent)this.myAgent).sendMessage(msg);

					}catch(IOException e){
						e.printStackTrace();
					}
					
				
					
					
					// Second message 
					try {
						ACLMessage msg2=new ACLMessage(ACLMessage.INFORM); 
						msg2.addReceiver(reply.getSender());
						msg2.setSender(this.myAgent.getAID());
						msg2.setConversationId("BackpackInfo");
						
						BackpackInfo myBag = new BackpackInfo(((mas.abstractAgent)this.myAgent).getLocalName(), ((mas.agents.CognitifAgent)this.myAgent).getTypeT(), ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
						msg2.setContentObject(myBag);
						
						((mas.abstractAgent)this.myAgent).sendMessage(msg2);
						//System.out.println("Agent "+this.myAgent.getLocalName()+" envoie : "+ myBag.getNom()+ " capacity : "+ myBag.getCapacity()+" type "+myBag.getTypeT());
	
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else{
					test = false; // On a repondu a tous les messages "Hello"
				}
			}	

		}

	}
	
	
	
	public boolean done(){
		return true;
	}

}
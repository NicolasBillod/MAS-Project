package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import princ.Node;
import princ.BackpackInfo;
import java.util.ArrayList;
import java.util.List;



public class SayHello extends Behaviour{

	
	private static final long serialVersionUID = -2058134622078521998L;
	private List<Node> noeudsVus;
	private List<BackpackInfo> bagInfos;

	
	

	/*
	 * L'agent envoie un message "Hello" et attend une reponse.
	 * Il concatene alors la carte recue avec sa carte à lui
	 * Il met aussi a jour sa liste d'information des sacs a dos
	 */
	public SayHello (final Agent myagent, List<Node> noeudsVus, List<BackpackInfo> bagInfos) {
		super(myagent);
		this.noeudsVus = noeudsVus;
		this.bagInfos = bagInfos;
	}

	
	
	
	
	
	
	
	
	@Override
	public void action() {

		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();


		

		if (myPosition!=""){
			
			
			//System.out.println("Agent "+this.myAgent.getLocalName()+ " is sending a Hello message.");
			ACLMessage msg=new ACLMessage(ACLMessage.INFORM); // ACLMessage.INFORM = 7
			msg.setSender(this.myAgent.getAID());
			msg.setContent("Hello World, I'm at "+myPosition);
			msg.setConversationId("Hello");

			
			
			/****
			 * Getting AID of all the other agents still register in the DFS
			 ****/
			DFAgentDescription template = new DFAgentDescription();
	        ServiceDescription sd = new ServiceDescription();
	        sd.setType("explorer");
	        template.addServices(sd);
	        
	        try {
	        	DFAgentDescription[] otherAgents = DFService.search(myAgent, template);
	            for (int i = 0; i < otherAgents.length; ++i) {
	            	if(!otherAgents[i].getName().getName().equals(this.myAgent.getName())){ // To avoid sending the message to himself
	            		msg.addReceiver(otherAgents[i].getName()); // otherAgents[i].getName() is an AID
	            	}
	            }	            

				((mas.abstractAgent)this.myAgent).sendMessage(msg);

	        }
	        catch (FIPAException fe) {
	        	fe.printStackTrace();
	        }


	        
	        
	        MessageTemplate mt = MessageTemplate.MatchConversationId("Carte");
			this.myAgent.blockingReceive(mt, 500); // On attend la reponse
			boolean test=true;
			
			
			
			// Tant qu'il y a des messages "Carte" dans la boite au lettre.
			while(test){
				
		        ACLMessage reply = myAgent.receive(mt);
				
		        
				// Si on trouve un message "Carte", on concatene les cartes
				if(reply != null){
				 	List<Node> carte =  new ArrayList<Node>();
				 	int ind;
				 	
				 	try {
						carte = (List<Node>)reply.getContentObject();

						Node actuel;
						
						
						for (Node n : carte){ // Pour chaque noeud de la carte recue
							ind=indInList(n.getNom(),noeudsVus);
									
							
							if(ind == -1){ // Si le noeud n'appartient pas aux noeudsVus (=notre carte)
								Node nouveau = new Node(n.getNom());
								nouveau.copyWithoutNeigh(n);
								int indiceNoeud;
	
								for(Node succ : n.getSuccesseurs()){
									indiceNoeud=indInList(succ.getNom(), noeudsVus);
									
									if(indiceNoeud != -1){ // S'il appartient aux noeuds deja visites - maj de la liste des voisins du noeud actuel
										if(indInList(noeudsVus.get(indiceNoeud).getNom(), nouveau.getSuccesseurs()) == -1)
											nouveau.ajoutVoisin(noeudsVus.get(indiceNoeud));
									}
									else{ //Creation du noeud + ajout aux voisins du noeud actuel
	
										Node noeudVoisin = new Node(succ.getNom());
										noeudVoisin.copyWithoutNeigh(succ);
										noeudsVus.add(noeudVoisin);
										nouveau.ajoutVoisin(noeudVoisin);
									}
									
								}
								
								noeudsVus.add(nouveau);
									
								
								
							}
							else{ // Le noeud appartient deja aux noeudsVus
								
								 if(!n.getOpen()){ // Si le noeud appartient + il est ferme dans la carte recue
									 actuel=noeudsVus.get(ind);
									 
									 if(!actuel.getOpen()){ // Check s'il est ferme dans noeudsVus
										if(actuel.getTreasure()>n.getTreasure()){
											actuel.setTreasure(n.getTreasure()); // maj de la quantite de tresor									
										}
										
										int indiceNoeud;
										for(Node succ : n.getSuccesseurs()){
											indiceNoeud=indInList(succ.getNom(), noeudsVus);
	
											if(indiceNoeud != -1){ // S'il appartient aux noeuds deja visites - maj de la liste des voisins du noeud actuel
												if(indInList(succ.getNom(), actuel.getSuccesseurs()) == -1)
													actuel.ajoutVoisin(noeudsVus.get(indiceNoeud));
											}
											else{ //Creation du noeud + ajout aux voisins du noeud actuel
	
												Node noeudVoisin = new Node(succ.getNom());
												noeudVoisin.copyWithoutNeigh(succ);
												noeudsVus.add(noeudVoisin);
												actuel.ajoutVoisin(noeudVoisin);
											}
													
										}
									 
									 
									 }
									 else{ // Le noeud est ouvert dans noeudsVus
										 actuel.copyWithoutNeigh(n);
										 int indiceNoeud;
										 for(Node succ : n.getSuccesseurs()){
											indiceNoeud=indInList(succ.getNom(), noeudsVus);
												
											if(indiceNoeud != -1){ // S'il appartient aux noeuds deja visites - maj de la liste des voisins du noeud actuel
												if(indInList(noeudsVus.get(indiceNoeud).getNom(), actuel.getSuccesseurs()) == -1)
													actuel.ajoutVoisin(noeudsVus.get(indiceNoeud));
											}
											else{ //Creation du noeud + ajout aux voisins du noeud actuel
	
												Node noeudVoisin = new Node(succ.getNom());
												noeudVoisin.copyWithoutNeigh(succ);
												noeudsVus.add(noeudVoisin);
												actuel.ajoutVoisin(noeudVoisin);
											}
												
										}
									 }
									 
								 }
							}
								
						}
						
						
						
						
					
						// BackpackInfo : 
				        MessageTemplate mt2 = MessageTemplate.MatchConversationId("BackpackInfo");
						ACLMessage reply2 = myAgent.receive(mt2);
						
						this.myAgent.blockingReceive(mt2, 500);
						if(reply2 != null){
						
							try{
								BackpackInfo bagInfoReceived = (BackpackInfo) reply2.getContentObject();
								//System.out.println("Agent "+this.myAgent.getLocalName()+" recu : "+ bagInfoReceived.getNom()+ " capacity : "+ bagInfoReceived.getCapacity()+" type "+bagInfoReceived.getTypeT());
								int indBag = indInListBag(bagInfoReceived.getNom(), bagInfos);
								if(indBag != -1){
									bagInfos.get(indBag).setCapacity(bagInfoReceived.getCapacity());
									bagInfos.get(indBag).setTypeT(bagInfoReceived.getTypeT());
								}
								else{
									bagInfos.add(bagInfoReceived);
								}
							} catch (UnreadableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						
				 	
				 	} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					test=false;
				}
				
			}
		}

	}
	
	
	public boolean done(){
		return true;
	}
	
	
	
	
	
	
	
	// Si valeur retournee est -1 alors pas dans la liste, sinon indice.
	public int indInList(String nomNoeud, List<Node> noeudsRencontres){
		int i;
		for(i=0; i<noeudsRencontres.size(); ++i){
			if(nomNoeud.equals(noeudsRencontres.get(i).getNom())){
				return i;
			}
		}
		return -1;
	}
	
	
	
	
	// Si valeur retournee est -1 alors pas dans la liste, sinon indice.
	public int indInListBag(String nomExplo, List<BackpackInfo> bagInfos){
		int i;
		if(!bagInfos.isEmpty()){
			for(i=0; i<bagInfos.size(); ++i){
				if(nomExplo.equals(bagInfos.get(i).getNom())){
					return i;
				}
			}
		}
		return -1;
	}

}
package mas.behaviours;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import env.Attribute;
import env.Couple;
import princ.Node;
import jade.core.behaviours.Behaviour;

/**************************************
 * 
 * 
 * 				BEHAVIOUR
 * 
 * 
 **************************************/



public class Strategy extends Behaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	private List<Node> noeudsVus;
	private int typeT;
	private int exitValue;
	private boolean dijkstra;
	private int indDijkstra;
	List<Node> listeLargeur;
	List<Integer> chemin;
	

	/*
	 * L'agent realise une exploration de la carte
	 * Il explore les cases voisines si elles ne l'ont pas encore ete
	 * Il cherche la case la plus proche non exploree sinon
	 * (Sa carte est actualisee lorsqu'il communique)
	 */
	public Strategy (final mas.abstractAgent myagent, List<Node> noeudsVus, int typeT) {
		this.typeT = typeT;
		this.noeudsVus = noeudsVus;
		this.exitValue = 1;
		this.dijkstra = false;
		this.indDijkstra = 0;
		this.listeLargeur = new ArrayList<Node>();
		this.chemin = new ArrayList<Integer>();
	}

	@Override
	public void action() {
				
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=""){

			List<Couple<String,List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();//myPosition

			
			//Little pause to allow you to follow what is going on
			/*try {
				System.out.println("Press a key to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			
			
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		
			
			
			/*****
			 * If we are not already doing a computed dijkstra
			 *****/
			if(!dijkstra){
				Node noeudActuel;
				Node noeudVoisin;
				int indiceNoeud;
	
				
				
				/*
				 * First, we look at the node we are on
				 */
				// Si nouveau noeud, ajouter le noeud - seulement pour le premier
				if(noeudsVus.isEmpty()){
					noeudActuel = new Node(myPosition, lobs);
					noeudActuel.fermer();
					noeudsVus.add(noeudActuel);
				}
				else{ // Ce noeud existe forcement deja dans les noeuds a visiter - une mise a jour est suffisante
					indiceNoeud = indInList(myPosition, noeudsVus);
					noeudActuel = noeudsVus.get(indiceNoeud);
					noeudActuel.checkForTreasure(lobs);
					if(noeudActuel.getOpen()){
						noeudActuel.fermer();
					}
				}
				
				
				
				
				
				
				
				
				
				
				
				/*
				 * We are now looking at the neighbors
				 */
				String nomVoisin;
				List<String> voisinsDejaVus = new ArrayList<String>();
				int cpt=0;
				
				
				for(Couple<String,List<Attribute>> infoVoisin : lobs){
	
					nomVoisin = infoVoisin.getLeft();
					
					if(nomVoisin != myPosition){ // Vraiment un voisin - sinon il s'agit du noeud ou l'on se trouve
						
						indiceNoeud=indInList(nomVoisin, noeudsVus);
						
						if(indiceNoeud != -1){ // S'il appartient aux noeuds deja visites - maj de la liste des voisins du noeud actuel
							if(indInList(noeudsVus.get(indiceNoeud).getNom(), noeudActuel.getSuccesseurs()) == -1)
								noeudActuel.ajoutVoisin(noeudsVus.get(indiceNoeud));

							if(!noeudsVus.get(indiceNoeud).getOpen()){ // Check pour savoir si on est deja alle sur ce voisin
								voisinsDejaVus.add(noeudsVus.get(indiceNoeud).getNom());
								cpt++;
							}
							
							
						}
						
						else{ //Creation du noeud + ajout aux voisins du noeud actuel
							noeudVoisin = new Node(nomVoisin);
							noeudsVus.add(noeudVoisin);
							noeudActuel.ajoutVoisin(noeudVoisin);
						}
						
						
					}
					
					
				} // On a normalement ajoute tous les voisins comme il faut
				
				
				
				
				
				int nbFermes = 0;
				for(Node n : noeudsVus){
					if(!n.getOpen())
						nbFermes++;
				}
				
				
				// Check if we are on the last node that wasn't explored and return if we are.
				if(noeudsVus.size() == nbFermes){
					exitValue = 3;
					System.out.println();					
					System.out.println("Agent "+this.myAgent.getLocalName()+" explored the whole map and his backpack has freespace : "+((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());					
					
					// Affichage de sa carte
					/*for(Node n : noeudsVus){
						System.out.println("Nom : "+n.getNom()+" qTresor : "+n.getTreasure()+" typeT : "+n.getTypeT()+" Open : "+n.getOpen()+" succ :");
						for(Node n2 : n.getSuccesseurs())
							System.out.println("Nom : "+n2.getNom());
					}*/
					
					System.out.println("On his map he can find the tresors : ");
					for( Node n : noeudsVus){
						if(n.getTreasure() != 0)
							System.out.println(n.getNom()+" qTreasure : "+n.getTreasure()+" type : "+n.getTypeT());
					}
					
					//done();
					return;
				}
				
				
				
				
				
				else{ // Else keep exploring
					
					
					/*
					 * We can now end this part with the movement :
					 * "go to a neighbor" (if)
					 * or 
					 * "execute Dijkstra" (else)
					 */
					
					if(cpt < lobs.size()-1){ // SI ON PEUT ACCEDER A UN VOISIN DIRECT
						
						voisinsDejaVus.add(myPosition);
						
						//Random move from the current position
						Random r= new Random();
						int moveId=r.nextInt(lobs.size()-voisinsDejaVus.size());
						
						//Don't go to a neighbor already visited
						while(voisinsDejaVus.contains(lobs.get(moveId).getLeft())){
							moveId+=1;
						}
						((mas.abstractAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
						voisinsDejaVus.clear();
					}
					
					
					else{ // SINON DIJKSTRA
						dijkstra = true;
						listeLargeur.clear();
						int indice = 0;
						chemin.clear();
						List<Integer> tabInd = new ArrayList<Integer>();
						listeLargeur.add(noeudActuel);
						tabInd.add(0);
						boolean test = false;
						
						for(int i=0;i<tabInd.size(); ++i) {
							for(Node n : listeLargeur.get(i).getSuccesseurs()){
								if(!listeLargeur.contains(n)){
									listeLargeur.add(n);
									tabInd.add(i);
								
									if(n.getOpen()){
										test=true;
										break;
									}
								}
								
							}
							if(test)
								break;
						}
						
						
			
						
						indice=tabInd.size()-1;
						while(indice!=0){
							chemin.add(indice);
							indice = tabInd.get(indice);
						}
							
						indDijkstra = 1;
						
						if( !((mas.abstractAgent)this.myAgent).moveTo(listeLargeur.get(chemin.get(chemin.size()-indDijkstra)).getNom()) ){ //On accede aux noeuds correspondant au chemin a travers leur indice dans listeLargeur 
							indDijkstra = 0;
						}
					}
				}
			}
			
			
			
			
			
			
			/*****
			 * If we are already doing a computed dijkstra
			 *****/
			else{
				if(listeLargeur.get(chemin.get(0)).getOpen() == false){
					dijkstra = false;
					indDijkstra = 0;
					listeLargeur.clear();
					chemin.clear();
				}
				else{
					if(indDijkstra < chemin.size()){
						indDijkstra++;
						if( !((mas.abstractAgent)this.myAgent).moveTo(listeLargeur.get(chemin.get(chemin.size()-indDijkstra)).getNom()) ){ //On accede aux noeuds correspondant au chemin a travers leur indice dans listeLargeur 
							indDijkstra--;
						}
					}
					
					if(indDijkstra == chemin.size()){
						dijkstra = false;
						indDijkstra = 0;
						listeLargeur.clear();
						chemin.clear();

					}
				}
			}
			
			
		}
		
		
		
		Random test= new Random();
		double resRandom = test.nextDouble();
		if(resRandom < 0.25)
			exitValue = 0;
		if(resRandom >= 0.25 && resRandom < 0.5)
			exitValue = 1;
		if(resRandom >= 0.5)
			exitValue = 2;
		
	}
	
	
	@Override
	public boolean done(){
		return true;
	}
	
	
	
	
	public int onEnd() {
		return exitValue;
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
	
}

package princ;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import env.Attribute;
import env.Couple;





public class Node implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1810028550097641911L;
	private List<Node> successeurs;
	private int treasure;
	private int typeT; // 0 = nothing ; 1 = treasure ; 2 = diamonds
	private String name;
	private boolean open;
	
	
	
	
	
	
	/********************************************** 
	 * 
	 * Constructors 
	 * 
	 **********************************************/
	
	public Node(String name){
		this.name=name;
		treasure=0;
		typeT = 0;
		open=true;
		successeurs = new ArrayList<Node>();
	}
	
	
	public Node(String nomNoeud, List<Couple<String, List<Attribute>>> lobs) {
		name = nomNoeud;
		
		for(Couple<String, List<Attribute>> a: lobs){
			if(a.getLeft().equals(name)){
				if(!a.getRight().isEmpty()){
					if(a.getRight().get(0).getName().equals("Diamonds")){
						typeT=2;
					}
					else{
						typeT=1;
					}
					treasure = (int)a.getRight().get(0).getValue();
				}
				else
					treasure=0;
			}
				
		}

		open=true;
		successeurs= new ArrayList<Node>();
	}
	
	
	
	
	
	
	
	/********************************************** 
	 * 
	 * Getters 
	 * 
	 **********************************************/
	public String getNom(){
		return name;
	}
	
	public boolean getOpen(){
		return open;
	}
	
	public int getTreasure(){
		return treasure;
	}
	
	public List<Node> getSuccesseurs(){
		return successeurs;
	}
	
	public int getTypeT(){
		return typeT;
	}
	
	
	
	
	
	/********************************************** 
	 * 
	 * Other functions : ajoutVoisin(), fermer(), setTreasure, update(), checkForTreasure()
	 * 
	 **********************************************/
	public void ajoutVoisin(Node voisin){
		successeurs.add(voisin);
	}
	
	public void fermer(){
		open = false;
	}


	public void setTreasure(int treasure2) {
		treasure=treasure2;
	}
	
	public void checkForTreasure(List<Couple<String, List<Attribute>>> lobs){
		for(Couple<String, List<Attribute>> a: lobs){
			if(a.getLeft().equals(name)){
				if(!a.getRight().isEmpty()){
					if(a.getRight().get(0).getName().equals("Diamonds")){
						typeT=2;
					}
					else{
						typeT=1;
					}
					treasure = (int)a.getRight().get(0).getValue();
					
				}
				else{
					treasure=0;
				}
			}
				
		}
	}

	
	public void copyWithoutNeigh(Node n) {
		treasure=n.getTreasure();
		typeT=n.getTypeT();
		open=n.getOpen();
		name=n.getNom();		
	}

}

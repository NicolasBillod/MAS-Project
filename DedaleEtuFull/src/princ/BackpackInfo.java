package princ;
import java.io.Serializable;


public class BackpackInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -451211175790184003L;
	private String nom;
	private int capacity;
	private int typeT;
	
	public BackpackInfo(String nom, int typeT, int capacity){
		this.nom = nom;
		this.capacity = capacity;
		this.typeT = typeT;
	}
	
	public int getCapacity(){
		return capacity;
	}
	
	public int getTypeT(){
		return typeT;
	}
	
	public String getNom(){
		return nom;
	}
	
	public void setCapacity(int capacity){
		this.capacity = capacity;
	}
	
	public void setTypeT(int typeT){
		this.typeT = typeT;
	}
	
}

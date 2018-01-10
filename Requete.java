public class Requete {
	public int entier;
	public String chemin;


	
	public Requete(int entier, String chemin){
		this.entier=entier;
		this.chemin=chemin;
				}
	
		public void setEntier (int entier){
			this.entier=entier;
		}
		public int getEntier(){
			return entier;
		}
		public void setChemin(String chemin){
			this.chemin=chemin;
		}
		public String getChemin(){
			return chemin;
		}

		
		
	public String toString(){
		return (" "+chemin+entier);
	}

}

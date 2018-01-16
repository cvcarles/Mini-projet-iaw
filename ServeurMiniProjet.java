// import des packages
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import java.io.File;

	public class ServeurMiniProjet extends Server{
		public ServeurMiniProjet(int port,boolean verbose)throws IOException {
			super(port,verbose);				//appel du constructeur de la classe mère
			String DataInputStream=null;
			 
			
		}
		public int entier;			// déclaration des variables 
		public int i=0;
		public int j=0;
		public File file;

		
					
	private void ecrit(String url) throws IOException{		// retranscrit le fichier sur le navigateur
		FileInputStream f=new FileInputStream(""+url);
		byte[] b=new byte[8];
		while (f.read(b)>=0){write(b);}
		f.close();
		
	}
	private void shell(String url){					// execute le shell passé en paramètre
		try{
			Process myp = Runtime.getRuntime().exec(url);
			myp.waitFor(); 						// attention, cette attente peut bloquer si la commande externe rencontre un problème
			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(myp.getInputStream()));
			while((line = in.readLine()) != null) {
			writeline(line);}
			} 
			catch(Exception e) {
				System.out.println("error");
			}
	}
	
		private void dialogue() throws IOException{
			while (true){
			acceptConn();
			Requete req = null;
			req = getRequete();
			if (req!=null){
				repRequete(req);
				}
			closeConn();
			}
			
		}
				
		public void repRequete(Requete req) throws IOException{
			if (entier==1){																			// si l'URL est /date
				SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy HH:mm:ss");
				Date date = new Date();
				writeline("HTTP/1.0 200 OK");							 
				writeline("content-type: text/plain");
				writeline("");
				writeline("Date Courante");
				writeline(dateFormat.format(date));}
			
			else if (entier==2){													// si l'URL pointe vers un répertoire
				writeline("HTTP/1.0 200 OK");							 			
				writeline("content-type: text/html");
				writeline("");
				writeline("<!DOCTYPE html>");
				String liste[] = file.list();             				     		
				for (int i = 0; i < liste.length; i++) {
					if (Pattern .compile("^index.html$").matcher(liste[i]).find()){j=1;}    // si le répertoire possède un fichier de type index.html ou .htm on l'ouvre
					else if (Pattern .compile("^index.htm$").matcher(liste[i]).find()){					
						j=2;
					}
				}
					if (j==1){ecrit(this.file+"/index.html");} // ouvrir le fichier et l'afficher
							
				else if (j==2){ecrit(this.file+"/index.htm");}
					
				else {															// sinon lister le contenu du répertoire
					writeline("<h1> Contenu du r&eacute;pertoire "+file+"</h1>");
					for (int i = 0; i < liste.length; i++) {
						writeline("<a href='"+file+"/"+liste[i]+"' >"+liste[i]+"</a><br>");
						}
					}
			}
			
			
		else if (entier>2 && entier<=8){		// si l'URL pointe vers un fichier
			writeline("HTTP/1.0 200 OK");	

				if      (entier==3){writeline("content-type: image/jpg");}
				else if (entier==4){writeline("content-type: image/png");}
				else if (entier==5){writeline("content-type: video/mpge");}
				else if (entier==6){writeline("content-type: text/html");}
				else if (entier==7){writeline("content-type: text/css");}
				else if (entier==8){writeline("content-type: application/pdf");}
			
			writeline("");
			ecrit(""+this.file);
					}
		else if (entier==0){						//code d'erreur
			writeline("HTTP/1.0 404 Not Found");
			writeline("content-type: text/html");
			writeline("");
			writeline("<!DOCTYPE html>");
			writeline("<html><body><h1>Votre fichier n'existe pas</h1>");
			writeline("<img src=\"/home/user/Documents/mon-cgi/tantpis.png\" alt=\"tant pis\"></body></html>");
							}
		}
				
	

		public Requete getRequete() throws IOException{
			String[] mots;
			String ligne=readline();
			if (ligne == null) return null;
			if (ligne.isEmpty()) return null;
			mots=ligne.split(" ");								//  créer un tableau en sérapant la ligne en fonction des espaces
			file = new File(mots[1]);		// file correspond au deuxième paramètre de type File contenu dans le package java.io.File
			if (mots.length>1){
				if (!mots[0].equals("GET") && !mots[0].equals("POST")){		// si la requete n'est ni de type GET ni de type POST
					return null;
				}
				while (ligne.isEmpty()==false){				// tant que la ligne n'est pas vide, la lire
					ligne=readline();
					if (ligne == null) return null;
					}
				
		}
// on assimile un entier à la variable "entier" selon les arguments de l'url
			
			if (mots[1].equals("/date")){
				entier=1;
			}
			else if (Pattern .compile(".*/mon-cgi/.*.sh$").matcher(mots[1]).find()){ // en cas de shell passé en url
			
					writeline("HTTP/1.0 200 OK");
					shell(""+file);
					entier=11;
			
			}
			
			else if (Pattern .compile(".mon-cgi/.*.sh\\?").matcher(mots[1]).find()){ // en cas de shell avec des paramètres
				
				writeline("HTTP/1.0 200 OK");
				String[] var = mots[1].split("\\?");		//mot[1] l'url entière
				String[] parametre = var[1].split("\\&");	// va séparer les variables de leurs valeurs parametre[0] les variables et leurs valeurs associées avec le = au milieu
				String resultat=("" +var[0]+" ");
				
		for (int i=0;i<parametre.length;i++ )	{			// si le shell possède des paramètres

				String[] val=parametre[i].split("=");		// on sépare chaque paramètre en fonction de "="
				resultat += (" "+val[0]+" "+val[1]);		// on associe le paramètre à sa valeur

		}
				shell(resultat); //lancer le shell avec des paramètres ex  var[0] le débute de l'url val[0]=mode et val[1]=cowthink
				entier=11;			// de manière à éviter qu'il ne le considère comme un répertoire
				}
		
			else if (file.exists() && file.isDirectory()==true){		// vérifie que le répertoire existe
				entier=2;
				}	
			else if (file.exists()){			// vérifie si ex: /home/user/Documents/salut.jpg existe
				
				if (Pattern .compile(".jpg$").matcher(mots[1]).find()){entier=3;}
				if (Pattern .compile(".png$").matcher(mots[1]).find()){entier=4;}
				if (Pattern .compile(".mp4$").matcher(mots[1]).find()){entier=5;}
				if (Pattern .compile(".html$").matcher(mots[1]).find()||Pattern .compile(".htm$").matcher(mots[1]).find()){entier=6;}
				if (Pattern .compile(".css$").matcher(mots[1]).find()){entier=7;}
				if (Pattern .compile(".pdf$").matcher(mots[1]).find()){entier=8;}
			}
			
			else{entier=0;}						// si le fichier ou le répertoire donné dans l'url n'existe pas, on renvoie l'entier renvoyant vers le message d'erreur
			return new Requete(entier, mots[1]);
		}
		

		
		
	public static void main(String[] args) throws IOException{		// fonction main du programme
			ServeurMiniProjet myserver = null;
			try {
				myserver = new ServeurMiniProjet(1234, true);
				} 
			catch (IOException e) {
				System.out.println("Problem while creating server!");
				System.exit(-1); // code erreur <> 0 pour signaler qu'il y a un problème 
				}
			try {
				myserver.dialogue();
				} 
			catch (IOException e) {
				System.out.println("Problem while talking to the client!");
				} 
			finally {
				System.out.println("Killing the server");
				myserver.close();
					}
			
				}
	// methode de dialogue correspondant à l'écho par le serveur d'une(seule) chaine lue cad reçue (envoyée) du client
		
}


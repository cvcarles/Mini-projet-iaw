import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import java.io.File;

	public class ServeurMiniProjet extends Server{
		public ServeurMiniProjet(int port,boolean verbose)throws IOException {
			super(port,verbose);
			String DataInputStream=null;
			 
			
		}
		public int entier;
		private File file;		// de type File contenu dans le package java.io.File
		public int i=0;
		public int j=0;

		
		private void dialogue() throws IOException{
			while (true){
			acceptConn();
			Requete req = null;
			req = getRequete();
			if (req!=null){
				repRequete(req);
			}
			closeConn();}}
			
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
					if (Pattern .compile("^index.html$").matcher(liste[i]).find()){j=1;}    // si le répertoire pocède un fichier de type index.html ou .htm
					else if (Pattern .compile("^index.htm$").matcher(liste[i]).find()){					
						j=2;}}
					
					if (j==1){ecrit(this.file+"/index.html"); // ouvrir le fichier et l'afficher
							}
				else if (j==2){
					/*FileInputStream f=new FileInputStream(this.file+"/index.htm");
						byte[] b=new byte[8];
						while (f.read(b)>=0){write(b);}
						f.close();*/
					ecrit(this.file+"/index.htm");}
					
				else {															// sinon lister le contenu du repertoire
					writeline("<h1> Contenu du r&eacute;pertoire "+file+"</h1>");
					for (int i = 0; i < liste.length; i++) {
						writeline("<a href='"+file+"/"+liste[i]+"' >"+liste[i]+"</a><br>");}}}
			
		
		else if (entier>2 && entier<=8){		// si l'URL pointe vers un fichier
			writeline("HTTP/1.0 200 OK");	

				if      (entier==3){writeline("content-type: image/jpg");}
				else if (entier==4){writeline("content-type: image/png");}
				else if (entier==5){writeline("content-type: video/mpge");}
				else if (entier==6){writeline("content-type: text/html");}
				else if (entier==7){writeline("content-type: text/css");}
				else if (entier==8){writeline("content-type: application/pdf");}
			
			writeline("");
				/*byte[] b=new byte[8];
				FileInputStream f=new FileInputStream(this.file);
				while (f.read(b)>=0){
					write(b);}
				f.close();*/
			ecrit(""+this.file);
					}
			
				
			/*else if (entier==11){					// si l'URL pointe vers un shell
				writeline("HTTP/1.0 200 OK");
				writeline("content-type: text/html");
				writeline("");
				writeline("<!DOCTYPE html>");}*/}
				
			
			
		
		private void writeline(File files) {
			      os.println(file);
			  }			
		private void ecrit(String url) throws IOException{
			FileInputStream f=new FileInputStream(""+url);
			byte[] b=new byte[8];
			while (f.read(b)>=0){write(b);}
			f.close();
			
		}
		

		public Requete getRequete() throws IOException{
			String[] mots;
			String ligne=readline();
			if (ligne == null) return null;
			if (ligne.isEmpty()) return null;
			mots=ligne.split(" ");								//  créer un tableau en sérapant par en fonction des espaces
			file = new File(mots[1]);							// file correspond au deuxième 
			if (mots.length>1){
				if (!mots[0].equals("GET") && !mots[0].equals("POST")){
					return null;
				}
				while (ligne.isEmpty()==false){
					ligne=readline();
					if (ligne == null) return null;
					}
				
		}
			if (mots[1].equals("/date")){
				entier=1;
			}
			else if (Pattern .compile(".*/mon-cgi/.*.sh$").matcher(mots[1]).find()){ // en cas de shell passé en url
				try{
					writeline("HTTP/1.0 200 OK");
					Process myp = Runtime.getRuntime().exec(""+file);
					myp.waitFor(); 	// attention, cette attente peut bloquer si la commande externe rencontre un problème
					String line = null;
					BufferedReader in = new BufferedReader(new InputStreamReader(myp.getInputStream()));
					while((line = in.readLine()) != null) {
					writeline(line);}
					} 
					catch(Exception e) {
						System.out.println("error");

			}
			}
			
			else if (Pattern .compile(".mon-cgi/.*.sh?").matcher(mots[1]).find()){ // en cas de shell avec des paramètres
				
				writeline("HTTP/1.0 200 OK");
				String[] var = mots[1].split("\\?");		//mot[1] l'url entière
				String[] parametre = var[1].split("\\&");	// va séparer les variables de leurs valeurs parametre[0] les variables et leurs valeurs associées avec le = au milieu
				String resultat=("" +var[0]+" ");
;
		for (int i=0;i<parametre.length;i++ )	{

				String[] val=parametre[i].split("=");
				resultat += (" "+val[0]+" "+val[1]);

		}
				try{			// si des paramètres
					Process myp = Runtime.getRuntime().exec(resultat); //lancer le shell avec des paramètres ex  var[0] le débute de l'url val[0]=mode et val[1]=cowthink
					myp.waitFor(); 	// attention, cette attente peut bloquer si la commande externe rencontre un problème
					String line = null;
					BufferedReader in = new BufferedReader(new InputStreamReader(myp.getInputStream()));
					while((line = in.readLine()) != null) {
					writeline(line);}
					
					} 
					catch(Exception e) {
						writeline("error");
			}


				
		}
			else if (file.exists() && file.isDirectory()){		// vérifie que le répertoire existe
				entier=2;
				}	
			else if (file.exists()){			// vérifie si ex: /home/user/Documents/salut existe
				
				if (Pattern .compile(".jpg$").matcher(mots[1]).find()){
					entier=3;}
				if (Pattern .compile(".png$").matcher(mots[1]).find()){
					entier=4;}
				if (Pattern .compile(".mp4$").matcher(mots[1]).find()){
					entier=5;}
				if (Pattern .compile(".html$").matcher(mots[1]).find()||Pattern .compile(".htm$").matcher(mots[1]).find()){
					entier=6;}
				if (Pattern .compile(".css$").matcher(mots[1]).find()){
					entier=7;}
				if (Pattern .compile(".pdf$").matcher(mots[1]).find()){
					entier=8;}
			}
			
			else{entier=0;}
			return new Requete(entier, mots[1]);
		}
		

		
		
		
	public static void main(String[] args) throws IOException{
			ServeurMiniProjet myserver = null;
			try {
				myserver = new ServeurMiniProjet(1234, true);
				} 
			catch (IOException e) {
				System.out.println("Problem while creating server!");
				System.exit(-1); // code erreur <> 0 pour signaler qu'il y a 
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


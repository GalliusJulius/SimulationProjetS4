public class EvenementPietonArrivePalier extends Evenement {
    /* PAP: Pieton Arrive Palier
       L'instant précis ou un passager qui à décidé de continuer à pieds arrive sur un palier donné.
       Classe à faire complètement par vos soins.
    */

	private Passager passager; 
	private Etage etage;
	
	public EvenementPietonArrivePalier(long d,Passager p,Etage e) {
    	// Signature approximative et temporaire... juste pour que cela compile.
    	super(d);
    	//on associe un passager à l'évènement courant
    	passager=p;
    	etage = e;
    }
	
    public void afficheDetails(StringBuilder buffer, Immeuble immeuble) {
    	buffer.append("PAP");
    	
    }

    public void traiter(Immeuble immeuble, Echeancier echeancier) {
    	if(!Global.isModeParfait()) {
    		etage.remove(passager);
    		etage.ajouterPieton(passager);
    	}
    }
    
    public long getPassager() {
    	return this.passager.getNumCrea();
    }
    
}

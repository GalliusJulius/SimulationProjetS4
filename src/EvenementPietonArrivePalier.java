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
    		if(etage.estPieton(passager)) {
    			if((etage.numéro() == passager.numéroDestination()) || (etage.numéro() == passager.numéroDestination()+1) || (etage.numéro() == passager.numéroDestination()-1)) {
    				etage.supPieton(passager);
    				immeuble.ajouterCumul(date-passager.dateDépart());
    				immeuble.nombreTotalDesPassagersSortis++;
    			} else {
    				if(passager.sens() == '^')
    					echeancier.ajouter(new EvenementPietonArrivePalier(date+Global.tempsPourMonterOuDescendreUnEtageAPieds,passager,immeuble.getEtage(etage.numéro()+1)));
    				else
    					echeancier.ajouter(new EvenementPietonArrivePalier(date+Global.tempsPourMonterOuDescendreUnEtageAPieds,passager,immeuble.getEtage(etage.numéro()-1)));
    			}
    		}
    		else {
	    		etage.remove(passager);
	    		etage.ajouterPieton(passager);
	    		//System.out.println(Math.abs(passager.numéroDestination()-etage.numéro()));
	    		echeancier.ajouter(new EvenementPietonArrivePalier(date+Global.tempsPourMonterOuDescendreUnEtageAPieds,passager,etage));
    		}
    	}
    }
    
    public long getPassager() {
    	return this.passager.getNumCrea();
    }
    
}

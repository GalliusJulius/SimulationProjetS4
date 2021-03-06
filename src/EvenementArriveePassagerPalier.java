public class EvenementArriveePassagerPalier extends Evenement {
    /* APP: Arrivée Passager Palier
       L'instant précis ou un nouveau passager arrive sur un palier donné.
    */
    
    private Etage étage;

    public EvenementArriveePassagerPalier(long d, Etage edd) {
	super(d);
	étage = edd;
    }

    public void afficheDetails(StringBuilder buffer, Immeuble immeuble) {
	buffer.append("APP ");
	buffer.append(étage.numéro());
    }

    public void traiter(Immeuble immeuble, Echeancier echeancier) {
		assert étage != null;
		assert immeuble.étage(étage.numéro()) == étage;
		
		Cabine cabine = immeuble.cabine;
		Passager p = new Passager(date, étage, immeuble);
	
		étage.ajouter(p);
		echeancier.ajouter(new EvenementArriveePassagerPalier(date + étage.arrivéeSuivante(), étage));
		
		if((cabine.porteOuverte) && (! cabine.cabinePleine()) && (cabine.étage.numéro() == p.étageDépart().numéro() && (!Global.modeParfait||Global.isModeParfait() && cabine.intention() == p.sens()))) {
			boolean rep = cabine.faireMonterPassager(p);
			if(rep) {
				// On ajoute 5 à la date initiale
				echeancier.modifFermeturePorte(Global.tempsPourOuvrirOuFermerLesPortes);
				//étage.suppPremierPassager();
				étage.supprimerPassager(p.getNumCrea());
			} else {
				echeancier.ajouter(new EvenementPietonArrivePalier(date+Global.délaiDePatienceAvantSportif,p,étage));
			}
		}
		else
			echeancier.ajouter(new EvenementPietonArrivePalier(date+Global.délaiDePatienceAvantSportif,p,étage));
    }
    
    public Etage getEtage() {
    	return étage;
    }

}

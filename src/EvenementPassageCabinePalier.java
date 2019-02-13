public class EvenementPassageCabinePalier extends Evenement {
    /* PCP: Passage Cabine Palier
       L'instant précis où la cabine passe juste en face d'un étage précis.
       Vous pouvez modifier cette classe comme vous voulez (ajouter/modifier des méthodes etc.).
    */
    
    private Etage étage;
    
    public EvenementPassageCabinePalier(long d, Etage e) {
	super(d);
	étage = e;
    }
    
    public void afficheDetails(StringBuilder buffer, Immeuble immeuble) {
	buffer.append("PCP ");
	buffer.append(étage.numéro());
    }
    
    public void traiter(Immeuble immeuble, Echeancier echeancier) {
		Cabine cabine = immeuble.cabine;
		assert ! cabine.porteOuverte;
		assert étage.numéro() != cabine.étage.numéro();
		
		
		long tps;
		
		if((cabine.passagersVeulentDescendre()) || (étage.aDesPassagers())) {
			tps = date + Global.tempsPourBougerLaCabineDUnEtage + Global.tempsPourEntrerOuSortirDeLaCabine * (cabine.nbPassagersVeulentDescendre(cabine.étage.numéro()) + étage.nbPassagersEtage(étage.numéro()) + tempsPourOuvrirOuFermerLesPortes); // +1 ?
			// étage.arrivéeSuivante() ?
			//géré en dessous
			//echeancier.ajouter(new EvenementOuverturePorteCabine(date+1));
			//echeancier.ajouter(new EvenementFermeturePorteCabine(date+1+(Global.tempsPourEntrerOuSortirDeLaCabine * cabine.nbPassagersVeulentDescendre(cabine.étage.numéro())) ));
		} else {
			tps = date + Global.tempsPourBougerLaCabineDUnEtage;
		}
		// TODO vérifier si des passagers veulent monter ?
		
		if((cabine.intention() == 'v') && (cabine.étage.numéro() != immeuble.étageLePlusBas().numéro()))
			cabine.étage = immeuble.étage(cabine.étage.numéro()-1);
		else if((cabine.intention() == '^') && (cabine.étage.numéro() != immeuble.étageLePlusHaut().numéro()))
			cabine.étage = immeuble.étage(cabine.étage.numéro()+1);
		
		//// A enlever dans les futures versions
		if(cabine.étage.numéro() == immeuble.étageLePlusBas().numéro()) {
			cabine.changerIntention('^');
		}
		else if(cabine.étage.numéro() == immeuble.étageLePlusHaut().numéro()){
			cabine.changerIntention('v');
		}
		////
		
		if((cabine.intention() == 'v') && (cabine.étage.numéro() != immeuble.étageLePlusBas().numéro()))
			echeancier.ajouter(new EvenementPassageCabinePalier(tps, immeuble.étage(cabine.étage.numéro()-1))); // étage.arrivéeSuivante() ?
		else if((cabine.intention() == '^') && (cabine.étage.numéro() != immeuble.étageLePlusHaut().numéro()))
			echeancier.ajouter(new EvenementPassageCabinePalier(tps, immeuble.étage(cabine.étage.numéro()+1))); // étage.arrivéeSuivante() ?
		
		//Gestion de l'ouverture des portes + intention
		
		if(Global.modeParfait && cabine.nbPassagersDansCabine() == 0 && étage.aDesPassagers()) {
			echeancier.ajouter(new EvenementOuverturePorteCabine(date));
			if(cabine.intention() != étage.getPremierPassager().sens()) {
				cabine.changerIntention(étage.getPremierPassager().sens());
			}
		}
		else if(Global.modeParfait && étage.aDesPassagers() && (cabine.intention() == étage.getPremierPassager().sens()) && !cabine.cabinePleine()){
			echeancier.ajouter(new EvenementOuverturePorteCabine(date));
		}
		else if(!Global.modeParfait && étage.aDesPassagers()) {
			echeancier.ajouter(new EvenementOuverturePorteCabine(date));
		}
		else if(cabine.passagersVeulentDescendre()) {
			System.out.println(étage);
			echeancier.ajouter(new EvenementOuverturePorteCabine(date));
		}
		
		if(cabine.nbPassagersDansCabine() != 0) {
			cabine.changerIntention(cabine.getPremierPassager().numéroDestination() > étage.numéro() ? '^' : 'v');
		}
		
		//System.out.println(étage.arrivéeSuivante());
		//System.out.println(date);
		//System.out.println(echeancier.tempsPourBougerLaCabineDUnEtage);
    }
}

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
		
		//if(!cabine.passagersVeulentDescendre()) {
		tps = date + echeancier.tempsPourBougerLaCabineDUnEtage;
		/*} else {
			tps = date + echeancier.tempsPourBougerLaCabineDUnEtage + echeancier.tempsPourEntrerOuSortirDeLaCabine * (cabine.);
		}*/
		
		if((cabine.intention() == 'v') && (cabine.étage.numéro() != immeuble.étageLePlusBas().numéro()))
			cabine.étage = immeuble.étage(cabine.étage.numéro()-1);
		else if((cabine.intention() == '^') && (cabine.étage.numéro() != immeuble.étageLePlusHaut().numéro()))
			cabine.étage = immeuble.étage(cabine.étage.numéro()+1);

		if(cabine.étage.numéro() == immeuble.étageLePlusBas().numéro()) {
			cabine.changerIntention('^');
		}
		else if(cabine.étage.numéro() == immeuble.étageLePlusHaut().numéro()){
			cabine.changerIntention('v');
		}
		
		if((cabine.intention() == 'v') && (cabine.étage.numéro() != immeuble.étageLePlusBas().numéro()))
			echeancier.ajouter(new EvenementPassageCabinePalier(tps, immeuble.étage(cabine.étage.numéro()-1))); // étage.arrivéeSuivante() ?
		else if((cabine.intention() == '^') && (cabine.étage.numéro() != immeuble.étageLePlusHaut().numéro()))
			echeancier.ajouter(new EvenementPassageCabinePalier(tps, immeuble.étage(cabine.étage.numéro()+1))); // étage.arrivéeSuivante() ?
		
		//System.out.println(étage.arrivéeSuivante());
		//date--;
		//System.out.println(echeancier.tempsPourBougerLaCabineDUnEtage);
    }
}

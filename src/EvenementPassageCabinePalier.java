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

    /**
     *Traitement de l'évènement
     */
    public void traiter(Immeuble immeuble, Echeancier echeancier) {
		Cabine cabine = immeuble.cabine;
		assert ! cabine.porteOuverte;
		assert étage.numéro() != cabine.étage.numéro();


		long tps;

		if((cabine.passagersVeulentDescendre()) || (étage.aDesPassagers() &&!Global.modeParfait)) {
			tps = date + Global.tempsPourBougerLaCabineDUnEtage + Global.tempsPourEntrerOuSortirDeLaCabine * (cabine.nbPassagersVeulentDescendre(cabine.étage.numéro()) + étage.nbPassagersEtage(étage.numéro()) + tempsPourOuvrirOuFermerLesPortes); // +1 ?
		} else {
			tps = date + Global.tempsPourBougerLaCabineDUnEtage;
		}

		//On fait avancer la cabine avec un garde fou évitant de faire un déplacement impossible
		if((cabine.intention() == 'v') && (cabine.étage.numéro() != immeuble.étageLePlusBas().numéro()))
			cabine.étage = immeuble.étage(cabine.étage.numéro()-1);
		else if((cabine.intention() == '^') && (cabine.étage.numéro() != immeuble.étageLePlusHaut().numéro()))
			cabine.étage = immeuble.étage(cabine.étage.numéro()+1);

		//boolean disant si la cabine va ouvrir ces portes
		boolean monter=doitOuvrirPortes(cabine);
		//si on peut monter on le fait sinon on regarde si il faut changer l'intention et on re check si on doit ouvrir les portes
		if(monter){
			echeancier.ajouter(new EvenementOuverturePorteCabine(date + Global.tempsPourOuvrirOuFermerLesPortes));
		}
		else {
			monter = doitOuvrirPorteIntent(cabine,changerIntention(immeuble));
			if(monter) echeancier.ajouter(new EvenementOuverturePorteCabine(date + Global.tempsPourOuvrirOuFermerLesPortes));
		}
		//On regarde si on doit ouvrir la porte de la cabine
		if((!étage.aDesPassagers() || (Global.isModeParfait() && !monter))&& !cabine.passagersVeulentDescendre()) {

			if((cabine.intention() == 'v') && (cabine.étage.numéro() != immeuble.étageLePlusBas().numéro())) {

				echeancier.ajouter(new EvenementPassageCabinePalier(tps, immeuble.étage(cabine.étage.numéro()-1))); // étage.arrivéeSuivante() ?
			}
			else if((cabine.intention() == '^') && (cabine.étage.numéro() != immeuble.étageLePlusHaut().numéro())){
				echeancier.ajouter(new EvenementPassageCabinePalier(tps, immeuble.étage(cabine.étage.numéro()+1))); // étage.arrivéeSuivante() ?
			}
		}
    }

    /**
     * Premiere vérification sur l'ouverture des portes
     * @param cabine
     * @return
     */
    public boolean doitOuvrirPortes(Cabine cabine){
      if(Global.modeParfait && étage.aDesPassagers() && (!étage.getPassagerIntention(cabine.intention()).isEmpty()) && !cabine.cabinePleine()){
  			return true;
  		}
  		else if(cabine.passagersVeulentDescendre()) {
  			return true;
  		}
  		else if((étage.aDesPassagers()) && (! cabine.cabinePleine())&& ! Global.isModeParfait()){
  			return true;
  		}
      else return false;
    }
    
    /**
     * Changement de l'intention si possible
     * @param immeuble
     * @return
     */
    public int changerIntention(Immeuble immeuble) {
    	Cabine cabine = immeuble.cabine;
    	int nb =-1;
		if(cabine.intention() == '^') nb=immeuble.passagerAuDessus(cabine.étage);
		else nb = immeuble.passagerEnDessous(cabine.étage);

		// Cas très spécifique pour les 2 modes
		if((((cabine.intention() == '^') && (immeuble.passagerAuDessus(cabine.étage) == -1) && (immeuble.passagerEnDessous(cabine.étage) != -1))
				|| ((cabine.intention() == 'v') && (immeuble.passagerEnDessous(cabine.étage) == -1) && (immeuble.passagerAuDessus(cabine.étage) != -1)))
				&& (cabine.nbPassagersDansCabine() == 0) && (!étage.aDesPassagers())){

			if((cabine.intention() == '^') && (immeuble.passagerAuDessus(cabine.étage) == -1))
				cabine.changerIntention('v');
			else
				cabine.changerIntention('^');
		}

		if(((!Global.isModeParfait()) && (!étage.aDesPassagers()))
		     && (((cabine.intention() == 'v') && (this.étage.numéro() == immeuble.étageLePlusBas().numéro())) || ((cabine.intention() == '^') && (this.étage.numéro() == immeuble.étageLePlusHaut().numéro())))) {

			if((cabine.intention() == 'v') && (this.étage.numéro() == immeuble.étageLePlusBas().numéro()))
					cabine.changerIntention('^');
				else
					cabine.changerIntention('v');
		} else if((!Global.isModeParfait())&& (!étage.aDesPassagers()) && ((cabine.nbPassagersDansCabine() == 0) || (cabine.intentionUnie() == '^') || (cabine.intentionUnie() == 'v'))){
			int dessus = immeuble.passagerAuDessus(étage);
			int dessous = immeuble.passagerEnDessous(étage);
			if((cabine.intentionUnie() == 'v') && (dessus == -1) && !cabine.passagerVeulentDescendre(cabine.intention(),cabine)) {
				cabine.changerIntention('v');
			} else if((cabine.intentionUnie() == '^') && (dessous == -1) && !cabine.passagerVeulentDescendre(cabine.intention(),cabine)) {
				cabine.changerIntention('^');
			}
		}
		return nb ;
    }
    
    /**
     * On regarde si on doit ouvrir la porte suite au changement d'intention
     * @param cabine
     * @param nb
     * @return
     */
    public boolean doitOuvrirPorteIntent(Cabine cabine,int nb) {
    	if((!Global.isModeParfait())&&étage.aDesPassagers()&&!cabine.cabinePleine()&&nb==-1)
			return true;
		else if((Global.isModeParfait())&&étage.aDesPassagers()&&!cabine.cabinePleine())
			if(((cabine.nbPassagersDansCabine() != 0) && (étage.memeIntention(cabine.intention()))) || ((nb == -1) && (cabine.nbPassagersDansCabine() == 0))) 
				return true;
		else if((!Global.isModeParfait()) && (étage.aDesPassagers()))
			return true;
    	return false;
    }


}

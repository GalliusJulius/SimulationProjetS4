public class EvenementFermeturePorteCabine extends Evenement {
    /* FPC: Fermeture Porte Cabine
       L'instant précis ou la porte termine de se fermer.
       Tant que la porte n'est pas complètement fermée, il est possible pour un passager
       de provoquer la réouverture de la porte. Dans ce cas le FPC est décalé dans le temps
       selon la méthode decalerFPC qui est dans l'échéancier.
    */

    public EvenementFermeturePorteCabine(long d) {
	super(d);
    }

    public void afficheDetails(StringBuilder buffer, Immeuble immeuble) {
	buffer.append("FPC");
    }

    public void traiter(Immeuble immeuble, Echeancier echeancier) {
		Cabine cabine = immeuble.cabine;
		assert cabine.porteOuverte;
		
		cabine.porteOuverte = false;
		long tps = date + Global.tempsPourBougerLaCabineDUnEtage;
		if((cabine.intention() == 'v') && (cabine.étage.numéro() != immeuble.étageLePlusBas().numéro()))
			echeancier.ajouter(new EvenementPassageCabinePalier(tps, immeuble.étage(cabine.étage.numéro()-1))); // étage.arrivéeSuivante() ?
		else if((cabine.intention() == '^') && (cabine.étage.numéro() != immeuble.étageLePlusHaut().numéro()))
			echeancier.ajouter(new EvenementPassageCabinePalier(tps, immeuble.étage(cabine.étage.numéro()+1))); // étage.arrivéeSuivante() ?
		assert ! cabine.porteOuverte;
    }


    public void setDate(long d){
	this.date = d;
    }

}

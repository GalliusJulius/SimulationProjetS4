public class EvenementOuverturePorteCabine extends Evenement {
    /* OPC: Ouverture Porte Cabine
       L'instant précis ou la porte termine de s'ouvrir.
    */

    public EvenementOuverturePorteCabine(long d) {
	super(d);
    }

    public void afficheDetails(StringBuilder buffer, Immeuble immeuble) {
	buffer.append("OPC");
    }

    public void traiter(Immeuble immeuble, Echeancier echeancier) {
		Cabine cabine = immeuble.cabine;
		assert ! cabine.porteOuverte;
	
		int i = 0;
		cabine.porteOuverte = true;
		
		if(cabine.passagersVeulentDescendre() && cabine.porteOuverte ) {
			i = cabine.faireDescendrePassagers(immeuble, this.date);
		}
		
		cabine.calculerIntention(this.date, immeuble);
		
		i += cabine.monterPassagers(echeancier);
		
		echeancier.ajouter(new EvenementFermeturePorteCabine(date + i*tempsPourEntrerOuSortirDeLaCabine + tempsPourOuvrirOuFermerLesPortes));
			
		assert cabine.porteOuverte;
    }
}

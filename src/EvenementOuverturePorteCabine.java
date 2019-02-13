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
		Etage étage = cabine.étage;
		assert ! cabine.porteOuverte;
	
		
		int i = 0;
		cabine.porteOuverte = true;
		while(cabine.porteOuverte && (! cabine.cabinePleine()) && (étage.aDesPassagers()) && (i < étage.nbPassagersEtage(étage.numéro()))) {
			assert étage.getPremierPassager() != null;
			boolean rep = cabine.faireMonterPassager(étage.getPremierPassager());
			if(rep)
				étage.suppPremierPassager();
			i++;
			System.out.println(i);
		}
		
		echeancier.ajouter(new EvenementFermeturePorteCabine(date + i*tempsPourEntrerOuSortirDeLaCabine + tempsPourOuvrirOuFermerLesPortes));
		
		assert cabine.porteOuverte;
    }

}

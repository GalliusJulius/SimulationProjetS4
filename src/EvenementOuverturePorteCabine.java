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
		
		if(cabine.passagersVeulentDescendre() && cabine.porteOuverte ) {
			i = cabine.faireDescendrePassagers(immeuble, this.date);
		}
		
		// Améliore le nombre de passagers pris (comparé sur une longue période)
		if(Global.isModeParfait()) {
			if(cabine.getPremierPassager(this.date) != null)
				cabine.changerIntention(cabine.getPremierPassager(this.date).sens());
			else {
				int dessus = immeuble.passagerAuDessus(étage);
				int dessous = immeuble.passagerEnDessous(étage);
				if(dessus == -1 && dessous == -1 && étage.aDesPassagers()) {
					cabine.changerIntention(étage.meilleurIntention(cabine));
				}
				else {
					if(dessus == -1 || dessous ==-1) {
						if(dessus == -1) 
							cabine.changerIntention('v');
						else if(dessous == -1) 
							cabine.changerIntention('^');
						else if(dessus < dessous) 
							cabine.changerIntention('^');
						
						else 
							cabine.changerIntention('v');
					}
				else if(dessus == -1 && dessous==-1) {
					if((cabine.intention() == '^') && !passagerVeulentDescendre(cabine.intention(),cabine)) {
						cabine.changerIntention('v');
					} else if((cabine.intention() == 'v') && !passagerVeulentDescendre(cabine.intention(),cabine)) {
						cabine.changerIntention('^');
					}
				}
				}
			}
		} else {
			int dessus = immeuble.passagerAuDessus(étage);
			int dessous = immeuble.passagerEnDessous(étage);
			if((cabine.intention() == '^') && (dessus == -1) && !passagerVeulentDescendre(cabine.intention(),cabine)) {
				cabine.changerIntention('v');
			} else if((cabine.intention() == 'v') && (dessous == -1) && !passagerVeulentDescendre(cabine.intention(),cabine)) {
				cabine.changerIntention('^');
			}
		}
		
		if(!Global.isModeParfait()) {
			while(cabine.porteOuverte && (! cabine.cabinePleine()) && (étage.aDesPassagers())) {
				//System.out.println( cabine.getPremierPassager()+" | "+ cabine.porteOuverte + " | "+ ! cabine.cabinePleine()+ " | "+étage.aDesPassagers()+ " | "+(i < étage.nbPassagersEtage(étage.numéro())));
				assert étage.getPremierPassager() != null;
				long num = étage.getPremierPassager().getNumCrea();
				boolean rep = cabine.faireMonterPassager(étage.getPremierPassager());
				if(rep) {
					echeancier.enleverArriverPalier(num);
					étage.suppPremierPassager();
				}
				i++;
			}
		}
		else {
			for(Passager p : étage.getPassagerIntention(cabine.intention())) {
				assert étage.getPremierPassager() != null;
				long num = p.getNumCrea();
				boolean rep = cabine.faireMonterPassager(p);
				if(rep) {
					echeancier.enleverArriverPalier(num);
					étage.supprimerPassager(num);
				}
				i++;
			}
		}
		echeancier.ajouter(new EvenementFermeturePorteCabine(date + i*tempsPourEntrerOuSortirDeLaCabine + tempsPourOuvrirOuFermerLesPortes));
			
		assert cabine.porteOuverte;
    }
    

    public boolean passagerVeulentDescendre(char intent,Cabine cabine) {
    	boolean res = false;
    	for(int i = 0 ; i<cabine.tableauPassager.length;i++) {
    		Passager p = cabine.tableauPassager[i];
    		if(p!=null) {
	    		if(intent == '^' && p.étageDestination().numéro()>cabine.étage.numéro()) return true;
	    		else if(intent == 'v' && p.étageDestination().numéro() <cabine.étage.numéro()) return true;
    		}
    	}
    	return res;
    }

}

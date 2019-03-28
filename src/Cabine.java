public class Cabine extends Global {
    /* Dans cette classe, vous pouvez ajouter/enlever/modifier/corriger les méthodes, mais vous ne
       pouvez pas ajouter des attributs (variables d'instance).
    */
    
    public Etage étage; // actuel, là ou se trouve la Cabine, jamais null.

    public boolean porteOuverte;

    private char intention; // 'v' ou '^'

    public Passager[] tableauPassager;
    /* Ceux qui sont actuellement dans la Cabine. On ne décale jamais les élements.
       Comme toute les collections, il ne faut pas l'exporter.
       Quand on cherche une place libre, on fait le parcours de la gauche vers la droite.
     */

    public Cabine(Etage e) {
		assert e != null;
		étage = e;
		tableauPassager = new Passager[nombreDePlacesDansLaCabine];
		porteOuverte = false;
		intention = 'v';
    }

    public void afficheDans(StringBuilder buffer) {
		buffer.append("Contenu de la cabine: ");
		for (Passager p: tableauPassager) {
		    if (p == null) {
			buffer.append("null");		
		    } else {
			p.afficheDans(buffer);
		    }
		    buffer.append(' ');
		}
		assert (intention == 'v') || (intention == '^');
		buffer.append("\nIntention de la cabine: " + intention);
    }

    /* Pour savoir si le passager p est bien dans la Cabine.
       Attention, c'est assez lent et c'est plutôt une méthode destinée à être 
       utilisée les asserts.
    */
    public boolean transporte(Passager p) {
		assert p != null;
		for (int i = tableauPassager.length - 1 ; i >= 0  ; i --) {
		    if (tableauPassager[i] == p) {
			return true;
		    }
		}
		return false;
    }

    public char intention() {
		assert (intention == 'v') || (intention == '^');
		return intention;
    }

    public void changerIntention(char s){
		assert (s == 'v') || (s == '^');
		intention = s;
    }

    public boolean faireMonterPassager(Passager p) { 
		assert p != null;
		assert ! transporte(p);
		for (int i=0 ; i<tableauPassager.length ; i++) {
		    if(tableauPassager[i]==null){
			tableauPassager[i]=p;
			return true;
		    }
		}
		return false;
    }

    public int faireDescendrePassagers(Immeuble immeuble,long d){
		int c=0;
		int i=tableauPassager.length-1;
		while(i>=0){
		    if(tableauPassager[i]!=null){
			assert transporte(tableauPassager[i]);
			if(tableauPassager[i].étageDestination() == étage){
				//ajout : on enleve le pap associé
			    immeuble.ajouterCumul(d-tableauPassager[i].dateDépart());
			    immeuble.nombreTotalDesPassagersSortis++;
			    tableauPassager[i]=null; 
			    c++;
			}
		    }
		    i--;
		}
		return c;
    }

    public boolean passagersVeulentDescendre(){
		int i=tableauPassager.length-1;
		while(i>=0){
		    if(tableauPassager[i]!=null){
				assert transporte(tableauPassager[i]);
				if(tableauPassager[i].étageDestination() == étage){
				    return true;
				}
		    }
		    i--;
		}
		return false;
    }
	
    
    public int nbPassagersDansCabine() {
    	int nb = 0;
    	
    	for(int i = 0; i < tableauPassager.length; i++) {
    		if(tableauPassager[i] != null) {
    			nb++;
    		}
    	}
    	
    	return nb;
    }
    
    public boolean cabinePleine() {
    	return this.nbPassagersDansCabine() == tableauPassager.length;
    }
    
    public int nbPassagersVeulentDescendre(int numEtage) {
    	int nbP = 0;
    	
    	for(int i = 0; i < tableauPassager.length; i++) {
    		if((tableauPassager[i] != null) && (tableauPassager[i].numéroDestination() == numEtage)) {
    			nbP++;
    		}
    	}
    	
    	return nbP;
    }
    
    public int nbPassagersPalier(int numEtage) {
    	int nbP = 0;
    	
    	for(int i = 0; i < tableauPassager.length; i++) {
    		if((tableauPassager[i].numéroDepart() == numEtage) && (! this.cabinePleine())) {
    			nbP++;
    		}
    	}
    	
    	return nbP;
    }
    
    /**
     * Permet d'obtenir le passager entré en dernier dans la cabine (celui qui a attendu le plus longtemps)
     * @param date
     * @return
     */
    public Passager getPremierPassager(long date) {
    	int i, numP = 0;
    	long dateMax = 0;
    	for(i = 0; i < tableauPassager.length; i++) {
    		if(tableauPassager[i] != null) {
    			if(date - tableauPassager[i].dateDépart() > dateMax) {
    				dateMax = date - tableauPassager[i].dateDépart();
    				numP = i;
    			}
    		} 
    	}
    	
    	return tableauPassager[numP];
    }

	public Passager getPassager(int num) {
		return tableauPassager[num];
	}
	
	
	/**
	 * Permet de savoir si tout les passagers dans la cabine ont la même intention : ' ' si pas de passager et '-' si les passagers n'ont pas la même intention.
	 * @return
	 */
	public char intentionUnie() {
		char res = ' ';
		
		for(int i = 0; i < tableauPassager.length; i++) {
    		if(tableauPassager[i] != null) {
    			if(((res != ' ') && (tableauPassager[i].sens() == res)) || res == ' ') {
    				res = tableauPassager[i].sens();
    			} else if((res != ' ') && (tableauPassager[i].sens() != res)) {
    				return '-';
    			}
    		} 
    	}
		
		return res;
	}
	
	
	/**
	 * Permet de calculer l'intention de la cabine en fonction du mode est des conditions de l'immeuble
	 * 
	 * @param date
	 * @param immeuble
	 */
	public void calculerIntention(long date, Immeuble immeuble) {
		if(Global.isModeParfait()) {
			// On regarde l'intention du premier passager dans la cabine
			if(this.getPremierPassager(date) != null) {
				this.changerIntention(this.getPremierPassager(date).sens());
			}
			else {
				
				int dessus = immeuble.passagerAuDessus(étage);
				int dessous = immeuble.passagerEnDessous(étage);
				
				// Cas où il n'y a pas de passager ni au-dessus ni en-dessous et que l'etage courant à des passagers 
				if(dessus == -1 && dessous == -1 && étage.aDesPassagers()) {
					this.changerIntention(étage.meilleurIntention(this));
				}
				// Cas où il n'y a pas de passager ni au-dessus ni en-dessous
				else if(dessus == -1 || dessous ==-1) {
					if((!étage.aDesPassagers()||this.nbPassagersDansCabine()==0) && (!étage.memeIntention(this.intention)) && !((dessus == -1) && (dessous == -1))) {
						if(dessus == -1) 
							this.changerIntention('v');
						else if(dessous == -1) 
							this.changerIntention('^');
						else if(dessus < dessous) 
							this.changerIntention('^');
						else 
							this.changerIntention('v');
					
					// Cas spécifique : si tout en bas ou tout en haut de l'immeuble et aucun passager dans l'immeuble et dans la cabine
					} else if(((!étage.aDesPassagers()) && (this.nbPassagersDansCabine() == 0))
						&& (((this.intention() == 'v') && (this.étage.numéro() == immeuble.étageLePlusBas().numéro())) || ((this.intention() == '^') && (this.étage.numéro() == immeuble.étageLePlusHaut().numéro())))) {
							if((this.intention() == 'v') && (this.étage.numéro() == immeuble.étageLePlusBas().numéro()))
								this.changerIntention('^');
							else
								this.changerIntention('v');
					}
				}
			}
		} else {
			
			if(!(étage.aDesPassagers() && étage.memeIntention(this.intention()))) {
				int dessus = immeuble.passagerAuDessus(étage);
				int dessous = immeuble.passagerEnDessous(étage);
				
				// Cas spécifique à exclure : l'étage n'a pas de passager, il n'y a pas de passager ni au-dessus ni en-dessous et il n'y a personne dans la cabine (dans ce cas là on ne change pas l'intention)
				if (! ((!étage.aDesPassagers()) && (dessus == -1) && (dessous == -1) && (this.nbPassagersDansCabine() == 0))) {
					if((this.intention() == '^') && (dessus == -1) && !passagerVeulentDescendre()) {
						this.changerIntention('v');
					} else if((this.intention() == 'v') && (dessous == -1) && !passagerVeulentDescendre()) {
						this.changerIntention('^');
					}
				} else {
					// Sinon on change l'intention, si la cabine est tout en bas ou tout en haut de l'immeuble
					if((this.intention() == 'v') && (this.étage.numéro() == immeuble.étageLePlusBas().numéro()))
						this.changerIntention('^');
					else if((this.intention() == '^') && (this.étage.numéro() == immeuble.étageLePlusHaut().numéro()))
						this.changerIntention('v');
				}
				
			}
		}
		
	}
	
	
	/**
	 * Permet de calculer le nombre de passager voulant descendre de la cabine pour l'étage en cours pour le mode parfait.
	 * 
	 * @return le nombre de passager voulant descendre
	 */
	public boolean passagerVeulentDescendre() {
    	boolean res = false;
    	for(int i = 0 ; i<this.tableauPassager.length;i++) {
    		Passager p = this.tableauPassager[i];
    		if(p!=null) {
	    		if((this.intention == '^') && (p.étageDestination().numéro() > this.étage.numéro())) return true;
	    		else if((this.intention == 'v') && (p.étageDestination().numéro() < this.étage.numéro())) return true;
    		}
    	}
    	return res;
    }

	/**
	 * Permet de faire monter les passagers dans la cabine en fonction du mode (enlève l'événement ).
	 * 
	 * @param echeancier
	 * @return le nombre de passger qui sont monter dans la cabine.
	 */
	public int monterPassagers(Echeancier echeancier) {
		int i = 0;
		
		if(!Global.isModeParfait()) {
			while(this.porteOuverte && (! this.cabinePleine()) && (étage.aDesPassagers())) {
				assert étage.getPremierPassager() != null;
				long num = étage.getPremierPassager().getNumCrea();
				boolean rep = this.faireMonterPassager(étage.getPremierPassager());
				if(rep) {
					echeancier.enleverArriverPalier(num);
					étage.suppPremierPassager();
				}
				i++;
			}
		}
		else {
			for(Passager p : étage.getPassagerIntention(this.intention())) {
				assert p != null;
				long num = p.getNumCrea();
				boolean rep = this.faireMonterPassager(p);
				if(rep) {
					echeancier.enleverArriverPalier(num);
					étage.supprimerPassager(num);
				}
				i++;
			}
		}
		
		return i;
	}
}

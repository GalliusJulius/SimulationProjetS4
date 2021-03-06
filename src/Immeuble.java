public class Immeuble extends Global {
    /* Dans cette classe, vous pouvez ajouter/enlever/modifier/corriger les méthodes, mais vous ne
       pouvez pas ajouter des attributs (variables d'instance).
    */
    
    private Etage[] tableauDesEtages;
    /* Les étages, dans l'ordre de leur numérotation. Aucun null dans ce tableau.
       Comme toute les collections, il ne faut pas l'exporter.
    */

    public Cabine cabine; // de l'ascenseur.

    private Etage niveauDuSol; // le niveau 0 en général.

    public long cumulDesTempsDeTransport = 0;
    
    public long nombreTotalDesPassagersSortis = 0;

    public Etage étageLePlusBas() {
	Etage res = tableauDesEtages[0];
        assert res != null;
        return res ;
    }

    public Etage étageLePlusHaut() {
	Etage res = tableauDesEtages[tableauDesEtages.length - 1];
        assert res != null;
        return res;
    }

    public Etage niveauDuSol() {
        assert étageLePlusHaut().numéro() >= niveauDuSol.numéro();
        assert étageLePlusBas().numéro() <= niveauDuSol.numéro();
        return niveauDuSol;
    }

    public Immeuble(Echeancier echeancier) {
	Etage e = null;
        tableauDesEtages = new Etage[9];
        int n = -1;
        for (int i = 0; i < tableauDesEtages.length; i++) {
            int fa = 41; // Pour le niveau 0 en dixieme de secondes:
            if (n != 0) {
                fa = fa * (tableauDesEtages.length - 1);
            }
	    e = new Etage(n, fa, this);
	    tableauDesEtages[i] = e;
            if (n == 0) {
                niveauDuSol = e;
            }
            n++;
        }
        for (int i = 0; i < tableauDesEtages.length; i++) {
            Etage etage = tableauDesEtages[i];
            long date = etage.arrivéeSuivante();
            echeancier.ajouter(new EvenementArriveePassagerPalier(date, etage));
        }
	e = étageLePlusHaut();
        cabine = new Cabine(e);
	e = étage(e.numéro() - 1);
	echeancier.ajouter(new EvenementPassageCabinePalier(tempsPourBougerLaCabineDUnEtage,e));
    }

    public void affiche(StringBuilder buffer) {
        System.out.print("Immeuble (mode ");
	System.out.print(modeParfait ? "parfait" : "infernal");
        System.out.println("):");
        int i = étageLePlusHaut().numéro();
        while (i >= étageLePlusBas().numéro()) {
	    buffer.setLength(0);
            étage(i).afficheDans(buffer);
	    System.out.println(buffer);
            i--;
        }
	buffer.setLength(0);
        cabine.afficheDans(buffer);
	buffer.append("\nCumul des temps de transport: ");
	buffer.append(cumulDesTempsDeTransport);
	buffer.append("\nNombre total des passagers sortis: ");
	buffer.append(nombreTotalDesPassagersSortis);
        buffer.append("\nRatio cumul temps / nb passagers : ");
	buffer.append(nombreTotalDesPassagersSortis == 0 ? 0 : cumulDesTempsDeTransport / nombreTotalDesPassagersSortis);
	System.out.println(buffer);
    }

    public Etage étage(int i) {
        assert étageLePlusBas().numéro() <= i : "trop bas" + i;
        assert étageLePlusHaut().numéro() >= i : "trop haut" + i;
        Etage res = tableauDesEtages[i - étageLePlusBas().numéro()];
        assert res.numéro() == i;
        return res;
    }

    public int nbEtages() {
        int res = tableauDesEtages.length;
        assert res == (étageLePlusHaut().numéro() - étageLePlusBas().numéro() + 1);
        return res;
    }
    
    public void ajouterCumul(long t){
    	cumulDesTempsDeTransport+=t;
    }
    
    public void ajouterPassagerSorti(){
    	nombreTotalDesPassagersSortis++;
    }
    
    
    //changement on retourne le nb détages de diff
    public int passagerAuDessus(Etage e){
    	assert e != null;
    	int res = 0;
    	for (int i=e.numéro()+1 ; i <= étageLePlusHaut().numéro() ; i++) {
	    Etage et = étage(i);
	    res++;
	    if(et.aDesPassagers())
		return res;
    	}
    	return -1;
    }
    
    
    public int passagerEnDessous(Etage e){
    	assert e != null;
    	int res = 0;
    	for (int i = étageLePlusBas().numéro() ; i < e.numéro() ; i++) {
	    Etage et = étage(i);
	    res++;
	    if (et.aDesPassagers())
  		return res;
     	}
    	return -1;
    }

	public Etage getEtage(int num) {
		Etage res = tableauDesEtages[num+1];
		
		return res;
	}
    
}

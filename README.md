                 
                 
                  ################################################################################
                  #                    PROJET COMPILATION PAR SIDATH GUEYE                       #
                  #                                 README                                       #
                  ################################################################################



L'ensemble du projet à été réalisé par moi même sauf l'aide reçu soit de Madame Scholivet (indication technique ou algorithmique)
ET/OU de mon collègues Alexandre WERY .


Dans le Fichier Sc2sa, la fonction  ci-dessous et son utilisation m'a  été suggérée par Alexandre WERY.

private <T> T retrieveNode(Node node){
        if(node == null)
            return null;
        node.apply(this);
        return (T)returnValue;
    }
    
Dans le Fichier  Fichier Sc2c3a, l'utilisation des deux tables et la manière du constructeur de Sa2c3a et la fonction 
ci desssous m'ont  été suggérés aussi par Alexandre WERY.

public Sa2c3a(SaNode root, Ts table){
        this.c3a = new C3a();
        this.table = table;
        root.accept(this);
    }

    private TsItemVar getVar(String name) {
        var variable = this.currentTable.getVar(name);
        return variable != null ? variable : this.table.getVar(name);
    }


J'ai reçu de l'aide d'Alexandre WERY qui m'a à chaque fois expliqué quand je l'ai voulu surtout en debut de chaque TP.





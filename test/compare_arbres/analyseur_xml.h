#ifndef __ANALYSEUR_XML__
#define __ANALYSEUR_XML__

/* structures de donnees et fonctions pour arbre abstrait */

typedef struct n noeud;

struct n{
  char *nom;
  char *texte;
  struct n *premier_fils;
  struct n *dernier_fils;
  struct n *suiv;
};

FILE *yyin;


void affiche_arbre(noeud *racine);
void libere_arbre(noeud *r);

/* analyseur */

noeud *analyseur_xml(void);
#endif

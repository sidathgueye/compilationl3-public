#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include"analyseur_xml.h"

int compare_arbres_rec(noeud *racine1, noeud *racine2, int verbose, int compare_text, int profondeur)
{
  noeud *f1, *f2;
  int resultat = 1;
  int i;
  if(verbose){
    for(i=0; i<profondeur; i++) printf(" ");
    printf("%s <------------> %s\n", racine1->nom, racine2->nom);
  }
  if(strcmp(racine1->nom, racine2->nom))
    return 0;

  if(compare_text){
    if(racine1->texte && !racine2->texte){
      if(verbose){
	for(i=0; i<profondeur; i++) printf(" ");
	printf("%s <------------> NULL\n", racine1->texte);
      }
      return 0;
    }
    
    if(!racine1->texte && racine2->texte){
      if(verbose){
	for(i=0; i<profondeur; i++) printf(" ");
	printf("NULL <------------> %s\n", racine2->texte);
      }
      return 0;
    }
    
    if(racine1->texte && racine2->texte){
      if(strcmp(racine1->texte, racine2->texte)){
	if(verbose){
	  for(i=0; i<profondeur; i++) printf(" ");
	  printf("%s <------------> %s\n", racine1->texte, racine2->texte);
	}
	return 0;
      }
    }
  }

  for(f1 = racine1->premier_fils, f2 = racine2->premier_fils;
      f1 && f2 && resultat;
      f1 = f1->suiv, f2 = f2->suiv){
    resultat = compare_arbres_rec(f1, f2, verbose, compare_text, profondeur+1);
  }
  return ((f1 == NULL) && (f2 == NULL) && resultat);
}

int compare_arbres(noeud *racine1, noeud *racine2, int verbose, int compare_text)
{
  return compare_arbres_rec(racine1, racine2, verbose, compare_text, 0);
}

void print_help_message(char *program_name)
{
  fprintf(stderr, "utilisation: %s fichier_xml fichier_xml options\n", program_name);
  fprintf(stderr, "option:\n");
  fprintf(stderr, "\t\th affiche ce message \n");
  fprintf(stderr, "\t\tv affiche le détail de la comparaison\n");
  fprintf(stderr, "\t\tt compare la valeur des noeuds terminaux et des noeuds non terminaux\n");

}


int main(int argc, char *argv[])
 {
   noeud *racine1;
   noeud *racine2;
   int resultat;
   int verbose = 0;
   int compare_text = 0;
   int i;
   
   if(argc < 3){
     print_help_message(argv[0]);
     exit(1);
   }

   for(i=3; i<argc; i++){
     if(!strcmp(argv[i], "v")) {
       verbose = 1;
     }
     else if(!strcmp(argv[i], "t")) {
       compare_text = 1;
     }
     else if(!strcmp(argv[i], "h")) {
       print_help_message(argv[0]);
       exit(1);
     }
   }
   
   
   yyin = fopen(argv[1], "r");
   if(yyin == NULL){
     fprintf(stderr, "impossible d'ouvrir le fichier %s\n", argv[1]);
     exit(1);
   }
   
   fprintf(stderr, "analyse du fichier : %s\n", argv[1]);
   racine1 = analyseur_xml();


   fclose(yyin);

   yyin = fopen(argv[2], "r");
   if(yyin == NULL){
     fprintf(stderr, "impossible d'ouvrir le fichier %s\n", argv[2]);
     exit(1);
   }
   
   fprintf(stderr, "analyse du fichier : %s\n", argv[2]);
   racine2 = analyseur_xml();

   fclose(yyin);



   /*   affiche_arbre(racine1);
   printf("\n");
   affiche_arbre(racine2);*/
   
   fprintf(stderr, "comparaison des arbres\n");
   resultat = compare_arbres(racine1, racine2, verbose, compare_text);
   libere_arbre(racine1);
   libere_arbre(racine2);


   if(resultat){ 
     fprintf(stderr,"arbres egaux\n");
     return 0;
   }
   else{ 
     fprintf(stderr,"arbres différents\n");
     return 1;
   }

 }

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include"analyseur_xml.h"

/* symboles terminaux */

#define CHEVRON_FERMANT 1
#define DEBUT_BF 2
#define DEBUT_BO 3
#define CHAINE 4
#define FIN 5

#define ISSPACE(a) (a == ' ') || (a == '\n') || (a == '\t')

#define YYTEXT_MAX 100

/* fonctions pour arbre abstrait */

noeud *cree_noeud(char *nom)
{
  noeud *n = malloc(sizeof(noeud));
  if(n == NULL){
    fprintf(stderr, "erreur d'allocation\n");
    exit(1);
  }
  n->nom = nom;
  n->texte = NULL;
  n->premier_fils = NULL;
  n->dernier_fils = NULL;
  n->suiv = NULL;
  return n;
}
void ajoute_texte(noeud *noeud, char *texte)
{
  if(noeud) noeud->texte = texte;
}

noeud *ajoute_fils(noeud *pere, noeud *fils)
{
  if(pere->premier_fils == NULL){
    pere->premier_fils = pere->dernier_fils = fils;
    return pere;
  }
  pere->dernier_fils->suiv = fils;
  pere->dernier_fils = fils;
  return pere;

}

void affiche_arbre(noeud *racine){
  noeud *n;
  printf("<%s> ", racine->nom);
  for(n=racine->premier_fils; n!=NULL; n = n->suiv){
    affiche_arbre(n);
  }
  printf("</%s> ", racine->nom);
}



void libere_arbre(noeud *racine)
{
  noeud *n;
  for(n=racine->premier_fils; n!=NULL; n = n->suiv){
    libere_arbre(n);
  }
  free(racine);
}

/* analyseur lexical */

int DEBUG = 0;
char yytext[YYTEXT_MAX];
int yyleng;
int yylval;
/* Compter les lignes pour afficher les messages d'erreur avec numero ligne */
int nb_ligne;
int cc;

/*******************************************************************************
 * Fonction qui ignore les espaces et commentaires. 
 * Renvoie -1 si arrivé à la fin du fichier, 0 si tout va bien 
 ******************************************************************************/
int mangeEspaces()
{ 
  char c = fgetc(yyin);
  while( ISSPACE(c) ) {    
    if( c == '\n' ) {
      nb_ligne++;
    }
    c = fgetc(yyin);    
  }
  if ( feof(yyin) ) {
    return -1;
  }    
  ungetc(c, yyin);
  return 0;
}
 
/*******************************************************************************
 * Lit un caractère et le stocke dans le buffer yytext 
 ******************************************************************************/
char lireCar(void)
{
  yytext[yyleng++] = fgetc(yyin);
  yytext[yyleng] = '\0';
  return yytext[yyleng - 1];
}
  
/*******************************************************************************
 * Remet le dernier caractère lu au buffer clavier et enlève du buffer yytext 
 ******************************************************************************/
void delireCar()
{
  char c;
  c = yytext[yyleng - 1];
  yytext[--yyleng] = '\0';
  ungetc(c, yyin);
}
/*******************************************************************************
 * Fonction principale de l'analyseur lexical, lit les caractères de yyin et
 * renvoie les tokens sous forme d'entier. Le code de chaque unité est défini 
 * dans symboles.h sinon (mot clé, idententifiant, etc.). Pour les tokens de 
 * type ID_FCT, ID_VAR et NOMBRE la valeur du token est dans yytext, visible 
 * dans l'analyseur syntaxique.
 ******************************************************************************/
int inTag = 0;

int yylex(void)
{
  char c;  
  yytext[yyleng = 0] = '\0';
 
  /* Si j'ai trouvé la fin du fichier en lisant des espaces ... */
  if( !inTag && mangeEspaces() == -1 ) {
    return FIN; /* Renvoie le marqueur de fin d'entrée */
  }  
  
  c = lireCar(); 
  if(c == '>'){
    if(DEBUG) { printf("CHEVRON_FERMANT\n"); }
    //inTag = 0;
    return CHEVRON_FERMANT;
  }
  if(c == '<'){
    inTag = 1;
    c = lireCar(); 
    if(c == '/'){
      if(DEBUG) { printf("DEBUT_BF\n"); }
      return DEBUT_BF;
    }
    else{
      delireCar();
      if(DEBUG) { printf("DEBUT_BO\n"); }
      return DEBUT_BO;
    }
  }
  do{
    c = lireCar(); 
    inTag = 0;
  }while((c!= '<') && (c!= '>') && !feof(yyin) && !(ISSPACE(c)));
  delireCar();
  if(DEBUG) { printf("CHAINE\n"); }

  return CHAINE;
 }

 
  /*******************************************************************************
   * Fonction auxiliaire appelée par l'analyseur syntaxique tout simplement pour 
   * afficher des messages d'erreur et l'arbre XML 
   ******************************************************************************/
void nom_token( int token, char *nom, char *valeur ) {
  //int i;
  
  strcpy( nom, "symbole" );
  if(token == CHEVRON_FERMANT) strcpy( valeur, "CHEVRON_FERMANT");
  else if(token == DEBUT_BF) strcpy( valeur, "DEBUT_BF");
  else if(token == DEBUT_BO) strcpy( valeur, "DEBUT_BO");
  else if(token == CHAINE) strcpy( valeur, "CHAINE");
}

/* analyse syntaxique */

noeud *B(void);
void LB(noeud *pere);
char *BO(void);
char *BF(void);
char *LCHAINE(void);

void erreur(char *msg){
  fprintf(stderr, "Ligne %d : erreur : %s\n", nb_ligne, msg);
  exit(1);
}



/* B -> BO LCHAINE LB LCHAINE BF */
noeud *B(void){
  char *nom_bo;
  char *nom_bf;
  noeud *n;
  char *texte = NULL;
  if(DEBUG) { printf("<B>\n"); }
  if(cc == DEBUT_BO){
    nom_bo = BO();
    n = cree_noeud(nom_bo);
    texte = LCHAINE();
    ajoute_texte(n, texte);
    LB(n);
    LCHAINE();
    nom_bf = BF();
    if(strcmp(nom_bo, nom_bf)){
      fprintf(stderr, "Ligne %d : arbre mal forme: balise ouvrante : \"%s\" balise fermante : \"%s\"\n", nb_ligne, nom_bo, nom_bf);
      exit(1);
    }
    free(nom_bf);
    if(DEBUG) { printf("</B>\n"); }
    return n;
  }
  erreur("B");
  return NULL;  
}

/* LCHAINE -> CHAINE LCHAINE */
/*         | */

char *LCHAINE(void){
  if(cc == CHAINE){
    char * texte = strdup(yytext);
    cc = yylex();
    LCHAINE();
    return texte;
  }
  if((cc == DEBUT_BO) ||(cc == DEBUT_BF)){
    return NULL;
  }
  erreur("LCHAINE");
  return NULL;
}

/* LB -> B LB 
       | */

void LB(noeud *pere)
{
  noeud *fils = NULL;
  if(cc == DEBUT_BO){
    fils = B();
    ajoute_fils(pere, fils);
    LB(pere);
    return;
  }
  if(cc == DEBUT_BF){
    return;
  }
  erreur("LB");
}

/* BO -> DEBUT_BO CHAINE CHEVRON_FERMANT */
char *BO(void)
{
  char *nom = NULL;
  if(DEBUG) { printf("<BO>\n"); }
  if(cc == DEBUT_BO){
    cc = yylex();
    if(cc == CHAINE){
      nom = strdup(yytext);
      cc = yylex();
      if(cc == CHEVRON_FERMANT){
	cc = yylex();
	if(DEBUG) { printf("</BO>\n"); }
	return nom;
      }
    }
  }
  erreur("BO");
  return NULL;  
}

/* BF -> DEBUT_BF CHAINE CHEVRON_FERMANT */
char *BF(void)
{
  char *nom = NULL;
  if(cc == DEBUT_BF){
    cc = yylex();
    if(cc == CHAINE){
      nom = strdup(yytext);
      cc = yylex();
      if(cc == CHEVRON_FERMANT){
	cc = yylex();
	return nom;
      }
    }
  }
  erreur("BF");
  return NULL;
}

noeud *analyseur_xml(void)
{
  nb_ligne = 0;
  cc = yylex();
  return B();
}



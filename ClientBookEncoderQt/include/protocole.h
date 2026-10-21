#ifndef PROTOCOLE_H
#define PROTOCOLE_H

#define CLE 1234

// ============================================================================
//  PROTOCOLE TEXTE (compatible client C++/Qt et client Java)
// ============================================================================
//
//  Trame  : XXXX<contenu>
//           XXXX    = longueur du contenu EN OCTETS (UTF-8), 4 chiffres ASCII, ex : "0023"
//           contenu = REQUETE#champ1#champ2#...      (séparateur de champs : '#')
//
//  Les champs ne doivent donc pas contenir le caractère '#'.
//  Les listes (GET_*) sont des champs dont les éléments sont séparés par ';'
//  (chaque élément est suivi d'un ';', ex : "1;2;3;").
//
//  C = Client, S = Serveur
//
//  Requête (C -> S)                                  Réponse (S -> C)
//  ------------------------------------------------  -----------------------------------------------
//  LOGIN#login#password                              LOGIN#ok            | LOGIN#ko#raison
//  LOGOUT#login                                      (pas de réponse)
//  GET_AUTHORS                                       GET_AUTHORS#ok#ids#noms#prenoms | GET_AUTHORS#ko#raison
//  GET_SUBJECTS                                      GET_SUBJECTS#ok#ids#noms        | GET_SUBJECTS#ko#raison
//  ADD_AUTHOR#nom#prenom#dateNaissance(yyyy-mm-dd)   ADD_AUTHOR#ok#message  | ADD_AUTHOR#ko#raison
//  ADD_SUBJECT#nom                                   ADD_SUBJECT#ok#message | ADD_SUBJECT#ko#raison
//  ADD_BOOK#titre#isbn#auteur#sujet#pages#annee#prix#stock
//                                                    ADD_BOOK#ok#message    | ADD_BOOK#ko#raison
//
//  "auteur" = "Nom Prénom" tel qu'affiché dans la liste ; "prix" avec '.' comme séparateur décimal.
// ============================================================================

#define LOGIN        "LOGIN"
#define LOGOUT       "LOGOUT"
#define GET_AUTHORS  "GET_AUTHORS"
#define GET_SUBJECTS "GET_SUBJECTS"
#define ADD_AUTHOR   "ADD_AUTHOR"
#define ADD_SUBJECT  "ADD_SUBJECT"
#define ADD_BOOK     "ADD_BOOK"

#define STATUT_OK "ok"
#define STATUT_KO "ko"

#define SEPARATEUR_CHAMP '#'
#define SEPARATEUR_LISTE ';'

#define TAILLE_ENTETE    4      // "XXXX"
#define TAILLE_MAX_TRAME 9999   // taille maximale du contenu (limite des 4 chiffres)
#define MAX_CHAMPS       16     // nombre maximum de champs d'une trame

#endif // PROTOCOLE_H

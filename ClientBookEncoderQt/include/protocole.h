#ifndef PROTOCOLE_H
#define PROTOCOLE_H

#define CLE 1234
// C : Client, S : Serveur
//      requete             sens            data1           data2           texte


#define LOGIN 1         // C -> S          1 ou 0          login           password
//                         S -> C       "OK" ou "KO"                       erreur
#define LOGOUT 2        // C -> S
#define GET_AUTHORS 3   // C -> S
#define GET_SUBJECTS 4  // C -> S
#define ADD_AUTHOR 5    // C -> S          lastName        firstName       
#define ADD_SUBJECT 6   // C -> S         name
#define ADD_BOOK 7      // C -> S 
// authorId, subjectId, title, isbn, pageCount, stockQuantity, price, publishYear 


typedef struct {
    long type;
    int expediteur;
    int requete;
    char* data1;    // pas de taille fixe, mettre XXXX avant chaque envoi de message pour indiquer la taille du message
    char* data2;    // utilisation de strlen pour calculer la taille du message
    char* texte;    // à l'envoi retirer les 4 premiers caractères de data1, data2 et texte pour mettre la taille du message dans une variable
} MESSAGE;

#endif // PROTOCOLE_H
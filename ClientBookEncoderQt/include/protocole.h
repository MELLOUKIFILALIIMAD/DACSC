#ifndef PROTOCOLE_H
#define PROTOCOLE_H

#define CLE 1234
// C : Client, S : Serveur
//      requete         sens            data1           data2           texte


#define CONNECT 1    // C -> S
#define DISCONNECT 2 // C -> S
#define LOGIN 3      // C -> S          1 ou 0          login           password
//                      S -> C       "OK" ou "KO"
#define LOGOUT 4     // C -> S



typedef struct {
    long type;
    int expediteur;
    int requete;
    char* data1;    // pas de taille fixe, mettre XXXX avant chaque envoi de message pour indiquer la taille du message
    char* data2;    // utilisation de strlen pour calculer la taille du message
    char* texte;    // à l'envoi retirer les 4 premiers caractères de data1, data2 et texte pour mettre la taille du message dans une variable
} MESSAGE;

#endif // PROTOCOLE_H
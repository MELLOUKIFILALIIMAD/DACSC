#ifndef SOCKET_H
#define SOCKET_H

#include "protocole.h"

int Socket();
void Bind(int socket, int port);
void Listen(int socket);
int Accept(int socket);
void Connect(int socket, const char *ip, int port);

// --- Trames texte (voir protocole.h pour le format) ---
int SendTrame(int socket, const char *contenu);                    // 0 = ok, -1 = erreur
int ReceiveTrame(int socket, char *contenu, int tailleMax);        // 1 = ok, 0 = déconnexion, -1 = erreur
int Echange(int socket, const char *requete, char *reponse, int tailleReponse); // client : envoi + réponse
int Decouper(char *contenu, char *champs[], int max);              // "A#B#C" -> champs[] (retourne le nombre)

void Close(int socket);

#endif

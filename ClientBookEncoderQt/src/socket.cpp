#include "socket.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
int Socket()
{
    int idSocket = socket(AF_INET, SOCK_STREAM, 0);

    if (idSocket == -1)
    {
        perror("(SOCKET) Erreur de socket");
        exit(EXIT_FAILURE);
    }

    return idSocket;
}


void Bind(int socket, int port)
{
    struct sockaddr_in adresse;

    memset(&adresse, 0, sizeof(adresse));

    adresse.sin_family = AF_INET;
    adresse.sin_addr.s_addr = INADDR_ANY;
    adresse.sin_port = htons(port);

    if (bind(socket,(struct sockaddr *)&adresse,sizeof(adresse)) == -1)
    {
        perror("(SOCKET) Erreur de bind");
        exit(EXIT_FAILURE);
    }
}


void Listen(int socket)
{
    if (listen(socket, 10) == -1)
    {
        perror("(SOCKET) Erreur de listen");
        exit(EXIT_FAILURE);
    }
}


int Accept(int socket)
{
    struct sockaddr_in adresseClient;
    socklen_t taille = sizeof(adresseClient);

    int socketClient = accept(socket,(struct sockaddr *)&adresseClient,&taille);

    if (socketClient == -1)
    {
        perror("(SOCKET) Erreur de accept");
        exit(EXIT_FAILURE);
    }

    return socketClient;
}


void Connect(int socket, const char *ip, int port)
{
    struct sockaddr_in adresse;

    memset(&adresse, 0, sizeof(adresse));

    adresse.sin_family = AF_INET;
    adresse.sin_port = htons(port);

    if (inet_pton(AF_INET, ip, &adresse.sin_addr) <= 0)
    {
        perror("(SOCKET) Erreur d'adresse IP");
        exit(EXIT_FAILURE);
    }

    if (connect(socket,(struct sockaddr *)&adresse,sizeof(adresse)) == -1)
    {
        perror("(SOCKET) Erreur de connect");
        exit(EXIT_FAILURE);
    }
}


int SendAll(int socket, const char *buffer, int taille)
{
    int total = 0;

    while (total < taille)
    {
        int resultat = send(socket,buffer + total,taille - total,MSG_NOSIGNAL);

        if (resultat <= 0)
        {
            return resultat;
        }

        total += resultat;
    }

    return total;
}


int ReceiveAll(int socket, char *buffer, int taille)
{
    int total = 0;

    while (total < taille)
    {
        int resultat = recv(socket,buffer + total,taille - total,0);
 
        if (resultat <= 0)
        {
            return resultat;
        }

        total += resultat;
    }

    return total;
}

int SendTrame(int socket, const char *contenu)
{
    int taille = (int) strlen(contenu);

    if (taille <= 0 || taille > TAILLE_MAX_TRAME)
    {
        fprintf(stderr, "(SOCKET) Trame invalide (taille %d, max %d)\n", taille, TAILLE_MAX_TRAME);
        return -1;
    }

    // Entête + contenu dans un seul buffer => un seul send()
    char buffer[TAILLE_ENTETE + TAILLE_MAX_TRAME + 1];

    snprintf(buffer, sizeof(buffer), "%0*d%s", TAILLE_ENTETE, taille, contenu);

    if (SendAll(socket, buffer, TAILLE_ENTETE + taille) <= 0)
    {
        return -1;
    }

    return 0;
}

int ReceiveTrame(int socket, char *contenu, int tailleMax)
{
    char entete[TAILLE_ENTETE + 1];

    int result = ReceiveAll(socket, entete, TAILLE_ENTETE);

    if (result == 0)
    {
        return 0; // Deconnexion
    }

    if (result < 0)
    {
        return -1; // Erreur
    }

    for (int i = 0; i < TAILLE_ENTETE; i++)
    {
        if (entete[i] < '0' || entete[i] > '9')
        {
            fprintf(stderr, "(SOCKET) Entete de trame invalide\n");
            return -1;
        }
    }

    entete[TAILLE_ENTETE] = '\0';

    int taille = atoi(entete);

    if (taille <= 0 || taille >= tailleMax)
    {
        fprintf(stderr, "(SOCKET) Taille de trame invalide : %d\n", taille);
        return -1;
    }

    if (ReceiveAll(socket, contenu, taille) <= 0)
    {
        return -1;
    }

    contenu[taille] = '\0';

    return 1;
}

int Echange(int socket, const char *requete, char *reponse, int tailleReponse)
{
    if (SendTrame(socket, requete) == -1)
    {
        return -1;
    }

    return ReceiveTrame(socket, reponse, tailleReponse);
}

int Decouper(char *contenu, char *champs[], int max)
{
    int nb = 0;
    char *debut = contenu;

    while (nb < max)
    {
        champs[nb++] = debut;

        char *separateur = strchr(debut, SEPARATEUR_CHAMP);

        if (separateur == NULL)
        {
            break;
        }

        *separateur = '\0';
        debut = separateur + 1;
    }

    return nb;
}


void Close(int socket)
{
    close(socket);
}
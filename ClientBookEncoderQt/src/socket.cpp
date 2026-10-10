#include "socket.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
int nbThreads = 0;
int port = 0;

void LireConfiguration()
{
    FILE *fichier = fopen("../configuration.txt", "r");
    if (fichier == NULL)
    {
        perror("Erreur ouverture configuration.txt");
        exit(1);
    }

    if (fscanf(fichier, " NB_THREADS = %d", &nbThreads) != 1 ||
        fscanf(fichier, " PORT_ENCODING = %d", &port) != 1)
    {
        fprintf(stderr, "Format de configuration.txt invalide\n");
        fclose(fichier);
        exit(1);
    }

    fclose(fichier);
}
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
        int resultat = send(socket,buffer + total,taille - total,0);

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

int Send(int socket, MESSAGE *message)
{
    if (SendAll(socket, (char *)&message->type, sizeof(long)) <= 0)
    {
        return -1;
    }

    if (SendAll(socket, (char *)&message->expediteur, sizeof(int)) <= 0)
    {
        return -1;
    }

    if (SendAll(socket, (char *)&message->requete, sizeof(int)) <= 0)
    {
        return -1;
    }

    char taille[5];

    int tailleData1 = 0;
    int tailleData2 = 0;
    int tailleTexte = 0;

    if (message->data1 != NULL)
    {
        tailleData1 = strlen(message->data1);
    }

    if (message->data2 != NULL)
    {
        tailleData2 = strlen(message->data2);
    }

    if (message->texte != NULL)
    {
        tailleTexte = strlen(message->texte);
    }

    sprintf(taille, "%04d", tailleData1);
    if (SendAll(socket, taille, 4) <= 0)
    {
        return -1;
    }

    if (tailleData1 > 0)
    {
        if (SendAll(socket, message->data1, tailleData1) <= 0)
        {
            return -1;
        }
    }

    sprintf(taille, "%04d", tailleData2);
    if (SendAll(socket, taille, 4) <= 0)
    {
        return -1;
    }

    if (tailleData2 > 0)
    {
        if (SendAll(socket, message->data2, tailleData2) <= 0)
        {
            return -1;
        }
    }

    sprintf(taille, "%04d", tailleTexte);
    if (SendAll(socket, taille, 4) <= 0)
    {
        return -1;
    }

    if (tailleTexte > 0)
    {
        if (SendAll(socket, message->texte, tailleTexte) <= 0)
        {
            return -1;
        }
    }

    return 0;
}

int Receive(int socket, MESSAGE *message)
{
    int result = ReceiveAll(socket, (char *)&message->type, sizeof(long));
    
    if (result == 0)
    {
        return 0; // Deconnexion
    }

    if (result < 0)
    {
        return -1; // Erreur
    }

    if (ReceiveAll(socket, (char *)&message->expediteur, sizeof(int)) <= 0)
    {
        return -1;
    }

    if (ReceiveAll(socket, (char *)&message->requete, sizeof(int)) <= 0)
    {
        return -1;
    }
    char taille[5];

    int tailleData1;
    int tailleData2;
    int tailleTexte;


    // Réception de data1

    if (ReceiveAll(socket, taille, 4) <= 0)
    {
        return -1;
    }

    taille[4] = '\0';

    tailleData1 = atoi(taille);

    message->data1 = (char *) malloc(tailleData1 + 1);

    if (message->data1 == NULL)
    {
        perror("(SOCKET) Erreur malloc data1");
        return -1;
    }

    if (tailleData1 > 0)
    {
        if (ReceiveAll(socket,message->data1,tailleData1) <= 0)
        {
            free(message->data1);
            return -1;
        }
    }

    message->data1[tailleData1] = '\0';


    // Réception de data2

    if (ReceiveAll(socket, taille, 4) <= 0)
    {
        free(message->data1);
        return -1;
    }

    taille[4] = '\0';

    tailleData2 = atoi(taille);

    message->data2 = (char *) malloc(tailleData2 + 1);

    if (message->data2 == NULL)
    {
        free(message->data1);
        perror("(SOCKET) Erreur malloc data2");
        return -1;
    }

    if (tailleData2 > 0)
    {
        if (ReceiveAll(socket,message->data2,tailleData2) <= 0)
        {
            free(message->data1);
            free(message->data2);
            return -1;
        }
    }

    message->data2[tailleData2] = '\0';


    // Réception de texte

    if (ReceiveAll(socket, taille, 4) <= 0)
    {
        free(message->data1);
        free(message->data2);
        return -1;
    }

    taille[4] = '\0';

    tailleTexte = atoi(taille);

    message->texte = (char *) malloc(tailleTexte + 1);

    if (message->texte == NULL)
    {
        free(message->data1);
        free(message->data2);
        perror("(SOCKET) Erreur malloc texte");
        return -1;
    }

    if (tailleTexte > 0)
    {
        if (ReceiveAll(socket,message->texte,tailleTexte) <= 0)
        {
            free(message->data1);
            free(message->data2);
            free(message->texte);
            return -1;
        }
    }

    message->texte[tailleTexte] = '\0';


    return 0;
}


void Close(int socket)
{
    close(socket);
}
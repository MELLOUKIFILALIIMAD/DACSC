#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <pthread.h>

#include "socket.h"
#include "protocole.h"

#define PORT 5000

int idServeur;

void HandlerSIGINT(int sig);
void *GestionClient(void *arg);
void Liberer(void* arg);


int main()
{
    // Armement du signal SIGINT
    struct sigaction A;

    A.sa_handler = HandlerSIGINT;
    sigemptyset(&A.sa_mask);
    A.sa_flags = 0;

    if (sigaction(SIGINT, &A, NULL) == -1)
    {
        perror("(SERVEUR) Erreur lors de l'armement de SIGINT");
        exit(1);
    }

    printf("(SERVEUR) Le signal SIGINT a bien été armé\n");


    // Création de la socket serveur
    printf("(SERVEUR) Creation de la socket\n");

    idServeur = Socket();

    // Association de la socket au port
    Bind(idServeur, PORT);

    // Mise en écoute
    Listen(idServeur);

    printf("(SERVEUR) Serveur en attente de connexions sur le port %d\n",
           PORT);


    // Attente des clients
    while (1)
    {
        int client = Accept(idServeur);

        printf("(SERVEUR) Nouveau client connecte\n");


        // Allocation pour transmettre la socket au thread
        int *socketClient = malloc(sizeof(int));

        if (socketClient == NULL)
        {
            perror("(SERVEUR) Erreur de malloc");

            Close(client);

            continue;
        }

        *socketClient = client;


        // Création du thread
        pthread_t thread;

        if (pthread_create(&thread,NULL,GestionClient,socketClient) != 0)
        {
            perror("(SERVEUR) Erreur de pthread_create");

            Close(client);

            free(socketClient);

            continue;
        }


        // Le thread est indépendant
        pthread_detach(thread);
    }


    return 0;
}


void *GestionClient(void *arg)
{
    int client = *((int *)arg);

    Liberer(arg);

    MESSAGE m;


    printf("(SERVEUR) Thread cree pour le client\n");


    while (1)
    {
        // Réception d'une requête
        int resultat = Receive(client, &m);

        if (resultat == -1)
        {
            printf("(SERVEUR) Erreur de reception\n");
            break;
        }


        printf("(SERVEUR) Requete recue : %d\n",m.requete);


        switch (m.requete)
        {
            case CONNECT:

                printf("(SERVEUR) CONNECT\n");

                break;


            case DISCONNECT:

                printf("(SERVEUR) DISCONNECT\n");

                free(m.data1);
                free(m.data2);
                free(m.texte);

                Close(client);

                return NULL;


            case LOGIN:

                printf("(SERVEUR) LOGIN\n");

                printf("(SERVEUR) Login : %s\n",m.data1);

                printf("(SERVEUR) Password : %s\n",m.data2);

                break;


            case LOGOUT:

                printf("(SERVEUR) LOGOUT\n");

                break;


            default:

                printf("(SERVEUR) Requete inconnue\n");

                break;
        }


        // Exemple de réponse
        if (Send(client, &m) == -1)
        {
            printf("(SERVEUR) Erreur d'envoi\n");

            free(m.data1);
            free(m.data2);
            free(m.texte);

            break;
        }


        // Libération des données reçues
        free(m.data1);
        free(m.data2);
        free(m.texte);
    }


    Close(client);

    return NULL;
}


void HandlerSIGINT(int sig)
{
    printf("\n(SERVEUR) Arret du serveur\n");

    Close(idServeur);

    exit(0);
}
void Liberer(void* arg)
{
	free(arg);
}
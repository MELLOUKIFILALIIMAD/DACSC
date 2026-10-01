#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <pthread.h>
#include <string>

#include "socket.h"
#include "protocole.h"
#include "database.h"

using namespace std;



pthread_mutex_t mutexDB = PTHREAD_MUTEX_INITIALIZER;


int idServeur;

void HandlerSIGINT(int sig);
void *GestionClient(void *arg);
void Liberer(void* arg);


int main()
{
    // Connexion SQL:
    printf("(SERVEUR) Version actuelle: 0.1.0");
    printf("(SERVEUR) Connexion à la base de donnée");
    ConnexionBD();
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
    Bind(idServeur, PORT_ENCODING);

    // Mise en écoute
    Listen(idServeur);

    printf("(SERVEUR) Serveur en attente de connexions sur le port %d\n", PORT_ENCODING);


    // Attente des clients
    while (1)
    {
        int client = Accept(idServeur);

        printf("(SERVEUR) Nouveau client connecte\n");


        // Allocation pour transmettre la socket au thread
        int *socketClient = (int *) malloc(sizeof(int));

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
    int result;
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
            case LOGIN:
                MESSAGE msg;
                int existe;
                fprintf(stderr,"(SERVEUR %ld) Requete LOGIN reçue de %d : --%s--\n",m.type, m.expediteur, m.data2);
                m.type = m.expediteur;
                msg.expediteur = getpid();
                msg.requete = LOGIN;
                pthread_mutex_lock(&mutexDB);
                existe = LoginExiste(m.data2);
                if (existe == 0)
                {
                    AjouterEmploye(m.data2, m.texte);
                }
                result = VerifierLogin(m.data2, m.texte);
                pthread_mutex_unlock(&mutexDB);
                if (result == 1)
                {
                    printf("(SERVEUR) Login correct\n");
                    string data1 = "OK";
                    msg.data1 = (char*)data1.c_str();

                    msg.data2 = NULL;
                    msg.texte = NULL;
                }
                else
                {
                    if (existe == 1)
                    {
                        fprintf(stderr,"(SERVEUR) Utilisateur déja existant\n");
                    }
                    printf("(SERVEUR) Login incorrect\n");

                    string data1 = "KO";
                    msg.data1 = (char*)data1.c_str();
                    msg.data2 = NULL;
                    msg.texte = NULL;

                }

                if (Send(client, &msg) == -1)
                {
                    printf("(SERVEUR %d) Erreur d'envoi (REQUETE LOGIN: %d)\n", getpid(), m.expediteur);
                    Close(client);
                    return NULL;

                }

                break;

            case LOGOUT:

                printf("(SERVEUR) LOGOUT\n");

                break;


            case GET_AUTHORS:

                printf("(SERVEUR) GET_AUTHORS\n");

                break;
            
            case GET_SUBJECTS:

                printf("(SERVEUR) GET_SUBJECTS\n");

                break;

            case ADD_AUTHOR:

                printf("(SERVEUR) ADD_AUTHOR\n");

                break;
            
            case ADD_SUBJECT:

                printf("(SERVEUR) ADD_SUBJECT\n");

                break;
            
            case ADD_BOOK:

                printf("(SERVEUR) ADD_BOOK\n");

                break;

        }



        if (m.data1 != NULL)
        {
            free(m.data1);
        }
        if (m.data2 != NULL)
        {
            free(m.data2);
        }
        if (m.texte != NULL)
        {
            free(m.texte);
        }
    }


    Close(client);
    return NULL;
}


void HandlerSIGINT(int sig)
{
    printf("\n(SERVEUR) Arret du serveur\n");

    Close(idServeur);
    DeconnexionBD();

    exit(0);
}
void Liberer(void* arg)
{
	free(arg);
}
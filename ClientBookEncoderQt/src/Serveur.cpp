#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <pthread.h>
#include <string>
#include "socket.h"
#include "protocole.h"
#include "database.h"

using namespace std;
#define TAILLE_FILE 20


pthread_mutex_t mutexDB = PTHREAD_MUTEX_INITIALIZER;

pthread_mutex_t mutexFile = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t condFile = PTHREAD_COND_INITIALIZER;

int fileClients[TAILLE_FILE];
int debut = 0;
int fin = 0;
int nbClients = 0;

int idServeur;

void HandlerSIGINT(int sig);
void *GestionClient(void *arg);
void Liberer(void* arg);
void *Worker(void *arg);


int main()
{
    // Connexion SQL:
    printf("(SERVEUR) Version actuelle: 0.2.2");
    printf("(SERVEUR) Connexion à la base de donnée");
    LireConfiguration();
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

    // Création du thread pool
    pthread_t threads[NB_THREADS];

    for (int i = 0; i < NB_THREADS; i++)
    {
        if (pthread_create(&threads[i], NULL, Worker, NULL) != 0)
        {
            perror("Erreur pthread_create");
            exit(1);
        }

        pthread_detach(threads[i]);
    }

    // Attente des clients
    while (1)
    {
        int client = Accept(idServeur);

        printf("(SERVEUR) Nouveau client connecte\n");

        pthread_mutex_lock(&mutexFile);

        if (nbClients < TAILLE_FILE)
        {
            fileClients[fin] = client;
            fin = (fin + 1) % TAILLE_FILE;
            nbClients++;

            pthread_cond_signal(&condFile);
        }
        else
        {
            Close(client);
        }

        pthread_mutex_unlock(&mutexFile);

        // Allocation pour transmettre la socket au thread
        int *socketClient = (int *) malloc(sizeof(int));

        if (socketClient == NULL)
        {
            perror("(SERVEUR) Erreur de malloc");

            Close(client);

            continue;
        }

        *socketClient = client;
    }


    return 0;
}


void *GestionClient(void *arg)
{
    int client = *((int *)arg);
    int result;
    int loggedIn = 0;
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
                    string texte = "Utilisateur inexistant";
                    string data1 = "KO";
                    msg.data1 = (char*)data1.c_str();
                    msg.texte = (char*)texte.c_str();
                    msg.data2 = NULL;

                    if (Send(client, &msg) == -1)
                    {
                        printf("(SERVEUR %d) Erreur d'envoi (REQUETE LOGIN: %d)\n", getpid(), m.expediteur);
                        Close(client);
                        return NULL;

                    }

                    pthread_mutex_unlock(&mutexDB);
                    break;
                }
                result = VerifierLogin(m.data2, m.texte);
                loggedIn = isLoggedin(m.data2);
                pthread_mutex_unlock(&mutexDB);
                if (result == 1)
                {
                    printf("(SERVEUR) Login correct\n");
                    string data1 = "OK";
                    msg.data1 = (char*)data1.c_str();

                    msg.data2 = NULL;
                    msg.texte = NULL;

                    result = LoggedIn(m.data2);
                    if (result == 0)
                    {
                        fprintf(stderr,"(SERVEUR) Erreur lors de la mise à jour de l'état de connexion de l'utilisateur\n");
                    }
                }
                else
                {
                    if (existe == 1)
                    {
                        fprintf(stderr,"(SERVEUR) Utilisateur déja existant\n");
                        string message = "Utilisateur déjà connecté";
                        msg.texte = (char*)message.c_str();
                    }
                    printf("(SERVEUR) Login incorrect\n");

                    string data1 = "KO";
                    msg.data1 = (char*)data1.c_str();
                    msg.data2 = NULL;

                    if (existe == 0 || loggedIn == 1)
                        msg.texte = NULL;

                }

                if (loggedIn == 1)
                {
                    fprintf(stderr,"(SERVEUR) Utilisateur déjà connecté\n");
                    msg.texte = (char*) "Utilisateur déjà connecté";
                    msg.data1 = (char*) "KO";
                    msg.data2 = NULL;
                }

                if (Send(client, &msg) == -1)
                {
                    printf("(SERVEUR %d) Erreur d'envoi (REQUETE LOGIN: %d)\n", getpid(), m.expediteur);
                    Close(client);
                    return NULL;

                }

                break;

            case LOGOUT:
                pthread_mutex_lock(&mutexDB);
                result = LoggedOut(m.data2);
                pthread_mutex_unlock(&mutexDB);
                if (result == 0)
                {
                    fprintf(stderr,"(SERVEUR) Erreur lors de la mise à jour de l'état de connexion de l'utilisateur\n");
                }
                else
                {
                    printf("(SERVEUR) Utilisateur déconnecté\n");
                }

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

void *Worker(void *arg)
{
    while (1)
    {
        pthread_mutex_lock(&mutexFile);

        // Tant qu'il n'y a aucun client, le thread attend
        while (nbClients == 0)
        {
            pthread_cond_wait(&condFile, &mutexFile);
        }

        // Récupération du prochain client
        int client = fileClients[debut];

        debut = (debut + 1) % TAILLE_FILE;
        nbClients--;

        pthread_mutex_unlock(&mutexFile);

        // Gestion du client
        int *socketClient = (int *)malloc(sizeof(int));

        if (socketClient == NULL)
        {
            Close(client);
            continue;
        }

        *socketClient = client;

        GestionClient(socketClient);
    }

    return NULL;
}

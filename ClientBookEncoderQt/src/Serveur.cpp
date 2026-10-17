#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <pthread.h>
#include <sstream>
#include <string>
#include <algorithm>
#include <locale>
#include <cstring>
#include <unistd.h>
#include "socket.h"
#include "config.h"
#include "protocole.h"
#include "database.h"
#include "Client.h"
#include "config.h"
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
    printf("(SERVEUR) Version actuelle: 1.0.0");
    printf("(SERVEUR) Connexion à la base de donnée");
    LireConfiguration();
    ConnexionBD();
    ReinitialiserConnexions();

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


// Envoie "REQUETE#ok[#texte]" ou "REQUETE#ko#texte". Retourne 0 si OK, -1 si erreur d'envoi.
static int Repondre(int client, const char *requete, bool ok, const string &texte = "")
{
    string reponse = string(requete) + SEPARATEUR_CHAMP + (ok ? STATUT_OK : STATUT_KO);

    if (!texte.empty())
    {
        reponse += SEPARATEUR_CHAMP + texte;
    }

    return SendTrame(client, reponse.c_str());
}

void *GestionClient(void *arg)
{
    int client = *((int *)arg);
    int logged = 0;
    string loginConnecte = "";
    Liberer(arg);

    char trame[TAILLE_MAX_TRAME + 1];
    char *champs[MAX_CHAMPS];

    printf("(SERVEUR) Thread cree pour le client\n");

    while (1)
    {
        // Réception d'une requête
        int resultat = ReceiveTrame(client, trame, sizeof(trame));

        if (resultat <= 0)
        {
            if (resultat == 0)
                printf("(SERVEUR) Client %d déconnecté\n", client);
            else
                printf("(SERVEUR) Echec de reception\n");

            break;
        }

        // Parsing : champs[0] = nom de la requête, champs[1..] = paramètres
        int nb = Decouper(trame, champs, MAX_CHAMPS);
        const char *requete = champs[0];
        int envoi = 0; // passe à -1 si l'envoi de la réponse échoue

        printf("(SERVEUR) Requete recue : %s\n", requete);

        if (strcmp(requete, LOGIN) == 0)
        {
            // LOGIN#login#password
            if (nb != 3)
            {
                envoi = Repondre(client, LOGIN, false, "Requête invalide");
            }
            else
            {
                const char *login = champs[1];
                const char *password = champs[2];
                string raison = "";
                bool ok = false;

                fprintf(stderr, "(SERVEUR %d) Requete LOGIN : %s\n", getpid(), login);

                pthread_mutex_lock(&mutexDB);

                int existe = LoginExiste(login);

                if (existe == 0)
                    raison = "Utilisateur inexistant";
                else if (existe < 0)
                    raison = "Erreur base de données";
                else if (VerifierLogin(login, password) != 1)
                    raison = "Mot de passe incorrect";
                else if (isLoggedin(login) == 1)
                    raison = "Utilisateur déjà connecté";
                else if (LoggedIn(login) == 1)
                {
                    ok = true;
                    logged = 1;
                    loginConnecte = login;
                    printf("(SERVEUR) Login correct\n");
                }
                else
                    raison = "Erreur de connexion";

                pthread_mutex_unlock(&mutexDB);

                envoi = Repondre(client, LOGIN, ok, raison);
            }
        }
        else if (strcmp(requete, LOGOUT) == 0)
        {
            // LOGOUT#login (pas de réponse)
            if (nb >= 2)
            {
                pthread_mutex_lock(&mutexDB);
                int result = LoggedOut(champs[1]);
                pthread_mutex_unlock(&mutexDB);

                if (result == 0)
                {
                    fprintf(stderr,"(SERVEUR) Erreur lors de la mise à jour de l'état de connexion de l'utilisateur\n");
                }
                else
                {
                    printf("(SERVEUR) Utilisateur déconnecté\n");
                    logged = 0;
                    loginConnecte = "";
                }
            }

            printf("(SERVEUR) LOGOUT\n");
        }
        else if (strcmp(requete, GET_AUTHORS) == 0)
        {
            // GET_AUTHORS -> GET_AUTHORS#ok#ids#noms#prenoms
            string ids, lastnames, firstnames;

            pthread_mutex_lock(&mutexDB);
            int res = GetAuthors(ids, lastnames, firstnames);
            pthread_mutex_unlock(&mutexDB);

            if (res != 1)
            {
                printf("(Serveur) Erreur lors de la récupération des auteurs\n");
                envoi = Repondre(client, GET_AUTHORS, false, "Erreur lors de la récupération des auteurs");
            }
            else
            {
                string reponse = string(GET_AUTHORS) + SEPARATEUR_CHAMP + STATUT_OK
                               + SEPARATEUR_CHAMP + ids
                               + SEPARATEUR_CHAMP + lastnames
                               + SEPARATEUR_CHAMP + firstnames;

                envoi = SendTrame(client, reponse.c_str());

                printf("(SERVEUR) Récupération des auteurs réussie\n");
            }
        }
        else if (strcmp(requete, GET_SUBJECTS) == 0)
        {
            // GET_SUBJECTS -> GET_SUBJECTS#ok#ids#noms
            string ids, names;

            pthread_mutex_lock(&mutexDB);
            int res = GetSubjects(ids, names);
            pthread_mutex_unlock(&mutexDB);

            if (res != 1)
            {
                printf("(Serveur) Erreur lors de la récupération des sujets\n");
                envoi = Repondre(client, GET_SUBJECTS, false, "Erreur lors de la récupération des sujets");
            }
            else
            {
                string reponse = string(GET_SUBJECTS) + SEPARATEUR_CHAMP + STATUT_OK
                               + SEPARATEUR_CHAMP + ids
                               + SEPARATEUR_CHAMP + names;

                envoi = SendTrame(client, reponse.c_str());

                printf("(SERVEUR) Récupération des sujets réussie\n");
            }
        }
        else if (strcmp(requete, ADD_AUTHOR) == 0)
        {
            // ADD_AUTHOR#nom#prenom#dateNaissance
            printf("(SERVEUR) ADD_AUTHOR\n");

            if (nb != 4)
            {
                envoi = Repondre(client, ADD_AUTHOR, false, "Requête invalide");
            }
            else
            {
                pthread_mutex_lock(&mutexDB);
                int res = AddAuthors(champs[1], champs[2], champs[3]);
                pthread_mutex_unlock(&mutexDB);

                if (res == 0)
                    envoi = Repondre(client, ADD_AUTHOR, false, "Erreur lors de l'ajout d'un auteur.");
                else
                    envoi = Repondre(client, ADD_AUTHOR, true, "Auteur ajouté avec succès.");
            }
        }
        else if (strcmp(requete, ADD_SUBJECT) == 0)
        {
            // ADD_SUBJECT#nom
            printf("(SERVEUR) ADD_SUBJECT\n");

            if (nb != 2)
            {
                envoi = Repondre(client, ADD_SUBJECT, false, "Requête invalide");
            }
            else
            {
                pthread_mutex_lock(&mutexDB);
                int res = AddSubjects(champs[1]);
                pthread_mutex_unlock(&mutexDB);

                if (res == 0)
                    envoi = Repondre(client, ADD_SUBJECT, false, "Erreur lors de l'ajout d'un sujet.");
                else
                    envoi = Repondre(client, ADD_SUBJECT, true, "Sujet ajouté avec succès.");
            }
        }
        else if (strcmp(requete, ADD_BOOK) == 0)
        {
            // ADD_BOOK#titre#isbn#auteur#sujet#pages#annee#prix#stock
            printf("(SERVEUR) ADD_BOOK\n");

            if (nb != 9)
            {
                envoi = Repondre(client, ADD_BOOK, false, "Requête invalide");
            }
            else
            {
                const char *title = champs[1];
                const char *isbn = champs[2];
                const char *authorname = champs[3];
                const char *subjecttitle = champs[4];
                int pageCount = atoi(champs[5]);
                int publishYear = atoi(champs[6]);

                string prix = champs[7];
                std::replace(prix.begin(), prix.end(), ',', '.');
                float price = (float) atof(prix.c_str());

                int stockQuantity = atoi(champs[8]);

                printf("pageCount = %d\npublishYear = %d\nprice = %.2f\nstockQuantity = %d\n",
                       pageCount, publishYear, price, stockQuantity);

                pthread_mutex_lock(&mutexDB);
                int res = AddBook(authorname, subjecttitle, title, isbn, pageCount, stockQuantity, price, publishYear);
                pthread_mutex_unlock(&mutexDB);

                if (res == 0)
                    envoi = Repondre(client, ADD_BOOK, false, "Erreur lors de l'ajout d'un livre.");
                else
                    envoi = Repondre(client, ADD_BOOK, true, "Livre ajouté avec succès.");
            }
        }
        else
        {
            envoi = Repondre(client, requete, false, "Requête inconnue");
        }

        if (envoi == -1)
        {
            printf("(SERVEUR %d) Erreur d'envoi (REQUETE %s)\n", getpid(), requete);
            break;
        }
    }

    // Fin de la connexion : on libère le compte s'il était connecté
    if (logged == 1)
    {
        pthread_mutex_lock(&mutexDB);
        LoggedOut(loginConnecte.c_str());
        pthread_mutex_unlock(&mutexDB);
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
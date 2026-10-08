#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <pthread.h>
#include <string>
#include <cstring>
#include <mysql.h>
#include "database.h"

MYSQL *connexion;

void ConnexionBD()
{
    connexion = mysql_init(NULL);

    if (mysql_real_connect(connexion,"localhost","Student","PassStudent1_","PourStudent",0, 0, 0) == NULL)
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        exit(1);
    }
}

void DeconnexionBD()
{
    mysql_close(connexion);
}

int VerifierLogin(const char *login, const char *password)
{
    char requete[256];

    sprintf(requete,"SELECT id FROM employees WHERE login='%s' AND password='%s';",login, password);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (mysql_num_rows(resultat) > 0)
    {
        mysql_free_result(resultat);
        return 1;
    }

    mysql_free_result(resultat);

    return 0;
}

int isLoggedin(const char *login)
{
    char requete[256];

    sprintf(requete,"SELECT Active FROM employees WHERE login='%s';",login);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (mysql_num_rows(resultat) > 0)
    {
        MYSQL_ROW row = mysql_fetch_row(resultat);
        int active = atoi(row[0]);
        mysql_free_result(resultat);
        return active;
    }

    mysql_free_result(resultat);

    return 0;
}

int LoginExiste(const char *login)
{
    char requete[256];

    sprintf(requete,"SELECT id FROM employees WHERE login='%s';",login);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (mysql_num_rows(resultat) > 0)
    {
        mysql_free_result(resultat);
        return 1;
    }

    mysql_free_result(resultat);

    return 0;
}

int LoggedIn(const char *login)
{
    char requete[256];

    // Updating Active to 1 if the user is logged in
    sprintf(requete,"UPDATE employees SET Active = 1 WHERE login='%s';",login);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    return 1;
}

int LoggedOut(const char *login)
{
    char requete[256];

    // Updating Active to 0 if the user is logged out
    sprintf(requete,"UPDATE employees SET Active = 0 WHERE login='%s';",login);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    printf("(SERVEUR) Utilisateur déconnecté : %s\n", login);

    return 1;
}

MESSAGE GetAuthors(int client)
{
    char requete[256];
    MESSAGE msg;

    sprintf(requete,"SELECT id, last_Name, first_Name FROM authors;");

    if (mysql_query(connexion, requete))
    {
        printf("(SERVEUR) Erreur lors de la récupération des auteurs : %s\n", mysql_error(connexion));
        msg.type = client;
        msg.expediteur = getpid();
        msg.requete = GET_AUTHORS;
        msg.data1 = NULL;
        msg.data2 = NULL;
        msg.texte = NULL;
        return msg;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (mysql_num_rows(resultat) > 0)
    {

        std::string data1 = ""; // id
        std::string data2 = ""; // lastName
        std::string texte = "";

        msg.type = client;
        msg.expediteur = getpid();
        msg.requete = GET_AUTHORS;

        MYSQL_ROW row;
        while ((row = mysql_fetch_row(resultat)))
        {
            data1 += std::string(row[0] ? row[0] : "") + ";";
            data2 += std::string(row[1] ? row[1] : "") + ";";
            texte += std::string(row[2] ? row[2] : "") + ";";
        }

        msg.data1 = strdup(data1.c_str());
        msg.data2 = strdup(data2.c_str());
        msg.texte = strdup(texte.c_str());
    }

    mysql_free_result(resultat);

    return msg;
}

MESSAGE GetSubjects(int client)
{
    char requete[256];
    MESSAGE msg;

    sprintf(requete,"SELECT id, name FROM subjects;");

    if (mysql_query(connexion, requete))
    {
        printf("(SERVEUR) Erreur lors de la récupération des sujets : %s\n", mysql_error(connexion));
        msg.type = client;
        msg.expediteur = getpid();
        msg.requete = GET_SUBJECTS;
        msg.data1 = NULL;
        msg.data2 = NULL;
        msg.texte = NULL;
        return msg;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (mysql_num_rows(resultat) > 0)
    {
        std::string data1 = ""; // id, exemple : 1;2;3..
        std::string texte = ""; // name, exemple : Roman;Science-fiction;Thriller.. 

        msg.type = client;
        msg.expediteur = getpid();
        msg.requete = GET_SUBJECTS;

        MYSQL_ROW row;
        while ((row = mysql_fetch_row(resultat)))
        {
            data1 += std::string(row[0] ? row[0] : "") + ";";
            texte += std::string(row[1] ? row[1] : "") + ";";
        }

        msg.data1 = strdup(data1.c_str());
        msg.data2 = NULL;
        msg.texte = strdup(texte.c_str());
    }

    mysql_free_result(resultat);

    return msg;
}

// Not used anywhere
int AjouterEmploye(const char *login, const char *password)
{
    char requete[256];

    sprintf(requete,"INSERT INTO employees (login, password) VALUES ('%s','%s');",login, password);

    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }

    printf("(SERVEUR) Employe ajoute : %s\n", login);

    return 1;
}
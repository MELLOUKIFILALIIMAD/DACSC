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
    printf("(SERVEUR) Login reçu : '%s'\n", login);
    printf("(SERVEUR) Mot de passe reçu : '%s'\n", password);

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
void ReinitialiserConnexions()
{
    char requete[] = "UPDATE employees SET Active = 0";

    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "(SERVEUR) Erreur de réinitialisation : %s\n",
                mysql_error(connexion));
    }
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
int AddAuthors(const char* lastname, const char* firstname, const char * date)
{
    char requete[256];

    sprintf(requete,"INSERT INTO authors (last_name, first_name, birth_date) VALUES ('%s','%s', '%s');", lastname, firstname, date);

    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }

    printf("(SERVEUR) Auteur ajoute : %s %s\n", lastname, firstname);

    return 1;

}
int AddSubjects(const char *name)
{
    char requete[256];

    sprintf(requete,"INSERT INTO subjects (name) VALUES ('%s');",name);

    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }

    printf("(SERVEUR) Sujet ajoute : %s\n", name);

    return 1;
}

int AddBook(const char* authorname, const char* subjecttitle, const char *title, const char *isbn, int pageCount, int stockQuantity, float price, int publishYear)
{
    size_t taille = strlen(title) + strlen(authorname) + strlen(subjecttitle) + strlen(isbn) + 512;
    char* requete = (char*)malloc(taille);

    if (requete == NULL)
        return 0;

    sprintf(requete,"SELECT id FROM authors WHERE CONCAT(last_name, ' ', first_name)='%s';", authorname);
    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);
    if (mysql_num_rows(resultat) == 0)
    {
        fprintf(stderr, "(SERVEUR) Auteur non trouvé : %s\n", authorname);
        mysql_free_result(resultat);
        return 0;
    }

    MYSQL_ROW row = mysql_fetch_row(resultat);
    int author_id = atoi(row[0]);
    mysql_free_result(resultat);

    sprintf(requete,"SELECT id FROM subjects WHERE name='%s';", subjecttitle);
    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }
    resultat = mysql_store_result(connexion);
    if (mysql_num_rows(resultat) == 0)
    {
        fprintf(stderr, "(SERVEUR) Sujet non trouvé : %s\n", subjecttitle);
        mysql_free_result(resultat);
        return 0;
    }
    row = mysql_fetch_row(resultat);
    int subject_id = atoi(row[0]);

    sprintf(requete,"INSERT INTO books (author_id, subject_id, title, isbn, page_count, stock_quantity, price, publish_year) VALUES (%d, %d, '%s', '%s', %d, %d, %f, %d);", author_id, subject_id, title, isbn, pageCount, stockQuantity, price, publishYear);
    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        mysql_free_result(resultat);
        return 0;
    }

    free(requete);
    printf("(SERVEUR) Livre ajoute : %s\n", title);

    mysql_free_result(resultat);
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
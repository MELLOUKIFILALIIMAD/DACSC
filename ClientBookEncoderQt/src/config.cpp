#include "config.h"

#include <stdio.h>
#include <stdlib.h>

int nbThreads = 0;
int port = 0;

void LireConfiguration(void)
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

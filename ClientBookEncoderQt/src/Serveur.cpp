#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <sys/wait.h>
#include <signal.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <mysql.h>
#include <setjmp.h>
#include "protocole.h" // contient la cle et la structure d'un message

int idQ, idShm;
void HandlerSIGINT(int sig);
int main()
{
	// armement des signaux
	struct sigaction A;
	A.sa_handler = HandlerSIGINT;
	sigemptyset(A.sa_mask);
	A.sa_flags = 0;
	if ((sigaction(SIGINT, &A, NULL)) == -1)
	{
	  perror("(SERVEUR) Erreur lors de l'armement de SIGINT\n");
	  exit(1);
	}
	printf("(SERVEUR) Le signal SINGINT à bien été armé \n");

	// Création des ressources
	fprintf(stderr,"(SERVEUR %d) Creation de la file de messages\n",getpid());
	if ((idQ = msgget(CLE, IPC_CREAT | 0600)) == -1)  // CLE definie dans protocole.h
	{
		perror("(SERVEUR) Erreur de msgget");
		exit(EXIT_FAILURE);
	}
	if ((idShm = shmget(CLE, 200, IPC_CREAT | 0600)) == -1) {
		perror("(SERVEUR) Erreur de shmget");
		msgctl(idQ, IPC_RMID, NULL);
		exit(EXIT_FAILURE);
	}
    fprintf(stderr,"(SERVEUR %d) memoire partagee cree idShm=%d\n", getpid(), idShm);
    MESSAGE m;
    while(1)
    {
		fprintf(stderr,"(SERVEUR %d) Attente d'une requete...\n",getpid());
    	if (msgrcv(idQ,&m,sizeof(MESSAGE)-sizeof(long),1,0) == -1)
    	{
    		perror("(SERVEUR) Erreur de msgrcv ");
      		msgctl(idQ,IPC_RMID,NULL);
      		exit(1);
    	}
    	switch(m.requete)
    	{
    		case CONNECT:
				break;
			case DISCONNECT:
				break;
			case LOGIN:
				break;
			case LOGOUT:
				break;
    	}
    }
}
void HandlerSIGINT(int sig)
{
	msgctl(idQ, IPC_RMID, NULL);
	shmctl(idShm, IPC_RMID, NULL);

	fprintf(stderr,"(SERVEUR) IPC supprimés\n");
	exit(0);
}
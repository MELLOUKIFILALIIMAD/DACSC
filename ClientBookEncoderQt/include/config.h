#ifndef CONFIG_H
#define CONFIG_H

extern int nbThreads, port;

#define NB_THREADS nbThreads
#define PORT_ENCODING port

// Lit ../configuration.txt et remplit nbThreads et port (quitte le programme en cas d'erreur)
void LireConfiguration(void);



#endif // CONFIG_H

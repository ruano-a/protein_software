#1. PROTEIN
Protein est un éditeur de texte hautement configurable et extensible grâce à un système de modules, il saura s'adapter aux gouts et besoins de l'utilisateur. Intégrant la gestion du travail collaboratif, Protein est l'éditeur idéal pour améliorer le flux de travail d'une équipe de développeurs. Disponible sur toutes les plateformes.

##2. Pré-requis
* Maven
    Afin de construire le projet.
* JDK 1.8
    Doit contenir JavaFx

##3. Informations
Cette branche est dédié au dévellopement du Core. Il permettra de gérer les modules, en ajouter, en supprimer et de les faires communiquer.

####Les Manager et leur fils:
* MenuManager
  * Manager.Module
* WorkspaceManager
  * Manager.Workspace
  * TabHolder
    * PTab
* NotificationManager
  * Manager.Notification
* NotificationType(enum)

##4. Instalation
Pour travailler sur ces sources et lancer le projet depuis IntelliJ, suivez ces étapes:
- Cloner le dépôt git
- Lancer la commande "mvn idea:idea" dans le répertoir contenant le pom.xml
- Cliquer sur le .ipr pour ouvrir le projet dans intelliJ

PS: intelliJ vous propose de reconnaitre le projet comme projet maven, un conseil acceptez. 

Enjoy !!!

## 5. Commandes maven
Maven sert à plusieurs choses, il fonctionne à la manière d'un Makefile, voici
les diverses commandes:

| Commandes           | Descriptifs                                      |
|---------------------|--------------------------------------------------|
| mvn idea:idea       | Construit un projet pour intelliJ                |
| mvn eclipse:eclipse | Construit un projet pour eclipse                 |
| mvn compile         | Compile le projet                                |
| mvn clean           | Clean le projet comme un make fclean             |
| mvn assembly:single | Assemble le projet pour créer un .JAR exécutable |

La commande "mvn clean compile assembly:single" est notament très utile pour les phases de test.
L'ensemble de ces commandes sont exécutable depuis intelliJ en mode graphique un fois le projet reconnu
comme projet maven.

## 6 . Docummentation utile
* Maven: http://maven-guide-fr.erwan-alliaume.com/maven-guide-fr/site/reference/public-book.html
* JavaFx: http://docs.oracle.com/javase/8/javase-clienttechnologies.htm
* Modules.Module.Git: https://www.atlassian.com/git/tutorials/setting-up-a-repository

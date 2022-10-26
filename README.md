# url-shortener : Getting started

## Requis
- java oracle jdk-19
- apache-maven-3.8.6
- Spring Boot 3.0.0-RC1
- H2 embedded database
- MySQL 8.0.31

Ce projet a été développé en utilisant également l'environnement suivant :
- Windows 10
- IntelliJ IDEA Community 2022.1.4
- MySQL Workbench 8.0
- PostMan 7.36.7
- Sonarqube Community 9.7
- GIT 2.38.1
- Github

## Compilation avec Maven
Il y a un pom.xml maven à la racine du projet.

### Compiler

    mvn clean install 

### Lancer les tests

    mvn test

## REST API

Voici une description de l'API rest pour url-shortener :


### createShortUrl

#### Request

`POST /`
{ "url" : ${url} }<br/>
${url} = BODY PARAMETER = l'url complète pour laquelle on souhaite obtenir une url courte.

    curl -X POST http://localhost:8080/ -H 'Content-Type: application/json' -d '{"url":"https://www.journaldemontreal.com/5-minutes"}'

#### Response

    HTTP/1.1 200
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Wed, 26 Oct 2022 19:06:58 GMT

    {"success":true,"shortUrl":"http://localhost:8080/YRIbFjrhPL"}

### decodeShortUrl

#### Request

`GET /${token}`<br/>
${token} = PATH parameter = le jeton unique qui constitue une url courte.

    curl -i -H 'Accept: application/json' http://localhost:8080/YRIbFjrhPL

#### Response

    HTTP/1.1 200
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Wed, 26 Oct 2022 19:07:24 GMT

    {"success":true,"originalCompleteUrl":"https://www.journaldemontreal.com/5-minutes"}


## Environnement

### Profils Spring
Il y a deux profils Spring:
- default : c'est le profil de développement pour le projet, qui utilise une base de données H2 embarquée, et les urls générées ont le baseurl localhost:8080 
- prod : c'est le profil de "production" pour le projet, qui utilise une base de données MySql séparée, et les urls générées ont le baseurl FLORENT-PC:8080 (note : on pourrait imaginer un vrai nom de domaine ici)

### Base de données

En environnement de développement, la base de données est la base embarquée H2 qui démarre automatiquement avec spring-boot. La base est persistente dans un fichier sur le serveur.

En environnement de "production", la base de données utilisée est MySQL 8.0.31. Les paramètres de connexion à la base de données sont dans application-prod.yml (voir spring.datasource.url.username)

#### SHORT_URL
La table principale utilisée dans la base de données, pour lier un jeton avec une url est SHORT_URL.

    # create table SHORT_URL
    create table SHORT_URL (
        ID BIGINT PRIMARY KEY AUTO_INCREMENT,
        TOKEN VARCHAR(10) UNIQUE NOT NULL,
        ORIGINAL_URL VARCHAR(2048)
    );

    # grant rights to user URLSHORTENER
    grant select, insert on URLSHORTENER.SHORT_URL to 'URLSHORTENER'@'localhost';


### Démarrage

En environnement de développement (par défaut), démarrer l'application dans un IDE avec UrlShortenerApplication.main, ou en utilisant le jar avec la commande 

    java -jar url-shortener-1.0.0-SNAPSHOT.jar UrlShortenerApplication

En environnement de "production" (profil prod), il faut utiliser une clé jasypt. Il faut démarrer l'application avec 

    java -Djasypt.encryptor.password=CLE_JASYPT -jar url-shortener-1.0.0-SNAPSHOT.jar UrlShortenerApplication

Pour des raisons de sécurité, la clé Jasypt n'est pas dans le readme.md.


### Exemples POSTMAN

Des exemples d'appel POSTMAN sont disponibles dans documentation/urlshortener.postman_collection.json

## Chiffrage Jasypt

Jasypt est utilisé pour chiffrer des valeurs sensibles dans les fichiers de configuration. Voir https://github.com/ulisesbocchio/jasypt-spring-boot

### Créer une valeur chiffrée

Pour créer une nouvelle valeur chiffrer, il faut utiliser la commande maven suivante : 

    mvn jasypt:encrypt-value -Djasypt.encryptor.password=THE_PASSWORD -Djasypt.plugin.value=THE_VALUE_YOU_WANT_TO_ENCRYPT

Et à la fin du traitement de la commande maven, on trouvera une valeur ENC(?????????) juste avant les lignes BUILD SUCCESS dans le build Maven. Il faut utiliser cette valeur telle quelle dans le fichier application.yml et il faudra fournir la clef de chiffrage à l'application au démarrage via un argument VM.

### Utiliser la clé dans le lancement du projet

Pour rappel, si des valeurs chiffrées sont utiliseés dans application.yml (comme notamment dans application-prod.yml), il faut indiquer la clé Jasypt dans un argument VM au démarrage de l'application, comme par exemple : 

    -Djasypt.encryptor.password=THE_PASSWORD


## Notes

Dans la notion d'url courte, on va nommer "jeton" (token) la partie qui est après le domaine et après le chemin du endpoint (/), et qui consitue l'identifiant unique de l'url courte.

### Génération du jeton pour l'url courte
J'ai eu plusieurs idées pour créer l'url courte 

#### utiliser une clé de hashage?
Pour une clé de hashage, j'ai rejeté l'idée car, selon l'algorithme de hashage, plusieurs valeurs originales différentes pourraient donner la même clé de hashage. Et selon l'algorithme de hashage, nous dépasserions le nombre de caractères maximum pour le "jeton" dans l'url courte.

#### utiliser une séquence numérique?
Pour une séquence numérique, on aurait 10 milliards de permutations, de 0000000000 à 9999999999, mais il serait alors possible de prédire les urls courtes. De plus, une perspective d'une limite à 10 millards n'est peut-être pas assez.

#### utiliser des caractères aléatoires?
Pour utiliser des caractères aléatoires, en utilisant les lettres de a à z en minuscules, les lettres de A à Z en majuscules, et les 10 chiffres de 0 à 9, on a 62 caractères disponibles, et avec 10 caractètres dans le jeton on arrive à 62^10 permutations, soit 839 299 365 868 340 224 permutations possibles, soit largement plus que l'option 2.
Par contre, l'inconvénient évident est que, si on génère un jeton complètement au hasard, il reste la possibilité, bien que relativement peu probable, de générer un jeton déjà utilisé!
Ceci nous oblige donc à systématiquement vérifier si le jeton est déjà utilisé, et à en générer un autre. 

#### Choix technique : caractères aléatoires
J'ai choisi de garder la stratégie des caractères aléatoires car elle présentait la meilleure fiabilité pour cet exercice.

Les caractères utilisés pour les caractères aléatoires sont paramétrés dans application.yml dans :

    urlshortener.token.characters

Le nombre de caractères utilisés pour le jeton est paramétré (défaut 10)  dans application.yml dans :

    urlshortener.token.length

Si la génération du jeton créé un jeton qui se trouve être déjà utilisé dans la base données, alors une erreur est déclenché, mais sprin-retry permet au code de réessayer jusqu'à 5 fois.
Le nombre de tentatives est paramétré (défaut 5)  dans application.yml dans :

    urlshortener.token.maxattempts

Risque : si on changait ces paramètres, on pourrait créer une situation problématique pour l'application. Par exemple, si on réduisait le choix de caractères, ou si on réduisait la taille du jeton.
Le système est prévu pour réessayer 5 fois (paramétré dans application.yml) en cas de génération d'un jeton déjà utilisé. Si l'application ne parvient pas du tout à créer un jeton unique, en dépit des tentatives d'essai, la requête se terminera avec un message d'erreur.

### Redirection HTTP

Dans UrlShortenerController.decodeShortUrl, si on voulait faire une vraie redirection http (plutôt que de simplement renvoyer l'url originale en réponse), on pourrait écrire quelque chose comme :

    @RequestMapping(value = "/{short-url-token}", method = RequestMethod.GET)
    public void decodeShortUrl(HttpServletResponse httpServletResponse, final @PathVariable("short-url-token") @NotBlank String shortUrlToken) throws ShortUrlTokenNotFoundException {
        httpServletResponse.setHeader("Location", urlShortenerService.getOriginalUrlForShortUrlToken(shortUrlToken));
        httpServletResponse.setStatus(302);
    }

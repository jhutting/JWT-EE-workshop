JSON Web Tokens (JWT) met Java EE
=================================

Benodigdheden
-------------
JDK8 - https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html  
Wildfly 10 - https://wildfly.org/downloads/  
Maven - https://maven.apache.org/  
JWT debugger - https://jwt.io/#debugger-io

Kopieer alvast de docs/jwt.properties naar de wildfly/standalone/configuration/ zodat deze gebruikt kan worden door de jsf client.

De database wordt straks aangemaakt in je home directory/users.h2.db het initiele script is te vinden onder docs/scripts/create-db.sql

Datasource in wildfly onder Subsystems    Subsystem: Datasources    Type: Non-XA    Datasource: jwt-users Back | :  
JNDI:java:/jwt-users  
Driver:h2  

onder connection: Connection URL: jdbc:h2:~/users

Opdrachten
----------

### Opdracht 0 - Kale REST applicatie zonder beveiliging
We beginnen met een simpele opdracht: check de code uit via Git
* Bouw de applicatie en deploy deze op je lokaal draaiende Wildfly instantie.
* Schiet een paar REST berichten in om gegevens op te vragen en muteren. (bijvoorbeeld met POSTMAN of intellij)

### Opdracht 1 - Private/public key pair genereren, instellen en JWT genereren
Genereer een eigen private/public key en laadt deze in om je JWT te beveiligen. Zie docs/create_keys.txt om de keys te genereren.  
(Als alternatief kun je ook de meegeleverde private_key.der en public_key.der uit de docs/ map gebruiken)

Stel bij het teruggeven van de token voor de beheerder/admin rol een kortere looptijd in.

### Opdracht 2 - Hash de wachtwoord velden conform OWASP
Hash de aangeleverde wachtwoorden voor zowel opslag als het vergelijken.  
Zie https://www.owasp.org/index.php/Hashing_Java voor een voorbeeld.

N.B. PBKDF2WithHmacSHA512 is veilig, maar heeft JDK8 of hoger nodig

### Opdracht 3 - Two-factor authentication, reset wachtwoord via email
Je hebt nu een werkende login module, maar wat als je gebruiker zijn wachtwoord niet meer weet?   
Zorg ervoor dat de gebruiker via zijn mailadres een token krijgt waarmee hij/zij het wachtwoord kan wijzigen.

Tip: wegschrijven in de log mag ook. Ga geen hele mailserver optuigen!

### Opdracht 4a - REST endpoint afschermen met @HeaderParam("authorization")
Gezien je nu JWT kunt genereren wordt het tijd om de endpoints te beschermen door te valideren, doe dit in eerste instantie met een @HeaderParam.  
Voorbeeld: Response updatePassword(@HeaderParam("authorization") final String authorization, final User user);

### Opdracht 4b - REST endpoint afschermen met @Secured annotatie
Omdat @HeaderParam en overal een verificationModule.validate(token) onhandig is. Houdt er rekening mee dat je dit alleen kunt doen als je op Rol wilt verifieren.  
In het geval je andere claims uit wilt lezen, bijvoorbeeld de gebruiker ID, dan zul je nog steeds @HeaderParam("authorization") moeten gebruiken.

N.B. een alternatief is een interceptor die de claims eenmalig uitleest en in de sessie beschikbaar maakt.

### Opdracht 5a - Maak een native (JSF / Android) client
Maak een simpele 'native' client voor de REST service, bijvoorbeeld met JSF of Android.

N.B. sla het token veilig op! 

### Opdracht 5b - Maak een javascript client (bijvoorbeeld met Angular 2)
Als vervolg op 5a, maak nu een client met javascript waarbij je het token veilig opslaat via de dubbele cookie-methode. Gebruik **geen** html5 localstorage!

Zie bijvoorbeeld http://stackoverflow.com/questions/27067251/where-to-store-jwt-in-browser-how-to-protect-against-csrf

### Opdracht 6 - Claims! Voeg zelf claims toe en gebruik die in een client uit opdracht 5

### Opdracht 7 - Voeg extra operaties/endpoints toe (optioneel)
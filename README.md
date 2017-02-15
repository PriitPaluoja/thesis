Lokaalseks testimiseks on vaja Linuxil põhinevat arvutit, millel on 512 MB RAM, 300 MB vaba kettaruumi veebirakendusele kasutamiseks ja internetiühenduse olemasolu.

Sammu 1 võib vahele jätta, kui JDK (Java Development Kit) 8 on juba installeeritud. Pärast sammude täitmist pääseb veebirakendusele ligi aadressil http://localhost:8080/ kasutajanimega admin@admin ning parooliga Fahrenheit451. Näidisraport on leitav koodi 11111111111 alt.

Sammud veebirakenduse käivitamiseks operatsioonisüsteemil Ubuntu 16.04:

1. $ sudo apt install openjdk-8-jdk
2. $ git clone https://github.com/PriitPaluoja/thesis.git
3. $ cd thesis
4. $ chmod +x deploy-local.sh
5. $ chmod +x gradlew
6. $ ./deploy-local.sh

Eelnevate sammude täitmise järel pöördub veebirakendus teenuses Heroku paikneva testandmebaasi poole. E-posti teavituste kasutamiseks tuleb application.properties failis asuvad konfiguratsiooniga seotud read vastavusse viia saadaoleva e-posti kliendiga.

* Patsiendi ülevaatuse sisestamise aadress: http://localhost:8080/examination
* Raporti loomise ja vaatamise aadress: http://localhost:8080/choice 
* Sätete aadress: http://localhost:8080/settings
* Kasutajate lisamise aadress: http://localhost:8080/new
* Kasutajate õiguste muutmise aadress: http://localhost:8080/manage

Lokaalseks testimiseks on vaja Linuxil põhinevat arvutit, millel on 512 MB RAM, 300 MB vaba kettaruumi veebirakendusele kasutamiseks ja internetiühenduse olemasolu.

Juhised veebirakenduse käivitamiseks operatsioonisüsteemil Ubuntu 16.04:

1. $ sudo apt install openjdk-8-jdk
2. $ sudo apt install git
3. $ git clone https://github.com/PriitPaluoja/thesis.git
4. $ cd thesis
5. $ chmod +x deploy-local.sh
6. $ chmod +x gradlew
6. $ ./deploy-local.sh

Pärast juhiste täitmist pääseb veebirakendusele ligi aadressil http://localhost:8080/ kasutajanimega admin@admin ning parooliga Fahrenheit451. Näidisraport on leitav isikukoodi 11111111111 alt.

* Patsiendi ülevaatuse sisestamise aadress: http://localhost:8080/examination
* Raporti loomise ja vaatamise aadress: http://localhost:8080/choice 
* Sätete aadress: http://localhost:8080/settings
* Kasutajate lisamise aadress: http://localhost:8080/new
* Kasutajate õiguste muutmise aadress: http://localhost:8080/manage

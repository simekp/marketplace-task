# Zadání
* Zkuste naprogramovat kód, který bude každých 5 minut kontrolovat, zda na tržišti Zonky.cz nepřibyly nějaké nové půjčky a pokud ano, vypíše je. Programové API tržiště Zonky je dostupné na adrese  https://api.zonky.cz/loans/marketplace, dokumentace pak na adrese http://docs.zonky.apiary.io/#.
   
* Výběr technologií necháme na vás, jen ať je to, prosím, v Javě.
   
* Přihlížíme zvláště k dobré testovatelnosti a čistotě kódu a naopak nemáme moc rádi over-engineered řešení :)

# Spuštění programu
Stažení projektu
```
git clone https://github.com/simekp/marketplace-task.git
cd marketplace-task/
```

Program můžeme spustit
```
./gradlew bootRun
```

nebo pomocí Docker
```
docker build . -t marketplace-task
docker run -it marketplace-task 
```

* Pro dotazování se do zonky marketu, je využito filtrování pomocí parametru `datePublished`. 
Parametr je pro první dotaz možné nastavit pomocí proměnné `zonky.marketplace.initialDate`. 
Např.
```
./gradlew bootRun -Pargs="--zonky.marketplace.initialDate=2018-11-08T10:00:00Z" 
```

```
docker build . -t marketplace-task
docker run -e initialDate=2018-11-08T10:00:00Z -it marketplace-task

```
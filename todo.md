
Panels:

- ***List of favourite recipies***
- ***Search for new recipies***
- Optional: ***Write your own recipy***

API (Meal DB):

Searching by name:
- `https://www.themealdb.com/api/json/v1/1/search.php?s={meal}`
- `https://www.themealdb.com/api/json/v1/1/search.php?s=pasta`


Searching by id:
- `https://www.themealdb.com/api/json/v1/1/lookup.php?i={id}`
- `https://www.themealdb.com/api/json/v1/1/lookup.php?i=52771`


Zbiór przepisów
•
•
Opis: Użytkownik tworzy listę ulubionych przepisów, zapisując własne przepisy lub
linki do nich w bazie danych Room.
Integracja z REST: Integracja z API takim jak TheMealDB pozwoli na pobieranie
nowych inspiracji, zdjęć potraw czy składników.

- Panel search
    - Filtruje kategorie, składniki
- Panel favourite meals:
    - Filture kategorie, składniki
- Elementy ViewModel, stan okien (jokeviewmodel)
- DB:
  - save more data than just links/ids, 


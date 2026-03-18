
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
    - Filter by mentioned `strCategory`
    - Filter by mentioned `strTags`
    - Filter by mentioned `strIngredientX`
- Panel favourite meals:
  - Filture kategorie, składniki
    - Panel Categories (`https://www.themealdb.com/api/json/v1/1/categories.php`)
      - --> Panel MealCategories:
    - Panel MealCategories `https://www.themealdb.com/api/json/v1/1/filter.php?c=Seafood`
      - --> Panel ViewMeal:
        - Requires additional fetch/fetch-screen to be performed (`https://www.themealdb.com/api/json/v1/1/lookup.php?i={id}`)
- Elementy ViewModel, stan okien (jokeviewmodel)
  - Alright, I should:
    - Separate Logic and UI inside screens using ViewModel.
    - Alter MainActivity to also use ViewModel
      - Separate/protect mealDao
- DB:
  - save more data than just links/ids, 


`https://www.themealdb.com/api/json/v1/1/categories.php` or shorter `https://www.themealdb.com/api/json/v1/1/list.php?c=list`
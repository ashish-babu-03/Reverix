# Reverix — AI-Powered Movie Booking

Reverix is a smart movie booking platform that recommends movies, theatres, and seats based on your mood and group type — powered by a real AI recommendation engine.

Built with Spring Boot + Kotlin on the backend and a custom HTML/CSS/JS frontend.

---

## What makes it different from BookMyShow

| Feature | BookMyShow | Reverix |
|---|---|---|
| Movie discovery | Browse manually | AI picks based on your mood |
| Theatre selection | List by distance | Classified by vibe (CELEBRATION, FAMILY, DATE_NIGHT, SILENT) |
| Seat selection | Manual | Recommended by group type |
| Group awareness | None | Adapts for solo / couple / friends / family |
| Premium access | None | CinePrime FIFO early-access queue |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9.22 |
| Framework | Spring Boot 3.2.3 |
| Security | Spring Security + JWT (jjwt 0.12.3) |
| Database | MySQL + Liquibase migrations |
| Movie Data | TMDb API |
| AI Engine | OpenRouter + Llama 3.3 70B (free tier) |
| Build | Gradle Kotlin DSL |
| Frontend | Vanilla HTML / CSS / JS |

---

## Features

**AI Mood Recommendation**
Send your mood in plain text ("I feel adventurous", "want something emotional") and the engine calls an LLM to extract genres, match movies from the database, and return ranked recommendations with reasoning.

**Theatre Vibe Classification**
8 Chennai theatres seeded with vibe types. The recommendation engine matches group type → vibe:
- Friends → CELEBRATION
- Family → FAMILY
- Couple → DATE_NIGHT
- Solo → SILENT

**Seat Booking Flow**
1. Browse movies from TMDb (live data)
2. Pick a show
3. Select seats (FRONT / MIDDLE / BACK zones)
4. Seats lock for 10 minutes during payment
5. Confirm booking → stored against your account

**JWT Auth**
Register and login return a signed JWT. All booking endpoints require `Authorization: Bearer <token>`.

**CinePrime**
Premium users get FIFO early access to shows marked `is_prime_early_access = true`.

---

## API Endpoints

### Auth
```
POST /api/auth/register
POST /api/auth/login
```

### Movies
```
GET /api/movies/now-playing
GET /api/movies/popular
GET /api/movies/search?query=
GET /api/movies/{id}
GET /api/movies/rentable
```

### Theatres
```
GET /api/theatres
GET /api/theatres/city/{city}
GET /api/theatres/city/{city}/vibe/{vibeType}
GET /api/theatres/{id}
GET /api/theatres/recommend?city=Chennai&groupType=friends
```

### AI Recommendation
```
POST /api/recommend
Body: {
  "mood": "adventurous",
  "groupType": "friends",
  "groupSize": 4,
  "city": "Chennai",
  "preferredZone": "MIDDLE"
}
```

### Bookings (JWT required)
```
POST   /api/bookings/lock-seats
POST   /api/bookings/confirm
DELETE /api/bookings/{id}
GET    /api/bookings/my-bookings
GET    /api/bookings/recommend-seats?showId=&groupType=&groupSize=
```

---

## Running Locally

### Prerequisites
- Java 17
- MySQL running on port 3306
- TMDb API key → [themoviedb.org](https://www.themoviedb.org/settings/api)
- OpenRouter API key → [openrouter.ai/settings/keys](https://openrouter.ai/settings/keys)

### Setup

1. Clone the repo
```bash
git clone https://github.com/ashish-babu-03/Reverix.git
cd Reverix/reverix
```

2. Create the database
```sql
CREATE DATABASE reverix_db;
```

3. Set your keys in `src/main/resources/application.properties`
```properties
tmdb.api.key=YOUR_TMDB_KEY
openrouter.api.key=YOUR_OPENROUTER_KEY
openrouter.model=meta-llama/llama-3.3-70b-instruct:free
```

4. Run
```bash
./gradlew bootRun
```

5. Open `http://localhost:8080` in your browser

Liquibase will create all 6 tables automatically on first run. 8 Chennai theatres are seeded on startup.

---

## Database Schema

```
users        → id, name, email, password, phone, role
theatres     → id, name, location, city, vibe_type, screen_size, avg_rating
movies       → id, tmdb_id, title, genre, language, mood_tags, poster_url, rating
shows        → id, movie_id, theatre_id, show_time, available_seats, price, is_prime_early_access
seats        → id, show_id, seat_number, zone, status, locked_by_user_id, locked_until
bookings     → id, user_id, show_id, seat_ids, total_amount, status, is_prime_booking
```

---

## Project Structure

```
com.reverix.reverix
├── config/         → JWT filter, Security config
├── model/          → JPA entities (User, Movie, Theatre, Show, Seat, Booking)
├── repository/     → Spring Data JPA repositories
├── dto/            → Request/Response data classes
├── service/        → Business logic + AI recommendation + TMDb integration
└── controller/     → REST endpoints
```

---

## Environment Variables

| Key | Description |
|---|---|
| `tmdb.api.key` | TMDb API key for live movie data |
| `openrouter.api.key` | OpenRouter key for LLM calls |
| `openrouter.model` | Model ID (default: `meta-llama/llama-3.3-70b-instruct:free`) |
| `app.jwt.secret` | JWT signing secret (change in production) |

---

## License

MIT

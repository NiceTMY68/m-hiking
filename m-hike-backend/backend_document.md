## M-Hike Backend Documentation

### Overview
Backend API for M-Hike mobile application. Stack: Node.js, Express, PostgreSQL (Sequelize). File uploads saved locally to `uploads/`. No Firebase/Maps/Weather/Auth (removed).

### Requirements
- Node.js 16+
- PostgreSQL 12+

### Environment Variables (.env)
- DB_HOST=localhost
- DB_PORT=5432
- DB_NAME=m-hiking
- DB_USER=postgres
- DB_PASSWORD=your-password
- PORT=3000
- NODE_ENV=development


### Install & Run
- npm install
- npm run dev (development)
- npm start (production)

### Base URL
- http://localhost:3000
- API base path: `/api`

### Endpoints by Feature (What each endpoint does)

Authentication
- Removed. Device-based model; all endpoints are public.

Hike Management
- GET `/api/hikes`: List hikes (pagination, filter, sort)
- POST `/api/hikes`: Create a new hike
- GET `/api/hikes/:id`: Get hike details (with observations)
- PUT `/api/hikes/:id`: Update a hike
- DELETE `/api/hikes/:id`: Delete a hike
- GET `/api/hikes/search`: Search hikes by text/length/difficulty
- GET `/api/hikes/statistics`: Aggregate stats (count, distance, by difficulty)

Public Hikes (no auth)
- GET `/api/hikes/public`: Public list of hikes
- GET `/api/hikes/public/search`: Public search

Observation Management
- GET `/api/hikes/:hikeId/observations`: List observations of a hike
- POST `/api/hikes/:hikeId/observations`: Add observation to a hike
- GET `/api/observations/:id`: Get single observation details
- PUT `/api/observations/:id`: Update an observation
- DELETE `/api/observations/:id`: Delete an observation

Cloud Sync (Data synchronization)
- POST `/api/sync/upload`: Upload (upsert) hikes and observations from client (requires `x-device-id`)
- GET `/api/sync/download`: Download device data (optionally since `lastSync`) (requires `x-device-id`)
- GET `/api/sync/status`: Sync metrics for device (requires `x-device-id`)
- POST `/api/sync/mark-synced`: Mark hikes as synced by IDs for device (requires `x-device-id`)

File Uploads (Local filesystem)
- POST `/api/upload/image`: Upload a single image file (multipart `image`)
- POST `/api/upload/images`: Upload multiple image files (multipart `images`)
- GET `/uploads/:filename`: Serve uploaded file statically

Bookings
- POST `/api/bookings`: Create a booking (email, hike_id, name/phone/notes)
- GET `/api/bookings?email=...`: List bookings for an email

### Health Check
- GET `/health` -> { status, message, timestamp }

### Authentication
- Removed. No user endpoints.

### Hikes
- GET `/api/hikes` (paginated: page, limit; filters: difficulty, location, startDate, endDate; sort: sortBy, order)
- POST `/api/hikes`
- GET `/api/hikes/:id`
- PUT `/api/hikes/:id`
- DELETE `/api/hikes/:id`
- GET `/api/hikes/search` (q, difficulty, minLength, maxLength)
- GET `/api/hikes/statistics`

### Observations
- GET `/api/hikes/:hikeId/observations`
- POST `/api/hikes/:hikeId/observations`
- GET `/api/observations/:id`
- PUT `/api/observations/:id`
- DELETE `/api/observations/:id`

### Sync (Cloud Data Sync)
- POST `/api/sync/upload`
  - Body: hikes[], observations[]
  - Upsert server-side; marks hikes as `is_synced=true`
- GET `/api/sync/download`
  - Query: optional `lastSync` -> returns items updated after timestamp
- GET `/api/sync/status` -> totals, synced ratio, lastSync
- POST `/api/sync/mark-synced` -> body: hikeIds[]

- POST `/api/upload/image` (multipart field: `image`)
- POST `/api/upload/images` (multipart field: `images[]`, max 10)
- Static serving: GET `/uploads/<filename>`
- Limits: 5MB; images only (jpg, jpeg, png, gif, webp)

- Hike
  - id (UUID, PK), name, location, device_id (string), date, parking_available (bool), length (float, km), difficulty (enum: easy|moderate|hard|expert), description, is_public (bool), is_synced (bool), created_at, updated_at
- Observation
  - id (UUID, PK), hike_id (FK), observation (<=500), time, comments, image_url, category (enum: wildlife|weather|trail_conditions|scenery|other), created_at, updated_at
- Booking
  - id (UUID, PK), hike_id (FK), email, name?, phone?, notes?, is_active (bool), created_at, updated_at

Associations
- Hike 1—N Observation; Hike 1—N Booking

- ### Validation
- Request validation middleware for hikes, observations
- UUID parameter validation where applicable

### Response Format
- Success: { success: true, message?, data }
- Error: { success: false, message, error? }
- Uses proper HTTP status codes (200, 201, 400, 401, 404, 500)

### Error Handling
- Centralized error handler in `app.js`
- 404 handler for unknown routes

### Security
- Public endpoints; Helmet headers, CORS enabled

### Project Structure
- `src/app.js` (Express app, routes mounting)
- `src/server.js` (startup + DB connection + schema sync)
- `src/config/database.js` (Sequelize instance + testConnection)
- `src/models/*` (Hike, Observation, Booking, associations)
- `src/controllers/*` (hikes, observations, sync, upload, bookings)
- `src/routes/*` (hikes, observations, sync, upload, bookings)
- `src/middleware/*` (validation, upload)
- `src/utils/*` (helpers, constants)
- `uploads/` (stored files)

### Notes
- Database schema syncs automatically on startup (`sequelize.sync`)
- All APIs are platform-agnostic and suitable for Android Native and .NET MAUI
- External APIs for Maps/Weather/Firebase were removed to keep runtime minimal

### Postman/Curl Quick Examples
  



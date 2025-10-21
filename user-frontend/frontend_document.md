# TÀI LIỆU KỸ THUẬT FRONTEND - M-HIKING MOBILE APP

## 📋 MỤC LỤC
1. [Tổng quan dự án](#1-tổng-quan-dự-án)
2. [Kiến trúc ứng dụng](#2-kiến-trúc-ứng-dụng)
3. [Mô hình dữ liệu](#3-mô-hình-dữ-liệu)
4. [Màn hình và chức năng](#4-màn-hình-và-chức-năng)
5. [Tính năng chính](#5-tính-năng-chính)
6. [Công nghệ và thư viện](#6-công-nghệ-và-thư-viện)
7. [Cơ sở dữ liệu](#7-cơ-sở-dữ-liệu)
8. [Mạng và đồng bộ hóa](#8-mạng-và-đồng-bộ-hóa)
9. [Giao diện người dùng](#9-giao-diện-người-dùng)
10. [Yêu cầu hệ thống](#10-yêu-cầu-hệ-thống)

---

## 1. TỔNG QUAN DỰ ÁN

### 1.1. Thông tin chung
- **Tên dự án**: M-Hiking Mobile App
- **Package name**: com.example.user_frontend
- **Platform**: Android Native
- **Ngôn ngữ lập trình**: Java
- **Build system**: Gradle (Kotlin DSL)
- **Phiên bản**: 1.0 (versionCode: 1)

### 1.2. Mục đích
Ứng dụng di động cho phép người dùng quản lý và theo dõi các chuyến đi bộ đường dài (hiking). Người dùng có thể:
- Tạo, xem, chỉnh sửa và xóa thông tin các chuyến đi bộ
- Ghi chép các quan sát (observations) trong mỗi chuyến đi
- Tìm kiếm các chuyến đi theo nhiều tiêu chí
- Đồng bộ dữ liệu với server qua WiFi

### 1.3. Đặc điểm nổi bật
- **Offline-first**: Dữ liệu được lưu trữ local trước, đồng bộ sau
- **WiFi-only sync**: Chỉ đồng bộ khi có kết nối WiFi
- **Material Design**: Giao diện hiện đại theo chuẩn Material Design 3
- **Portrait-only**: Ứng dụng chỉ hỗ trợ chế độ dọc

---

## 2. KIẾN TRÚC ỨNG DỤNG

### 2.1. Mô hình kiến trúc
Ứng dụng sử dụng kiến trúc **DAO Pattern (Data Access Object)** với các tầng rõ ràng:

```
┌─────────────────────────────────────┐
│      Presentation Layer             │
│  (Activities & Adapters)            │
├─────────────────────────────────────┤
│      Business Logic Layer           │
│  (DAO & Services)                   │
├─────────────────────────────────────┤
│      Data Layer                     │
│  (SQLite Database & Models)         │
├─────────────────────────────────────┤
│      Network Layer                  │
│  (API Client & Sync Service)        │
└─────────────────────────────────────┘
```

### 2.2. Cấu trúc package
```
com.example.user_frontend/
├── activities/              # Các màn hình Activity
│   ├── AddEditHikeActivity
│   ├── AddEditObservationActivity
│   ├── HikeDetailsActivity
│   ├── HikeListActivity
│   ├── ObservationListActivity
│   └── SearchActivity
├── adapters/               # RecyclerView Adapters
│   ├── HikeAdapter
│   └── ObservationAdapter
├── database/               # Database & DAO
│   ├── DatabaseHelper
│   ├── HikeDAO
│   └── ObservationDAO
├── models/                 # Data Models
│   ├── Hike
│   └── Observation
├── network/                # Network & API
│   ├── ApiClient
│   └── SyncService
├── utils/                  # Utilities
│   └── NetworkUtils
└── MainActivity            # Entry point
```

### 2.3. Flow điều hướng
```
MainActivity (Home)
├── AddEditHikeActivity (Add/Edit Hike)
├── HikeListActivity (View All Hikes)
│   ├── HikeDetailsActivity (Hike Details)
│   │   └── ObservationListActivity (View Observations)
│   │       └── AddEditObservationActivity (Add/Edit Observation)
│   └── AddEditHikeActivity (Edit from list)
└── SearchActivity (Search Hikes)
    └── HikeDetailsActivity (From search results)
```

---

## 3. MÔ HÌNH DỮ LIỆU

### 3.1. Model: Hike
**Mục đích**: Đại diện cho một chuyến đi bộ đường dài

**Thuộc tính**:
| Tên trường | Kiểu dữ liệu | Bắt buộc | Mô tả |
|------------|--------------|----------|--------|
| id | String (UUID) | Yes | ID duy nhất của chuyến đi |
| name | String | Yes | Tên chuyến đi |
| location | String | Yes | Địa điểm |
| date | Date | Yes | Ngày thực hiện |
| parkingAvailable | boolean | Yes | Có chỗ đỗ xe không |
| length | float | Yes | Độ dài (km) |
| difficulty | String | Yes | Độ khó: easy, moderate, hard, expert |
| description | String | No | Mô tả chi tiết |
| estimatedDuration | String | No | Thời gian ước tính |
| weatherConditions | String | No | Điều kiện thời tiết |
| userId | String | No | ID người dùng (cho sync) |
| isSynced | boolean | Yes | Đã đồng bộ chưa |
| createdAt | Date | Yes | Thời gian tạo |
| updatedAt | Date | Yes | Thời gian cập nhật |

**Đặc điểm**:
- Implements `Serializable` để truyền qua Intent
- Auto-generate UUID khi khởi tạo
- Auto-update `updatedAt` khi có thay đổi

### 3.2. Model: Observation
**Mục đích**: Đại diện cho một quan sát trong chuyến đi

**Thuộc tính**:
| Tên trường | Kiểu dữ liệu | Bắt buộc | Mô tả |
|------------|--------------|----------|--------|
| id | String (UUID) | Yes | ID duy nhất |
| hikeId | String | Yes | ID chuyến đi (Foreign key) |
| observation | String | Yes | Nội dung quan sát |
| time | Date | Yes | Thời gian ghi nhận |
| comments | String | No | Ghi chú bổ sung |
| category | String | No | Danh mục: wildlife, weather, trail_conditions, scenery, other |
| imageUrl | String | No | URL hình ảnh |
| userId | String | No | ID người dùng |
| createdAt | Date | Yes | Thời gian tạo |
| updatedAt | Date | Yes | Thời gian cập nhật |

**Mối quan hệ**:
- Nhiều Observations thuộc về một Hike
- Cascade delete: Xóa Hike sẽ xóa tất cả Observations liên quan

---

## 4. MÀN HÌNH VÀ CHỨC NĂNG

### 4.1. MainActivity (Màn hình chính)
**Mục đích**: Màn hình chủ đạo, điểm khởi đầu của ứng dụng

**Giao diện**:
- Toolbar với tiêu đề "M-Hiking"
- TextView hiển thị số lượng chuyến đi
- 5 nút chính:
  - **Add New Hike**: Thêm chuyến đi mới
  - **View All Hikes**: Xem danh sách chuyến đi
  - **Search Hikes**: Tìm kiếm chuyến đi
  - **Sync Now**: Đồng bộ với server
  - **Delete All**: Xóa tất cả dữ liệu

**Chức năng**:
- Hiển thị tổng số chuyến đi hiện có
- Điều hướng đến các màn hình khác
- Kiểm tra WiFi trước khi sync
- Xác nhận trước khi xóa tất cả dữ liệu
- Auto-refresh khi quay lại từ màn hình khác (onResume)

**Xử lý đồng bộ**:
```
1. Kiểm tra WiFi connection
2. Nếu không có WiFi → hiển thị thông báo
3. Nếu có WiFi → gọi SyncService
4. Hiển thị kết quả sync (success/error)
```

### 4.2. HikeListActivity (Danh sách chuyến đi)
**Mục đích**: Hiển thị tất cả các chuyến đi đã lưu

**Giao diện**:
- RecyclerView với LinearLayoutManager
- Mỗi item hiển thị:
  - Tên chuyến đi
  - Địa điểm
  - Ngày thực hiện (format: dd/MM/yyyy)
  - Độ dài (km)
  - Độ khó (với màu sắc phân biệt)
  - Có chỗ đỗ xe (Yes/No)
  - Trạng thái đồng bộ
  - 3 nút: Edit, Delete, Add Observation
- Empty state message khi không có dữ liệu

**Chức năng**:
- Load tất cả chuyến đi từ database (sắp xếp theo ngày mới nhất)
- Click vào item → xem chi tiết
- Click Edit → mở màn hình chỉnh sửa
- Click Delete → xác nhận và xóa
- Click Add Observation → mở danh sách observations
- Tự động refresh khi quay lại

**Tối ưu**:
- Sử dụng RecyclerView.Adapter để tối ưu hiệu năng
- ViewHolder pattern để tái sử dụng view

### 4.3. AddEditHikeActivity (Thêm/Sửa chuyến đi)
**Mục đích**: Form nhập liệu cho chuyến đi mới hoặc chỉnh sửa chuyến đi cũ

**Giao diện**:
- ScrollView chứa form với các trường:
  - TextInputEditText: Name, Location, Length
  - TextInputEditText (read-only + DatePicker): Date
  - MaterialCheckBox: Parking Available
  - Spinner: Difficulty (Easy, Moderate, Hard, Expert)
  - TextInputEditText (optional): Description, Duration, Weather
- 2 nút: Save, Cancel

**Chức năng**:
- **Add mode**: Tạo chuyến đi mới
  - Tự động generate UUID
  - Set isSynced = false
- **Edit mode**: Cập nhật chuyến đi
  - Load dữ liệu cũ vào form
  - Giữ nguyên ID
  - Update timestamp

**Validation**:
- Name: Bắt buộc, không được rỗng
- Location: Bắt buộc, không được rỗng
- Date: Bắt buộc
- Length: Bắt buộc, phải > 0
- Hiển thị error message ngay trên field

**DatePicker**:
- Click vào Date field → mở DatePickerDialog
- Format hiển thị: dd/MM/yyyy
- Default: ngày hiện tại

### 4.4. SearchActivity (Tìm kiếm)
**Mục đích**: Tìm kiếm chuyến đi theo nhiều tiêu chí

**Giao diện**:
- Switch toggle: Basic Search / Advanced Search
- **Basic Search**:
  - TextInputEditText: Search by name
  - Button: Search
- **Advanced Search**:
  - TextInputEditText: Name, Location
  - TextInputEditText: Min Length, Max Length
  - TextInputEditText (DatePicker): Start Date, End Date
  - Buttons: Search, Clear
- RecyclerView: Hiển thị kết quả
- TextView: Số lượng kết quả
- Empty state: "No results found"

**Chức năng**:
- **Basic Search**: Tìm theo tên (LIKE query)
- **Advanced Search**: Kết hợp nhiều tiêu chí
  - Name: LIKE search
  - Location: LIKE search
  - Length: Range (min-max)
  - Date: Range (start-end)
  - Ít nhất 1 tiêu chí phải được nhập
- Hiển thị số lượng kết quả tìm được
- Click vào kết quả → xem chi tiết
- Có thể Edit/Delete trực tiếp từ kết quả

**Tối ưu**:
- Dynamic SQL query building
- Chỉ add điều kiện nào có giá trị

### 4.5. HikeDetailsActivity (Chi tiết chuyến đi)
**Mục đích**: Hiển thị toàn bộ thông tin chi tiết của một chuyến đi

**Giao diện**:
- ScrollView hiển thị:
  - Tên chuyến đi (header lớn)
  - Icon + Location
  - Icon + Date
  - Icon + Length
  - Icon + Difficulty (với màu sắc)
  - Icon + Parking status
  - Description (nếu có)
  - Estimated Duration (nếu có)
  - Weather Conditions (nếu có)
  - Sync status
- Buttons:
  - Edit Hike
  - Delete Hike
  - View Observations
  - Add Observation

**Chức năng**:
- Load chi tiết từ database theo ID
- Edit → mở AddEditHikeActivity
- Delete → xác nhận và xóa (cùng với observations)
- View Observations → mở ObservationListActivity
- Add Observation → mở AddEditObservationActivity

### 4.6. ObservationListActivity (Danh sách quan sát)
**Mục đích**: Hiển thị tất cả các quan sát của một chuyến đi

**Giao diện**:
- Toolbar: Hiển thị tên chuyến đi
- RecyclerView với các observation items:
  - Nội dung quan sát
  - Thời gian (format: dd/MM/yyyy HH:mm)
  - Category (nếu có)
  - Comments (nếu có)
  - Buttons: Edit, Delete
- FAB (Floating Action Button): Thêm observation mới
- Empty state: "No observations yet"

**Chức năng**:
- Load observations theo hikeId
- Sắp xếp theo thời gian mới nhất
- Add → mở form nhập
- Edit → mở form chỉnh sửa
- Delete → xác nhận và xóa

### 4.7. AddEditObservationActivity (Thêm/Sửa quan sát)
**Mục đích**: Form nhập liệu cho quan sát

**Giao diện**:
- TextInputEditText: Observation content (required)
- TextInputEditText (read-only + TimePicker): Time
- Spinner: Category
- TextInputEditText: Additional comments
- Buttons: Save, Cancel

**Chức năng**:
- Auto-fill thời gian hiện tại
- Validate: Observation content bắt buộc
- Save → lưu vào database
- Link với hikeId

**Categories**:
- Wildlife
- Weather
- Trail Conditions
- Scenery
- Other

---

## 5. TÍNH NĂNG CHÍNH

### 5.1. CRUD Operations (Hikes)
**Create**:
- Form validation đầy đủ
- Auto-generate UUID
- Set timestamps
- Set isSynced = false

**Read**:
- Get all hikes (sorted by date DESC)
- Get by ID
- Search by name
- Advanced search (multiple criteria)

**Update**:
- Load existing data
- Preserve ID
- Update timestamp
- Set isSynced = false (cần sync lại)

**Delete**:
- Confirmation dialog
- Cascade delete observations
- Update UI immediately

### 5.2. CRUD Operations (Observations)
**Create**:
- Linked to hikeId
- Auto-fill current time
- Categories selection

**Read**:
- Get by hikeId
- Sorted by time DESC

**Update**:
- Preserve ID and hikeId
- Update timestamp

**Delete**:
- Confirmation dialog
- Update count in parent screen

### 5.3. Search & Filter
**Basic Search**:
- Input: Search text
- Query: `WHERE name LIKE '%query%'`
- Real-time results

**Advanced Search**:
- Multiple criteria combination
- Dynamic query building
- Support:
  - Name (LIKE)
  - Location (LIKE)
  - Length range (>=, <=)
  - Date range (>=, <=)

**Example query**:
```sql
SELECT * FROM hikes 
WHERE name LIKE '%mountain%' 
  AND location LIKE '%vietnam%'
  AND length >= 5.0 
  AND length <= 15.0
  AND date >= 1234567890
  AND date <= 9876543210
ORDER BY date DESC
```

### 5.4. Data Synchronization (Cập nhật theo backend mới)
**Nguyên tắc**:
- Offline-first architecture
- Chỉ sync khi có WiFi
- Thiết bị định danh bằng header `x-device-id` (ANDROID_ID)
- Upload theo batch, download hợp nhất (upsert)

**Quy trình Sync Upload**:
```
1. Kiểm tra WiFi
2. Lấy danh sách hikes chưa sync và toàn bộ observations
3. POST /api/sync/upload với payload:
   {
     "hikes": [ { id, name, location, date, parking_available, length, difficulty, description } ],
     "observations": [ { id, hike_id, observation, time, comments, category } ]
   }
4. Nếu thành công → đánh dấu is_synced = true cho các hikes tương ứng
```

**Quy trình Sync Download**:
```
1. Kiểm tra WiFi
2. GET /api/sync/download (có thể thêm lastSync sau này)
3. Parse JSON trả về → upsert vào SQLite:
   - Nếu tồn tại → update
   - Nếu chưa có → insert
4. Cập nhật lại UI (ví dụ số lượng hikes)
```

**API Endpoints**:
- `POST /api/sync/upload`: Batch upload hikes + observations
- `GET /api/sync/download`: Tải dữ liệu về máy và hợp nhất

**Error handling**:
- Network errors → Show error message
- Auth errors → Prompt login
- Server errors → Show error details

### 5.5. Offline Support
**Strategies**:
1. **Local Database First**: 
   - Tất cả thao tác ghi vào SQLite trước
   - UI update ngay lập tức
   
2. **Sync Later**:
   - Đánh dấu data chưa sync (isSynced = false)
   - User chủ động sync khi có WiFi
   
3. **Conflict Resolution**:
   - Hiện tại: Last-write-wins
   - Server data sẽ ghi đè local data khi conflict

### 5.6. Data Persistence
**SQLite Database**:
- Name: `MHiking.db`
- Version: 1
- Tables: hikes, observations
- Foreign key constraints enabled
- Cascade delete enabled

**SharedPreferences**:
- Name: `MHikingPrefs`
- Lưu: user settings (không còn lưu token)

---

## 6. CÔNG NGHỆ VÀ THƯ VIỆN

### 6.1. Core Technologies
| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| Android SDK | 36 (min: 24) | Platform phát triển |
| Java | 11 | Ngôn ngữ lập trình |
| Gradle | 8.13.0 | Build system |

### 6.2. Android Jetpack & UI Libraries
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| AppCompat | 1.7.1 | Backward compatibility |
| Material Components | 1.13.0 | Material Design UI |
| ConstraintLayout | 2.2.1 | Flexible layouts |
| RecyclerView | 1.3.2 | Efficient list rendering |
| CardView | 1.0.0 | Card-based UI |

### 6.3. Network & Data Libraries
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| OkHttp | 4.12.0 | HTTP client |
| Logging Interceptor | 4.12.0 | Network logging |
| Gson | 2.10.1 | JSON parsing |

### 6.4. Database
| Công nghệ | Mục đích |
|-----------|----------|
| SQLite | Local database |
| SQLiteOpenHelper | Database management |
| Custom DAO | Data access layer |

### 6.5. Testing Libraries
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| JUnit | 4.13.2 | Unit testing |
| AndroidX Test | 1.3.0 | Android testing |
| Espresso | 3.7.0 | UI testing |

---

## 7. CƠ SỞ DỮ LIỆU

### 7.1. Database Schema

**Database Name**: `MHiking.db`  
**Version**: 1

#### Table: hikes
```sql
CREATE TABLE hikes (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    location TEXT NOT NULL,
    date INTEGER NOT NULL,
    parking_available INTEGER DEFAULT 0,
    length REAL,
    difficulty TEXT NOT NULL,
    description TEXT,
    estimated_duration TEXT,
    weather_conditions TEXT,
    user_id TEXT,
    is_synced INTEGER DEFAULT 0,
    created_at INTEGER,
    updated_at INTEGER
)
```

**Indexes**: None (có thể cải thiện)

**Constraints**:
- id: PRIMARY KEY
- name, location, date, difficulty: NOT NULL

#### Table: observations
```sql
CREATE TABLE observations (
    id TEXT PRIMARY KEY,
    hike_id TEXT NOT NULL,
    observation TEXT NOT NULL,
    time INTEGER NOT NULL,
    comments TEXT,
    category TEXT,
    image_url TEXT,
    user_id TEXT,
    created_at INTEGER,
    updated_at INTEGER,
    FOREIGN KEY(hike_id) REFERENCES hikes(id) ON DELETE CASCADE
)
```

**Indexes**: None (nên thêm index cho hike_id)

**Constraints**:
- id: PRIMARY KEY
- hike_id: FOREIGN KEY → hikes(id)
- ON DELETE CASCADE: Xóa hike → xóa observations

### 7.2. DAO Pattern Implementation

#### HikeDAO Methods
```java
// Create
long insertHike(Hike hike)

// Read
List<Hike> getAllHikes()
Hike getHikeById(String id)
List<Hike> searchHikesByName(String query)
List<Hike> advancedSearch(...)
List<Hike> getUnsyncedHikes()

// Update
int updateHike(Hike hike)

// Delete
int deleteHike(String id)
void deleteAllHikes()
```

#### ObservationDAO Methods
```java
// Create
long insertObservation(Observation observation)

// Read
List<Observation> getObservationsByHikeId(String hikeId)
List<Observation> getAllObservations()
Observation getObservationById(String id)

// Update
int updateObservation(Observation observation)

// Delete
int deleteObservation(String id)
int deleteObservationsByHikeId(String hikeId)
```

### 7.3. Database Helpers

**Singleton Pattern**:
```java
DatabaseHelper.getInstance(context)
```
- Đảm bảo chỉ có 1 instance
- Thread-safe
- Tránh memory leaks

**onConfigure**:
- Enable foreign key constraints
- Thực thi trước onCreate/onUpgrade

**onUpgrade**:
- Drop tables
- Recreate (không migrate data - nên cải thiện)

---

## 8. MẠNG VÀ ĐỒNG BỘ HÓA

### 8.1. Network Architecture

#### ApiClient (Singleton)
**Base URL**: `http://10.0.2.2:3000/api/`  
(10.0.2.2 = localhost cho Android Emulator)

**Configuration**:
```java
- Timeouts: 30 seconds (connect, read, write)
- HTTP Logging: Level.BODY (development)
- Device Interceptor: Auto-add header x-device-id (ANDROID_ID)
- Content-Type: application/json
```

**Features**:
- Auto-attach x-device-id cho mọi request
- Request/Response logging
- Centralized JSON parsing (Gson)

> Không còn token-based authentication; không lưu token.

### 8.2. Sync Service

#### WiFi Check
```java
boolean isWifiConnected()
```
- Check ConnectivityManager
- Verify network type = TYPE_WIFI
- Required before sync

#### Sync Process
```java
void syncData(SyncCallback callback)
```

**Flow (cập nhật)**:
1. Check WiFi → Error if no WiFi
2. Gather unsynced hikes + all observations
3. POST `/api/sync/upload` (batch)
4. If success → mark hikes synced locally
5. GET `/api/sync/download` → upsert vào SQLite (merge)

**Async Execution**:
- Uses ExecutorService
- Single thread executor
- Callbacks on main thread (runOnUiThread)

#### Sync Callbacks
```java
interface SyncCallback {
    void onSuccess(String message)
    void onError(String error)
}
```

#### Data Format (JSON)

**Hike Sync**:
```json
{
  "id": "uuid-string",
  "name": "Mountain Trek",
  "location": "Vietnam",
  "date": 1234567890000,
  "parking_available": true,
  "length": 12.5,
  "difficulty": "moderate",
  "description": "Beautiful trail..."
}
```

**Observation Sync**:
```json
{
  "observation": "Saw a deer",
  "time": 1234567890000,
  "comments": "Near the summit",
  "category": "wildlife"
}
```

### 8.3. Network Security

**Permissions**:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

**Cleartext Traffic**: Enabled (cho development)
```xml
android:usesCleartextTraffic="true"
```
⚠️ **Warning**: Nên disable trong production, dùng HTTPS

**Network Security Config**: Chưa có (nên thêm)

### 8.4. Error Handling

**Network Errors**:
- IOException → "Sync failed: Network error"
- Timeout → "Sync failed: Request timeout"

**Auth Errors**:
- 401 Unauthorized → "Not logged in. Please login first."
- No token → "Not logged in. Please login first."

**WiFi Errors**:
- No WiFi → "WiFi not connected. Sync requires WiFi connection."

**Server Errors**:
- 4xx/5xx → Show error message from server
- Parse error → "Sync failed: Invalid response"

---

## 9. GIAO DIỆN NGƯỜI DÙNG

### 9.1. Design System

#### Color Scheme
**Primary Colors**:
- Primary: `#2E7D32` (Green 800) - Đại diện cho thiên nhiên
- Primary Dark: `#1B5E20` (Green 900)
- Primary Light: `#4CAF50` (Green 500)
- Accent: `#FF6F00` (Orange 900) - CTA buttons

**Difficulty Colors**:
- Easy: `#4CAF50` (Green)
- Moderate: `#FFC107` (Yellow)
- Hard: `#FF9800` (Orange)
- Expert: `#F44336` (Red)

**Status Colors**:
- Synced: `#4CAF50` (Green)
- Not Synced: `#FF9800` (Orange)
- Error: `#F44336` (Red)

**Background**:
- Background: `#F5F5F5` (Grey 100)
- Surface: `#FFFFFF` (White)
- Card: `#FFFFFF` (White)

#### Typography
- Title: Material Toolbar default
- Body: TextInputEditText default
- Labels: Material TextInputLayout hints
- Buttons: MaterialButton text

#### Spacing
- Card elevation: 4dp
- Card margin: 8dp
- Padding: 16dp (standard)
- Button spacing: 8dp

### 9.2. Material Components Used

**Buttons**:
- `MaterialButton`: Primary actions
- FAB: Add new items

**Input Fields**:
- `TextInputLayout` + `TextInputEditText`: All text inputs
- Material error styling

**Selections**:
- `Spinner`: Dropdown selections
- `MaterialCheckBox`: Boolean inputs
- `SwitchMaterial`: Toggle states
- `DatePickerDialog`: Date selection

**Lists**:
- `RecyclerView`: Scrollable lists
- `CardView`: List items
- `LinearLayoutManager`: Vertical lists

**Navigation**:
- `MaterialToolbar`: App bar
- Up navigation: Back to parent
- Intent navigation: Between screens

### 9.3. Layouts

#### activity_main.xml
- ConstraintLayout (root)
- MaterialToolbar
- ScrollView
  - LinearLayout (vertical)
    - TextView (hike count)
    - 6 MaterialButtons (Add, View, Search, Sync, Download, Delete All)

#### activity_hike_list.xml
- LinearLayout (root)
- MaterialToolbar
- RecyclerView
- TextView (empty state)

#### activity_add_edit_hike.xml
- LinearLayout (root)
- MaterialToolbar
- ScrollView
  - LinearLayout (vertical)
    - Multiple TextInputLayouts
    - Spinner
    - CheckBox
    - Button group

#### activity_search.xml
- LinearLayout (root)
- MaterialToolbar
- ScrollView
  - LinearLayout (vertical)
    - SwitchMaterial
    - Basic search layout
    - Advanced search layout
    - Results header
    - RecyclerView
    - Empty state

#### item_hike.xml (RecyclerView item)
- CardView (root)
- LinearLayout
  - TextViews (hike info)
  - Difficulty badge
  - Sync status indicator
  - Button group (Edit, Delete, Observations)

#### item_observation.xml
- CardView (root)
- LinearLayout
  - TextView (observation)
  - TextView (time)
  - TextView (category)
  - TextView (comments)
  - Button group (Edit, Delete)

### 9.4. User Experience Features

#### Loading States
- Toast messages for operations
- Sync progress indication

#### Empty States
- "No hikes found" - HikeListActivity
- "No observations yet" - ObservationListActivity
- "No results found" - SearchActivity

#### Error States
- Field validation errors (inline)
- Network error toasts
- Sync error messages

#### Confirmation Dialogs
- Delete hike confirmation
- Delete observation confirmation
- Delete all data confirmation

#### Auto-refresh
- onResume() in list activities
- After CRUD operations
- After sync completion

#### Accessibility
- Content descriptions (cần cải thiện)
- Touch targets >= 48dp
- Color contrast ratios

---

## 10. YÊU CẦU HỆ THỐNG

### 10.1. Android Requirements

**Minimum SDK**: 24 (Android 7.0 Nougat)
- Release date: August 2016
- Market coverage: ~99% devices

**Target SDK**: 36 (Android 14)
- Latest features & optimizations
- Modern behavior changes

**Compile SDK**: 36

**Architecture Support**: 
- ARM64-v8a (primary)
- ARMv7 (compatible)
- x86, x86_64 (emulator)

### 10.2. Hardware Requirements

**Minimum**:
- RAM: 1 GB
- Storage: 50 MB
- Screen: 4.0" (480x800)
- Network: WiFi required for sync

**Recommended**:
- RAM: 2 GB+
- Storage: 100 MB
- Screen: 5.0" (720x1280)
- Network: WiFi 4 or higher

### 10.3. Network Requirements

**Sync Operations**:
- WiFi connection required
- Minimum speed: 1 Mbps
- Base URL: http://10.0.2.2:3000/api/

**Offline Mode**:
- Full functionality without network
- Sync later when WiFi available

### 10.4. Permissions Required

```xml
<!-- Required -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

**Runtime Permissions**: None (all permissions are normal)

### 10.5. Build Configuration

**Build Tools**: Android Gradle Plugin 8.13.0
**Java Version**: 11 (sourceCompatibility & targetCompatibility)
**ProGuard**: Disabled (release builds)
**Multidex**: Not required (method count < 64K)

**Debug Build**:
```
applicationId: com.example.user_frontend
versionCode: 1
versionName: 1.0
debuggable: true
minifyEnabled: false
```

**Release Build** (nếu có):
```
Same as debug but:
debuggable: false
minifyEnabled: false (should enable)
```

---

## 11. KẾ HOẠCH CẢI TIẾN ĐỀ XUẤT

### 11.1. Security Improvements
- [ ] Enable HTTPS for production
- [ ] Add Network Security Config
- [ ] Implement certificate pinning
- [ ] Encrypt sensitive data in SharedPreferences
- [ ] Add ProGuard/R8 obfuscation

### 11.2. Performance Optimizations
- [ ] Add database indexes (hike_id, date)
- [ ] Implement pagination for large lists
- [ ] Add image caching for observations
- [ ] Use RoomDatabase instead of raw SQLite
- [ ] Implement background sync with WorkManager

### 11.3. Feature Enhancements
- [ ] Photo upload for observations
- [ ] GPS tracking during hike
- [ ] Map integration (Google Maps)
- [ ] Export data to PDF/Excel
- [ ] Social sharing features
- [ ] Offline maps support
- [ ] Weather API integration

### 11.4. Code Quality
- [ ] Add unit tests (DAO, Models)
- [ ] Add UI tests (Espresso)
- [ ] Add integration tests
- [ ] Implement MVVM architecture
- [ ] Use Dependency Injection (Hilt/Dagger)
- [ ] Add Kotlin coroutines for async operations

### 11.5. UX Improvements
- [ ] Add loading indicators
- [ ] Implement pull-to-refresh
- [ ] Add search suggestions
- [ ] Improve empty states with illustrations
- [ ] Add onboarding tutorial
- [ ] Implement dark mode
- [ ] Add animations and transitions

### 11.6. Database Improvements
- [ ] Implement proper migration strategy
- [ ] Add full-text search
- [ ] Add database versioning
- [ ] Implement database backup/restore
- [ ] Add data export/import

### 11.7. Sync Improvements
- [ ] Auto-sync on WiFi connect
- [ ] Conflict resolution strategy
- [ ] Partial sync (only changed data)
- [ ] Sync queue management
- [ ] Retry failed syncs

---

## 12. HƯỚNG DẪN BUILD VÀ CHẠY

### 12.1. Prerequisites
1. **Android Studio**: Arctic Fox or later
2. **JDK**: 11 or higher
3. **Android SDK**: API Level 36
4. **Emulator/Device**: Android 7.0+ (API 24+)

### 12.2. Build Steps

**Clone & Open**:
```bash
# Clone repository
git clone <repository-url>

# Open in Android Studio
File → Open → Select user-frontend folder
```

**Sync Dependencies**:
```bash
# In Android Studio
File → Sync Project with Gradle Files
```

**Configure Backend**:
```java
// In ApiClient.java, update BASE_URL if needed
private static final String BASE_URL = "http://10.0.2.2:3000/api/";
// 10.0.2.2 = localhost for emulator
// For real device: use computer's IP address
```

**Build**:
```bash
# Debug build
./gradlew assembleDebug

# Or use Android Studio
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

**Install & Run**:
```bash
# Install to connected device
./gradlew installDebug

# Or use Android Studio
Run → Run 'app'
```

### 12.3. Testing

**Run Unit Tests**:
```bash
./gradlew test
```

**Run Instrumented Tests**:
```bash
./gradlew connectedAndroidTest
```

**Test Coverage**:
```bash
./gradlew jacocoTestReport
```

### 12.4. APK Location

**Debug APK**:
```
app/build/intermediates/apk/debug/app-debug.apk
```

**Output Metadata**:
```
app/build/intermediates/apk/debug/output-metadata.json
```

---

## 13. TROUBLESHOOTING

### 13.1. Common Issues

**Issue**: "Cleartext HTTP traffic not permitted"
```
Solution: 
1. Check android:usesCleartextTraffic="true" in manifest
2. Or add network_security_config.xml
```

**Issue**: "Unable to connect to server"
```
Solution:
1. Check backend is running
2. Verify BASE_URL (10.0.2.2 for emulator)
3. For real device, use computer's IP
4. Check firewall settings
```

**Issue**: "Database locked"
```
Solution:
1. Close database connections properly
2. Use singleton DatabaseHelper
3. Avoid multiple writes simultaneously
```

**Issue**: "Out of memory"
```
Solution:
1. Enable multidex if needed
2. Optimize images
3. Add pagination for large lists
```

### 13.2. Debug Tips

**Enable Logging**:
```java
// OkHttp logging is already enabled in ApiClient
HttpLoggingInterceptor.Level.BODY
```

**Database Inspection**:
```bash
# In Android Studio
View → Tool Windows → App Inspection → Database Inspector
```

**Network Inspection**:
```bash
# In Android Studio
View → Tool Windows → App Inspection → Network Inspector
```

---

## 14. KẾT LUẬN

### 14.1. Điểm Mạnh
✅ **Architecture**:
- Clean separation of concerns
- DAO pattern implementation
- Singleton for database & API

✅ **Offline Support**:
- Full CRUD without network
- Automatic sync when WiFi available
- Clear sync status indication

✅ **User Experience**:
- Material Design compliance
- Intuitive navigation
- Comprehensive validation
- Clear error messages

✅ **Data Management**:
- SQLite with foreign keys
- Cascade delete
- Timestamp tracking
- UUID for unique IDs

### 14.2. Điểm Cần Cải Thiện
⚠️ **Security**:
- HTTP instead of HTTPS
- No data encryption
- No certificate pinning

⚠️ **Performance**:
- No database indexes
- No pagination
- Raw SQLite (should use Room)

⚠️ **Testing**:
- Limited test coverage
- No UI tests
- No integration tests

⚠️ **Features**:
- No photo upload
- No GPS tracking
- No offline maps

### 14.3. Tổng Kết
Đây là một ứng dụng Android well-structured với kiến trúc rõ ràng, tính năng đầy đủ cho mục đích quản lý hiking trips. Code được tổ chức tốt, dễ maintain và extend. Tuy nhiên, vẫn cần improvements về security, performance và testing để đưa vào production.

**Thời gian phát triển ước tính**: 2-3 tuần (1 developer)  
**Độ phức tạp**: Medium  
**Khả năng mở rộng**: High  
**Maintainability**: Good  

---

**Document Version**: 1.0  
**Last Updated**: October 2025  
**Author**: Technical Documentation Team  
**Contact**: [Your contact information]


# MediX API Contract

Version: **v1 (Part 2)**. This file and the records in `medix-common` are the contract between
`medix-client` and `medix-server`. The server must accept and return exactly these DTOs.
Any DTO change must update this file and both sides.

- DTOs: `com.medix.common.dto.*` (records), enums: `com.medix.common.enums.*`
- Client interfaces: `com.medix.client.api.service.*` (one method per endpoint, Javadoc names the endpoint)

---

## 1. Conventions

| Topic | Rule |
|---|---|
| Base URL | `server.url` from `client.properties` (default `http://localhost:8080`). All paths start with `/api`. |
| Format | JSON, UTF-8, `Content-Type: application/json`. Only exception: CSV import (`multipart/form-data`). |
| JSON library | Jackson 3 on both sides. Unknown properties are ignored when reading. |
| Field names | camelCase, exactly the record component names. |
| Enums | Constant name as string, e.g. `"PHARMACY_OWNER"`, `"READY_FOR_PICKUP"`. |
| Dates/times | ISO-8601 strings, never numbers. `Instant` → `"2026-10-31T16:05:00Z"` (UTC). `LocalDate` → `"2027-03-31"`. `LocalTime` → `"09:00"` or `"09:00:00"`. `DayOfWeek` → `"MONDAY"`. |
| Money | `BigDecimal` JSON numbers with 2 decimals, single currency (BDT). |
| Distance | `double` kilometres, rounded to 2 decimals, straight-line (PostGIS geography distance). |
| Auth header | `Authorization: Bearer <accessToken>` on every non-public endpoint. |
| Pagination | Query params `page` (0-based, default 0) and `size` (default 20, max 100, audit log default 50). Response is `PageResponse<T>`. |
| Optional query params | Omitted or empty = no filter. Boolean filters default to `false`. |
| Success codes | `200 OK` with body; `201 Created` with body for creates; `204 No Content` for void operations. |
| Errors | Every non-2xx response has an `ApiError` body (section 3). |
| Time zone | "Open now" and "today" use the server setting `medix.timezone` (default `Asia/Dhaka`). |

### Roles column

| Value | Meaning |
|---|---|
| public | No token needed. A token, if sent, is still validated. |
| authenticated | Any logged-in role. |
| CUSTOMER / PHARMACY_OWNER / ADMIN | That role only. |
| PHARMACY_OWNER (own) | Owner of the pharmacy in the path (or of the resource). Other owners → `403 FORBIDDEN`. |

### Business rules both sides rely on

| Rule | Value |
|---|---|
| Access token lifetime | 15 minutes (`expiresInSeconds = 900`) |
| Refresh token lifetime | 7 days, single use, rotated on every refresh, stored in Redis |
| Pickup window | Reservation `expiresAt = createdAt + 2 hours`; scheduler sets `EXPIRED` and releases stock |
| Active reservations per customer | max 5 (`RESERVATION_LIMIT_REACHED`) |
| Reservation lines | 1-10 lines, 1-20 units each, one pharmacy per reservation |
| Pickup code | 6 characters from `A-Z` and `2-9` (no 0/1/O/I), unique among active reservations of a pharmacy; matched case-insensitively |
| Available quantity | `quantity - reservedQuantity` of non-expired batches |
| Low stock | `availableQuantity <= lowStockThreshold` (default threshold 10) |
| Expiring soon | expiry date within 30 days |
| Expired batches | never counted in search or reservations |
| Reservation stock allocation | earliest-expiry batch first (FEFO); price fixed at reservation time |
| Searchable pharmacies | status `APPROVED` only |
| Search cache | availability results cached in Redis for 60 s, evicted on stock changes of that medicine |

---

## 2. Shared types

### PageResponse&lt;T&gt;
```json
{ "content": [ ... ], "page": 0, "size": 20, "totalElements": 57, "totalPages": 3 }
```

### CountResponse
```json
{ "count": 4 }
```

### Enums

| Enum | Values |
|---|---|
| `Role` | CUSTOMER, PHARMACY_OWNER, ADMIN |
| `PharmacyStatus` | PENDING, APPROVED, REJECTED, SUSPENDED |
| `ReservationStatus` | PENDING, CONFIRMED, READY_FOR_PICKUP, COMPLETED, CANCELLED, EXPIRED |
| `MovementType` | STOCK_IN, CSV_IMPORT, SALE, ADJUSTMENT, EXPIRED_REMOVAL, RESERVATION_HOLD, RESERVATION_RELEASE, RESERVATION_FULFILLED |
| `AlertType` | LOW_STOCK, OUT_OF_STOCK, EXPIRING_SOON, EXPIRED, RESERVATION_CREATED, RESERVATION_STATUS_CHANGED, PHARMACY_PENDING_REVIEW, PHARMACY_STATUS_CHANGED |
| `DosageForm` | TABLET, CAPSULE, SYRUP, SUSPENSION, INJECTION, CREAM, OINTMENT, DROPS, INHALER, POWDER, OTHER |
| `SearchSort` | DISTANCE (default), PRICE, QUANTITY |
| `ErrorCode` | see section 3 |

### Reservation status transitions

```
PENDING ──► CONFIRMED ──► READY_FOR_PICKUP ──► COMPLETED
   │            │                │
   └────────────┴────────────────┴──► CANCELLED  (customer or owner)
                                  └──► EXPIRED    (scheduler only)
```
- Owner may set CONFIRMED, READY_FOR_PICKUP, CANCELLED via the status endpoint.
- COMPLETED only through pickup-code verification (allowed from CONFIRMED or READY_FOR_PICKUP).
- Customer may cancel while PENDING, CONFIRMED or READY_FOR_PICKUP.
- Any other change → `409 INVALID_STATUS_TRANSITION`. Implemented in `ReservationStatus.canTransitionTo`.

### Pharmacy status transitions
- New pharmacy → PENDING. Admin: PENDING → APPROVED / REJECTED; APPROVED ↔ SUSPENDED; REJECTED → APPROVED.
- Owner edit of a REJECTED pharmacy → PENDING. Owner edit of APPROVED keeps APPROVED.
- REJECTED and SUSPENDED need a `reason`.

---

## 3. Errors

### ApiError body
```json
{
  "timestamp": "2026-10-31T16:05:00Z",
  "status": 400,
  "code": "VALIDATION_FAILED",
  "message": "Request has invalid fields",
  "path": "/api/reservations",
  "fieldErrors": [
    { "field": "items[0].quantity", "message": "must be less than or equal to 20" }
  ]
}
```
`fieldErrors` is omitted when empty. Record-level checks (`@AssertTrue` methods) report the property name,
e.g. `"field": "medicinesUnique"`.

### Error codes

| Code | HTTP | When |
|---|---|---|
| VALIDATION_FAILED | 400 | Bean Validation failed on body or query params (`fieldErrors` filled) |
| MALFORMED_REQUEST | 400 | Unreadable JSON, wrong type, bad enum value, missing multipart part |
| CSV_INVALID_FORMAT | 400 | CSV file empty, not UTF-8, or header missing/wrong (row errors go in the report instead) |
| UNAUTHORIZED | 401 | No/invalid access token on a protected endpoint |
| INVALID_CREDENTIALS | 401 | Wrong email or password |
| TOKEN_EXPIRED | 401 | Access token expired → client calls `/api/auth/refresh` once and retries |
| INVALID_REFRESH_TOKEN | 401 | Refresh token unknown, used, expired or revoked → client goes to login |
| FORBIDDEN | 403 | Role not allowed, or not the owner of the resource |
| ACCOUNT_DISABLED | 403 | Login or refresh by a disabled user |
| NOT_FOUND | 404 | Resource does not exist or is not visible to the caller |
| EMAIL_ALREADY_EXISTS | 409 | Register with a used email |
| LICENSE_ALREADY_EXISTS | 409 | Pharmacy license number already registered |
| MEDICINE_ALREADY_EXISTS | 409 | Same name + strength + dosageForm already in catalog |
| MEDICINE_IN_USE | 409 | Deleting a medicine used by inventory or reservations |
| PHARMACY_NOT_APPROVED | 409 | Reserving at, or adding stock to, a non-APPROVED pharmacy* |
| INSUFFICIENT_STOCK | 409 | Reservation or adjustment exceeds available quantity; deleting a batch with reserved units |
| INVALID_STATUS_TRANSITION | 409 | Reservation or pharmacy status change not allowed |
| RESERVATION_LIMIT_REACHED | 409 | Customer already has 5 active reservations |
| CONCURRENT_MODIFICATION | 409 | Optimistic lock `version` mismatch → reload and retry |
| FILE_TOO_LARGE | 413 | CSV upload over 2 MB or 5000 rows |
| INVALID_PICKUP_CODE | 422 | No active reservation with that code at this pharmacy |
| RATE_LIMITED | 429 | Too many requests (login: 10/min per IP+email; geocoding: 30/min per IP) |
| INTERNAL_ERROR | 500 | Unexpected server error (message is generic, details only in server log) |
| GEOCODING_UNAVAILABLE | 503 | Nominatim unreachable |
| NETWORK_ERROR | – | **Client only**: server unreachable / unreadable response. Never sent by the server. |

\* Owners **may** add stock while their pharmacy is PENDING (to prepare before approval); only SUSPENDED and
REJECTED pharmacies get `PHARMACY_NOT_APPROVED` on inventory writes. Reservations require APPROVED.

---

## 4. Endpoints

### 4.1 Auth — `AuthService`

| Method | Path | Request | Response | Roles | Errors |
|---|---|---|---|---|---|
| POST | `/api/auth/register` | `RegisterRequest` | 201 `TokenResponse` | public | VALIDATION_FAILED, EMAIL_ALREADY_EXISTS |
| POST | `/api/auth/register-owner` | `RegisterOwnerRequest` | 201 `TokenResponse` | public | VALIDATION_FAILED, EMAIL_ALREADY_EXISTS, LICENSE_ALREADY_EXISTS |
| POST | `/api/auth/login` | `LoginRequest` | 200 `TokenResponse` | public | VALIDATION_FAILED, INVALID_CREDENTIALS, ACCOUNT_DISABLED, RATE_LIMITED |
| POST | `/api/auth/refresh` | `RefreshTokenRequest` | 200 `TokenResponse` | public | INVALID_REFRESH_TOKEN, ACCOUNT_DISABLED |
| POST | `/api/auth/logout` | `RefreshTokenRequest` | 204 | authenticated | UNAUTHORIZED (an invalid refresh token is ignored) |
| GET | `/api/auth/me` | – | 200 `UserResponse` | authenticated | UNAUTHORIZED, TOKEN_EXPIRED |

**RegisterRequest** — `fullName` (required, ≤100), `email` (required, email, ≤254), `phone` (optional, `@PhoneNumber`), `password` (`@StrongPassword`: 8-72 chars, ≥1 letter, ≥1 digit).

**RegisterOwnerRequest** — same as above but `phone` required, plus `pharmacy: PharmacyRequest` (required, validated). Creates the owner and the pharmacy (PENDING) in one transaction, and a `PHARMACY_PENDING_REVIEW` alert for admins.

**LoginRequest** — `email`, `password` (required).

**RefreshTokenRequest** — `refreshToken` (required, ≤512).

**TokenResponse**
```json
{ "accessToken": "eyJ...", "refreshToken": "c2f1...", "tokenType": "Bearer", "expiresInSeconds": 900,
  "user": { "id": 1, "fullName": "Rahim Uddin", "email": "rahim@example.com", "phone": "+8801711000000",
            "role": "CUSTOMER", "createdAt": "2026-10-31T10:00:00Z" } }
```
JWT claims: `sub` = user id, `email`, `role`, `iat`, `exp`. Emails are stored lower-case.

### 4.2 Medicines — `MedicineService`

| Method | Path | Request | Response | Roles | Errors |
|---|---|---|---|---|---|
| GET | `/api/medicines/suggest?q=&limit=` | – | 200 `List<MedicineSuggestion>` | public | VALIDATION_FAILED (`q` < 2 chars, `limit` not 1-20; default 10) |
| GET | `/api/medicines?q=&page=&size=` | – | 200 `PageResponse<MedicineResponse>` | public | – |
| GET | `/api/medicines/{id}` | – | 200 `MedicineResponse` | public | NOT_FOUND |
| POST | `/api/medicines` | `MedicineRequest` | 201 `MedicineResponse` | ADMIN | VALIDATION_FAILED, MEDICINE_ALREADY_EXISTS |
| PUT | `/api/medicines/{id}` | `MedicineRequest` | 200 `MedicineResponse` | ADMIN | VALIDATION_FAILED, NOT_FOUND, MEDICINE_ALREADY_EXISTS |
| DELETE | `/api/medicines/{id}` | – | 204 | ADMIN | NOT_FOUND, MEDICINE_IN_USE |

**MedicineRequest** — `name` (required, ≤150), `genericName` (≤150), `manufacturer` (≤150), `dosageForm` (required), `strength` (required, ≤50, e.g. `"500 mg"`), `category` (≤100), `prescriptionRequired`, `description` (≤2000).

**MedicineResponse** — `id`, `name`, `genericName`, `manufacturer`, `dosageForm`, `strength`, `category`, `prescriptionRequired`, `description`.

**MedicineSuggestion** — `id`, `name`, `genericName`, `dosageForm`, `strength`. Suggest uses pg_trgm similarity on name and genericName, prefix matches ranked first.

### 4.3 Search — `SearchService`

| Method | Path | Request | Response | Roles | Errors |
|---|---|---|---|---|---|
| GET | `/api/search/availability` | query `AvailabilitySearchQuery` | 200 `PageResponse<AvailabilityResult>` | public | VALIDATION_FAILED |
| GET | `/api/search/pharmacies/nearby?lat=&lng=&radiusKm=` | – | 200 `List<NearbyPharmacyResponse>` | public | VALIDATION_FAILED |

**AvailabilitySearchQuery** (query params) — `q` (2-100 chars) **or** `medicineId` (one required; medicineId wins), `lat` (required, -90..90), `lng` (required, -180..180), `radiusKm` (0.5-50, default 5), `openNowOnly` (default false), `minQuantity` (≥1, default 1), `sort` (`DISTANCE`|`PRICE`|`QUANTITY`, default DISTANCE), `page`, `size`.
With `q`, all catalog medicines matching the name (pg_trgm) are searched and each pharmacy+medicine pair is one result.
Each search increments Redis search statistics for the normalized term (medicine name when `medicineId` is used).

**AvailabilityResult**
```json
{ "pharmacy": { "id": 7, "name": "Lazz Pharma", "addressLine": "House 12, Road 5", "city": "Dhaka",
                "phone": "+8801700000007", "latitude": 23.7806, "longitude": 90.4070 },
  "medicine": { "id": 3, "name": "Napa", "genericName": "Paracetamol", "dosageForm": "TABLET", "strength": "500 mg" },
  "distanceKm": 1.24, "availableQuantity": 120, "price": 1.20, "openNow": true,
  "nearestExpiry": "2027-05-31", "lastUpdated": "2026-10-31T09:12:00Z" }
```
Only rows with `availableQuantity >= minQuantity`. `price` = lowest unit price among counted batches.

**NearbyPharmacyResponse** — `pharmacy: PharmacySummary`, `distanceKm`, `openNow`. Max 100, nearest first. `radiusKm` default 5, range 0.5-50.

### 4.4 Pharmacies — `PharmacyService`

| Method | Path | Request | Response | Roles | Errors |
|---|---|---|---|---|---|
| GET | `/api/pharmacies/{id}` | – | 200 `PharmacyResponse` | public (non-APPROVED: own owner, ADMIN) | NOT_FOUND |
| GET | `/api/pharmacies/mine` | – | 200 `List<PharmacyResponse>` | PHARMACY_OWNER | – |
| POST | `/api/pharmacies` | `PharmacyRequest` | 201 `PharmacyResponse` | PHARMACY_OWNER | VALIDATION_FAILED, LICENSE_ALREADY_EXISTS |
| PUT | `/api/pharmacies/{id}` | `PharmacyRequest` | 200 `PharmacyResponse` | PHARMACY_OWNER (own), ADMIN | VALIDATION_FAILED, NOT_FOUND, FORBIDDEN, LICENSE_ALREADY_EXISTS |

**PharmacyRequest** — `name` (required, ≤150), `licenseNumber` (required, ≤50, unique), `phone` (required), `email` (optional), `addressLine` (required, ≤255), `city` (required, ≤100), `postalCode` (≤20), `latitude`/`longitude` (required), `open24Hours`, `openingHours` (required list, ≤7 entries, one per day; ignored when `open24Hours`). Days missing from the list count as closed.

**OpeningHours** — `day` (`"MONDAY"`…), `opensAt`, `closesAt` (`"HH:mm"`, required unless `closed`), `closed`. `closesAt` before `opensAt` means closing after midnight.
```json
{ "day": "FRIDAY", "opensAt": "15:00", "closesAt": "23:00", "closed": false }
```

**PharmacyResponse** — `id`, `name`, `licenseNumber`, `phone`, `email`, `addressLine`, `city`, `postalCode`, `latitude`, `longitude`, `status`, `statusReason`, `open24Hours`, `openingHours`, `openNow`, `ownerId`, `ownerName`, `createdAt`, `updatedAt`.

**PharmacySummary** — `id`, `name`, `addressLine`, `city`, `phone`, `latitude`, `longitude`.

### 4.5 Inventory — `InventoryService`

All paths are under `/api/pharmacies/{pharmacyId}/inventory`.

| Method | Path | Request | Response | Roles | Errors |
|---|---|---|---|---|---|
| GET | `` (root) `?q=&lowStockOnly=&expiringWithinDays=&page=&size=` | query `InventoryQuery` | 200 `PageResponse<InventoryItemResponse>` | PHARMACY_OWNER (own), ADMIN | FORBIDDEN, NOT_FOUND |
| GET | `/{itemId}` | – | 200 `InventoryItemResponse` | PHARMACY_OWNER (own), ADMIN | FORBIDDEN, NOT_FOUND |
| POST | `` (root) | `AddStockRequest` | 201 `InventoryItemResponse` | PHARMACY_OWNER (own) | VALIDATION_FAILED, FORBIDDEN, NOT_FOUND (medicine), PHARMACY_NOT_APPROVED |
| POST | `/{itemId}/adjust` | `AdjustStockRequest` | 200 `InventoryItemResponse` | PHARMACY_OWNER (own) | VALIDATION_FAILED, FORBIDDEN, NOT_FOUND, INSUFFICIENT_STOCK, CONCURRENT_MODIFICATION, PHARMACY_NOT_APPROVED |
| PATCH | `/{itemId}` | `UpdateInventoryItemRequest` | 200 `InventoryItemResponse` | PHARMACY_OWNER (own) | VALIDATION_FAILED, FORBIDDEN, NOT_FOUND, CONCURRENT_MODIFICATION |
| DELETE | `/{itemId}` | – | 204 | PHARMACY_OWNER (own) | FORBIDDEN, NOT_FOUND, INSUFFICIENT_STOCK (has reserved units) |
| POST | `/import` | multipart, part `file` (text/csv) | 200 `CsvImportReport` | PHARMACY_OWNER (own) | CSV_INVALID_FORMAT, FILE_TOO_LARGE, FORBIDDEN, PHARMACY_NOT_APPROVED |
| GET | `/movements?itemId=&page=&size=` | – | 200 `PageResponse<StockMovementResponse>` | PHARMACY_OWNER (own), ADMIN | FORBIDDEN, NOT_FOUND |

Ordering: inventory list by medicine name, then expiry date; movements newest first.

**InventoryQuery** (query params) — `q` (≤100), `lowStockOnly`, `expiringWithinDays` (0-365, includes expired), `page`, `size`.

**AddStockRequest** — `medicineId` (required), `batchNumber` (required, ≤50), `expiryDate` (required, future), `quantity` (1-100000), `price` (required, ≥0, 8.2 digits), `lowStockThreshold` (optional 0-10000). Same medicine + batchNumber in this pharmacy → quantity added to that batch (expiryDate must match, else VALIDATION_FAILED on `expiryDate`). Movement `STOCK_IN`.

**AdjustStockRequest** — `quantityChange` (non-zero, -100000..100000), `type` (`SALE` | `ADJUSTMENT` | `EXPIRED_REMOVAL`; SALE and EXPIRED_REMOVAL must be negative), `reason` (required, ≤255), `version`. Result may not drop `quantity` below `reservedQuantity` (INSUFFICIENT_STOCK).

**UpdateInventoryItemRequest** — `price` (required, ≥0), `lowStockThreshold` (0-10000), `version`.

**InventoryItemResponse** — `id`, `pharmacyId`, `medicine: MedicineSuggestion`, `batchNumber`, `expiryDate`, `quantity`, `reservedQuantity`, `availableQuantity`, `price`, `lowStockThreshold`, `lowStock`, `expiringSoon`, `expired`, `version`, `updatedAt`.

**CSV import format** — UTF-8, comma-separated, header row required (case-insensitive, order fixed):
```
medicine_name,strength,dosage_form,batch_number,expiry_date,quantity,price,low_stock_threshold
Napa,500 mg,TABLET,B-2026-11,2027-05-31,200,1.20,20
```
Medicine is matched by name + strength + dosage_form (case-insensitive) in the catalog; unknown medicine → row error.
Each valid row works like AddStock (movement `CSV_IMPORT`). Max 2 MB / 5000 rows.

**CsvImportReport**
```json
{ "totalRows": 3, "importedRows": 2, "failedRows": 1,
  "errors": [ { "rowNumber": 3, "column": "expiry_date", "message": "Must be a future date in yyyy-MM-dd",
                "rawLine": "Seclo,20 mg,CAPSULE,S-1,2020-01-01,50,6.00," } ] }
```

**StockMovementResponse** — `id`, `inventoryItemId`, `medicineName`, `batchNumber`, `type`, `quantityChange`, `quantityAfter`, `reason`, `reservationId`, `performedBy`, `createdAt`.

Every stock change re-checks LOW_STOCK / OUT_OF_STOCK and creates an alert when the item crosses the threshold (not on every change while already low).

### 4.6 Reservations — `ReservationService`

| Method | Path | Request | Response | Roles | Errors |
|---|---|---|---|---|---|
| POST | `/api/reservations` | `CreateReservationRequest` | 201 `ReservationResponse` | CUSTOMER | VALIDATION_FAILED, NOT_FOUND, PHARMACY_NOT_APPROVED, INSUFFICIENT_STOCK, RESERVATION_LIMIT_REACHED |
| GET | `/api/reservations/mine?status=&page=&size=` | – | 200 `PageResponse<ReservationResponse>` | CUSTOMER | – |
| GET | `/api/reservations/{id}` | – | 200 `ReservationResponse` | CUSTOMER (own), PHARMACY_OWNER (own pharmacy), ADMIN | NOT_FOUND, FORBIDDEN |
| POST | `/api/reservations/{id}/cancel` | `CancelReservationRequest` | 200 `ReservationResponse` | CUSTOMER (own) | NOT_FOUND, FORBIDDEN, INVALID_STATUS_TRANSITION |
| GET | `/api/pharmacies/{pharmacyId}/reservations?status=&page=&size=` | – | 200 `PageResponse<ReservationResponse>` | PHARMACY_OWNER (own), ADMIN | FORBIDDEN, NOT_FOUND |
| PATCH | `/api/pharmacies/{pharmacyId}/reservations/{id}/status` | `ReservationStatusUpdateRequest` | 200 `ReservationResponse` | PHARMACY_OWNER (own) | VALIDATION_FAILED, FORBIDDEN, NOT_FOUND, INVALID_STATUS_TRANSITION |
| POST | `/api/pharmacies/{pharmacyId}/reservations/complete` | `PickupVerificationRequest` | 200 `ReservationResponse` | PHARMACY_OWNER (own) | VALIDATION_FAILED, FORBIDDEN, INVALID_PICKUP_CODE |

Ordering: newest first.

**CreateReservationRequest** — `pharmacyId` (required), `items` (1-10, unique medicines), `note` (≤500).
**ReservationItemRequest** — `medicineId` (required), `quantity` (1-20).
All lines succeed or none (single transaction, row locks on batches). Creates `RESERVATION_HOLD` movements and a `RESERVATION_CREATED` alert for the owner. Status starts `PENDING`.

**CancelReservationRequest** — `reason` (optional, ≤500). Releases stock (`RESERVATION_RELEASE`).

**ReservationStatusUpdateRequest** — `status` (`CONFIRMED` | `READY_FOR_PICKUP` | `CANCELLED`), `reason` (required for CANCELLED, ≤500).

**PickupVerificationRequest** — `pickupCode` (6 chars `[A-Za-z2-9]`). Completes the active reservation (`CONFIRMED` or `READY_FOR_PICKUP`) with that code at that pharmacy; creates `RESERVATION_FULFILLED` movements.

**ReservationResponse**
```json
{ "id": 42, "pickupCode": "K7P2QX", "status": "PENDING",
  "pharmacy": { "id": 7, "name": "Lazz Pharma", "addressLine": "House 12, Road 5", "city": "Dhaka",
                "phone": "+8801700000007", "latitude": 23.7806, "longitude": 90.4070 },
  "customerId": 1, "customerName": "Rahim Uddin", "customerPhone": "+8801711000000",
  "items": [ { "medicine": { "id": 3, "name": "Napa", "genericName": "Paracetamol", "dosageForm": "TABLET",
                             "strength": "500 mg" },
               "quantity": 10, "unitPrice": 1.20, "lineTotal": 12.00 } ],
  "totalPrice": 12.00, "note": null, "statusReason": null,
  "createdAt": "2026-10-31T10:00:00Z", "expiresAt": "2026-10-31T12:00:00Z", "updatedAt": "2026-10-31T10:00:00Z" }
```
Every status change creates a `RESERVATION_STATUS_CHANGED` alert for the other party.

### 4.7 Alerts — `AlertService`

| Method | Path | Request | Response | Roles | Errors |
|---|---|---|---|---|---|
| GET | `/api/alerts?unreadOnly=&page=&size=` | – | 200 `PageResponse<AlertResponse>` | authenticated | – |
| GET | `/api/alerts/unread-count` | – | 200 `CountResponse` | authenticated | – |
| POST | `/api/alerts/{id}/read` | – | 204 | authenticated (own alert) | NOT_FOUND |
| POST | `/api/alerts/read-all` | – | 204 | authenticated | – |

**AlertResponse** — `id`, `type`, `title`, `message`, `pharmacyId`, `pharmacyName`, `inventoryItemId`, `reservationId`, `read`, `createdAt`. Unrelated ids are null.

Alerts are per user. Owner alerts go to the pharmacy's owner; admin alerts to every ADMIN.
Scheduled jobs (ShedLock): expiry scan daily 06:00 (EXPIRING_SOON / EXPIRED, once per batch per type), reservation expiry every minute.

### 4.8 Admin — `AdminService`

| Method | Path | Request | Response | Roles | Errors |
|---|---|---|---|---|---|
| GET | `/api/admin/dashboard` | – | 200 `DashboardStatsResponse` | ADMIN | – |
| GET | `/api/admin/users?q=&role=&enabled=&page=&size=` | query `AdminUserQuery` | 200 `PageResponse<AdminUserRow>` | ADMIN | VALIDATION_FAILED |
| PATCH | `/api/admin/users/{id}/status` | `UserStatusUpdateRequest` | 200 `AdminUserRow` | ADMIN | VALIDATION_FAILED, NOT_FOUND, FORBIDDEN (self) |
| GET | `/api/admin/pharmacies?status=&q=&page=&size=` | – | 200 `PageResponse<PharmacyResponse>` | ADMIN | – |
| PATCH | `/api/admin/pharmacies/{id}/status` | `PharmacyStatusUpdateRequest` | 200 `PharmacyResponse` | ADMIN | VALIDATION_FAILED, NOT_FOUND, INVALID_STATUS_TRANSITION |
| GET | `/api/admin/audit-log?action=&entityType=&actorId=&from=&to=&page=&size=` | query `AuditLogQuery` | 200 `PageResponse<AuditLogRow>` | ADMIN | VALIDATION_FAILED |

**DashboardStatsResponse** — `totalUsers`, `totalCustomers`, `totalOwners`, `totalPharmacies`, `pendingPharmacies`, `approvedPharmacies`, `totalMedicines`, `activeReservations`, `reservationsToday`, `lowStockItems`, `expiringSoonItems`, `topSearches: List<SearchStat>` (top 10), `reservationsLast7Days: List<DailyCount>`, `searchesLast7Days: List<DailyCount>` (7 entries each, oldest first, today included, zero-filled), `generatedAt`. May be cached up to 60 s.

**SearchStat** — `term`, `count`. **DailyCount** — `date`, `count`.

**AdminUserQuery** — `q` (name/email contains), `role`, `enabled`, `page`, `size`.
**AdminUserRow** — `id`, `fullName`, `email`, `phone`, `role`, `enabled`, `createdAt`, `lastLoginAt`.
**UserStatusUpdateRequest** — `enabled` (required), `reason` (≤500). Disabling revokes the user's refresh tokens.

**PharmacyStatusUpdateRequest** — `status` (required), `reason` (required for REJECTED/SUSPENDED, ≤500). Creates a `PHARMACY_STATUS_CHANGED` alert for the owner.

**AuditLogQuery** — `action`, `entityType` (≤50 each, exact match), `actorId`, `from`, `to` (inclusive dates), `page`, `size` (default 50).
**AuditLogRow** — `id`, `actorId`, `actorEmail`, `action`, `entityType`, `entityId`, `details`, `createdAt`.
Audited actions (minimum): `USER_REGISTERED`, `LOGIN_FAILED`, `USER_ENABLED`, `USER_DISABLED`, `PHARMACY_CREATED`, `PHARMACY_UPDATED`, `PHARMACY_APPROVED`, `PHARMACY_REJECTED`, `PHARMACY_SUSPENDED`, `MEDICINE_CREATED`, `MEDICINE_UPDATED`, `MEDICINE_DELETED`, `INVENTORY_IMPORTED`, `RESERVATION_EXPIRED`.

### 4.9 Geocoding — `GeocodingService`

| Method | Path | Request | Response | Roles | Errors |
|---|---|---|---|---|---|
| GET | `/api/geo/search?q=` | – | 200 `List<GeocodeResult>` (max 5) | public | VALIDATION_FAILED (`q` < 3 chars), RATE_LIMITED, GEOCODING_UNAVAILABLE |
| GET | `/api/geo/reverse?lat=&lng=` | – | 200 `GeocodeResult` | public | VALIDATION_FAILED, NOT_FOUND, RATE_LIMITED, GEOCODING_UNAVAILABLE |

**GeocodeResult** — `displayName`, `latitude`, `longitude`, `city`, `postalCode`.
The server proxies OpenStreetMap Nominatim (with a proper User-Agent, max 1 request/second upstream) and caches results in Redis for 24 h.

---

## 5. Endpoint index

| # | Method | Path | Roles | Client method |
|---|---|---|---|---|
| 1 | POST | /api/auth/register | public | AuthService.register |
| 2 | POST | /api/auth/register-owner | public | AuthService.registerOwner |
| 3 | POST | /api/auth/login | public | AuthService.login |
| 4 | POST | /api/auth/refresh | public | AuthService.refresh |
| 5 | POST | /api/auth/logout | authenticated | AuthService.logout |
| 6 | GET | /api/auth/me | authenticated | AuthService.currentUser |
| 7 | GET | /api/medicines/suggest | public | MedicineService.suggest |
| 8 | GET | /api/medicines | public | MedicineService.list |
| 9 | GET | /api/medicines/{id} | public | MedicineService.get |
| 10 | POST | /api/medicines | ADMIN | MedicineService.create |
| 11 | PUT | /api/medicines/{id} | ADMIN | MedicineService.update |
| 12 | DELETE | /api/medicines/{id} | ADMIN | MedicineService.delete |
| 13 | GET | /api/search/availability | public | SearchService.searchAvailability |
| 14 | GET | /api/search/pharmacies/nearby | public | SearchService.nearbyPharmacies |
| 15 | GET | /api/pharmacies/{id} | public* | PharmacyService.get |
| 16 | GET | /api/pharmacies/mine | PHARMACY_OWNER | PharmacyService.myPharmacies |
| 17 | POST | /api/pharmacies | PHARMACY_OWNER | PharmacyService.create |
| 18 | PUT | /api/pharmacies/{id} | PHARMACY_OWNER (own), ADMIN | PharmacyService.update |
| 19 | GET | /api/pharmacies/{pharmacyId}/inventory | PHARMACY_OWNER (own), ADMIN | InventoryService.list |
| 20 | GET | /api/pharmacies/{pharmacyId}/inventory/{itemId} | PHARMACY_OWNER (own), ADMIN | InventoryService.get |
| 21 | POST | /api/pharmacies/{pharmacyId}/inventory | PHARMACY_OWNER (own) | InventoryService.addStock |
| 22 | POST | /api/pharmacies/{pharmacyId}/inventory/{itemId}/adjust | PHARMACY_OWNER (own) | InventoryService.adjustStock |
| 23 | PATCH | /api/pharmacies/{pharmacyId}/inventory/{itemId} | PHARMACY_OWNER (own) | InventoryService.update |
| 24 | DELETE | /api/pharmacies/{pharmacyId}/inventory/{itemId} | PHARMACY_OWNER (own) | InventoryService.delete |
| 25 | POST | /api/pharmacies/{pharmacyId}/inventory/import | PHARMACY_OWNER (own) | InventoryService.importCsv |
| 26 | GET | /api/pharmacies/{pharmacyId}/inventory/movements | PHARMACY_OWNER (own), ADMIN | InventoryService.movements |
| 27 | POST | /api/reservations | CUSTOMER | ReservationService.create |
| 28 | GET | /api/reservations/mine | CUSTOMER | ReservationService.myReservations |
| 29 | GET | /api/reservations/{id} | CUSTOMER (own), PHARMACY_OWNER (own), ADMIN | ReservationService.get |
| 30 | POST | /api/reservations/{id}/cancel | CUSTOMER (own) | ReservationService.cancel |
| 31 | GET | /api/pharmacies/{pharmacyId}/reservations | PHARMACY_OWNER (own), ADMIN | ReservationService.pharmacyReservations |
| 32 | PATCH | /api/pharmacies/{pharmacyId}/reservations/{id}/status | PHARMACY_OWNER (own) | ReservationService.updateStatus |
| 33 | POST | /api/pharmacies/{pharmacyId}/reservations/complete | PHARMACY_OWNER (own) | ReservationService.completeByPickupCode |
| 34 | GET | /api/alerts | authenticated | AlertService.list |
| 35 | GET | /api/alerts/unread-count | authenticated | AlertService.unreadCount |
| 36 | POST | /api/alerts/{id}/read | authenticated | AlertService.markRead |
| 37 | POST | /api/alerts/read-all | authenticated | AlertService.markAllRead |
| 38 | GET | /api/admin/dashboard | ADMIN | AdminService.dashboard |
| 39 | GET | /api/admin/users | ADMIN | AdminService.users |
| 40 | PATCH | /api/admin/users/{id}/status | ADMIN | AdminService.updateUserStatus |
| 41 | GET | /api/admin/pharmacies | ADMIN | AdminService.pharmacies |
| 42 | PATCH | /api/admin/pharmacies/{id}/status | ADMIN | AdminService.updatePharmacyStatus |
| 43 | GET | /api/admin/audit-log | ADMIN | AdminService.auditLog |
| 44 | GET | /api/geo/search | public | GeocodingService.search |
| 45 | GET | /api/geo/reverse | public | GeocodingService.reverse |

\* non-APPROVED pharmacies only for their owner and ADMIN.

Route note for the server: `/api/pharmacies/mine` and `/api/pharmacies/{pharmacyId}/inventory/movements`
must be mapped so they are not captured by `/{id}` / `/{itemId}` (use numeric path patterns such as `{id:\d+}`).

---

## 6. Change log

| Version | Change |
|---|---|
| v1 | Initial contract (Part 2). |

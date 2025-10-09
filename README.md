# NGS (Next Game Store) - Backend

> 게임 판매 플랫폼 백엔드 API 서버

---

## 📌 프로젝트 소개

NGS(Next Game Store)는 Steam 게임 정보를 기반으로 한 게임 판매 플랫폼입니다.
Steam API를 활용하여 게임 데이터를 수집하고, 사용자에게 게임 검색, 구매, 리뷰 등의 기능을 제공합니다.

### 주요 기능
- 🎮 **Steam 게임 크롤링**: Steam API 연동으로 게임 정보 자동 수집 (한국어 지원)
- 🔍 **게임 검색 및 필터링**: 장르, 가격, 할인율 기반 검색
- 🔐 **소셜 로그인**: Google, Kakao, Naver OAuth2 인증
- 💳 **결제 시스템**: PortOne 연동 게임 구매 및 결제
- ⭐ **리뷰 시스템**: 게임 평점 및 리뷰 작성
- 🛒 **장바구니**: 장바구니 및 위시리스트 관리

---

## 🛠 기술 스택

### Backend Framework
- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **Spring Security + OAuth2**
- **QueryDSL** (동적 쿼리)
- **MapStruct** (DTO 매핑)

### Database
- **MySQL 8.0** (Docker)
- **JPA + Hibernate**

### Authentication
- **JWT** (Access/Refresh Token)
- **OAuth2** (Google, Kakao, Naver)

### Infrastructure
- **Docker** (MySQL 컨테이너)
- **Firebase Storage** (이미지 업로드)
- **Gradle** (빌드 도구)

### API Documentation
- **Swagger/OpenAPI 3.0**

### External APIs
- **Steam Store API** (게임 정보 크롤링)
- **PortOne API** (결제)

---

## 🏗 프로젝트 구조

```
src/main/java/com/imfine/ngs
├── crawler/         # Steam API 크롤링
│   ├── client/      # Steam API 클라이언트
│   ├── service/     # 크롤링 서비스
│   └── mapper/      # 장르 매핑
├── game/            # 게임 관리
│   ├── entity/      # Game, GameTag, GameMainMedia
│   ├── repository/  # JPA Repository
│   ├── service/     # 게임 조회/검색 서비스
│   └── controller/  # 게임 API
├── user/            # 사용자 관리
│   ├── entity/      # User, UserSession
│   ├── service/     # 사용자 인증/관리
│   └── controller/  # 사용자 API
├── order/           # 주문 관리
│   ├── entity/      # Order, OrderDetail
│   └── service/     # 주문 생성/조회
├── payment/         # 결제 관리
│   ├── service/     # PortOne 결제 서비스
│   └── controller/  # 결제 API
├── community/       # 리뷰/커뮤니티
│   ├── entity/      # Review
│   └── service/     # 리뷰 작성/조회
├── media/           # 미디어 관리
│   └── service/     # Firebase Storage
├── support/         # 고객 지원
└── _global/         # 공통 설정
    ├── config/      # Security, CORS, JPA 설정
    ├── jwt/         # JWT 인증
    └── exception/   # 전역 예외 처리
```

---

## 🚀 실행 방법

### 1. 사전 요구사항
- Java 21 이상
- Docker Desktop
- MySQL 8.0 (Docker 컨테이너)

### 2. MySQL Docker 컨테이너 실행
```bash
docker run -d \
  --name mysql-container \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=ngs_db \
  -p 3306:3306 \
  mysql:8.0
```

### 3. 환경 변수 설정
프로젝트 루트에 `.env` 파일 생성:
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ngs_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# JWT
JWT_SECRET=your-jwt-secret-key

# OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
KAKAO_CLIENT_ID=your-kakao-client-id
KAKAO_CLIENT_SECRET=your-kakao-client-secret
NAVER_CLIENT_ID=your-naver-client-id
NAVER_CLIENT_SECRET=your-naver-client-secret

# OAuth2 Redirect
OAUTH2_REDIRECT_URL=http://localhost:3000/auth/callback

# PortOne
API_SECRET=your-portone-api-secret

# Firebase
FIREBASE_BUCKET=your-firebase-bucket
FIREBASE_PATH=path/to/firebase-credentials.json
```

### 4. 애플리케이션 실행
```bash
# Gradle 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### 5. API 문서 확인
브라우저에서 접속:
```
http://localhost:8080/swagger-ui/index.html
```

---

## 📡 주요 API 엔드포인트

### 게임 API
- `GET /api/games` - 게임 목록 조회 (필터링/정렬)
- `GET /api/games/{id}` - 게임 상세 조회
- `GET /api/games/search` - 게임 검색

### 크롤링 API (관리자)
- `POST /api/admin/crawl/steam/game/{steamAppId}` - Steam 게임 크롤링 (단일)
- `POST /api/admin/crawl/steam/bulk` - Steam 게임 일괄 크롤링

### 사용자 API
- `POST /api/users/register` - 회원가입
- `POST /api/users/login` - 로그인
- `GET /api/users/me` - 내 정보 조회

### 주문/결제 API
- `POST /api/orders` - 주문 생성
- `POST /api/payments/validate` - 결제 검증
- `GET /api/orders/my` - 내 주문 내역

### 리뷰 API
- `POST /api/reviews` - 리뷰 작성
- `GET /api/reviews/game/{gameId}` - 게임 리뷰 조회

---

## 🎮 Steam 크롤링 사용법

### JSON 파일로 일괄 크롤링
```bash
# 1. Steam App ID 목록 JSON 파일 생성 (예: steam_games.json)
{
  "steamAppIds": [1245620, 570, 730, 440, 271590]
}

# 2. 크롤링 API 호출
curl -X POST http://localhost:8080/api/admin/crawl/steam/bulk \
  -H "Content-Type: application/json" \
  -d @steam_games.json
```

### 장르 자동 매핑
Steam API의 장르 정보는 자동으로 `GameTagType` enum으로 매핑됩니다:
- Action, RPG, Strategy, Simulation, Sports, Racing, Puzzle 등 28개 장르 지원
- 한국어 장르명도 자동 매핑

---

## 🔒 인증 및 보안

### JWT 토큰 인증
- Access Token: 6시간 유효
- Refresh Token: HTTP-Only 쿠키로 관리
- Bearer 토큰 방식: `Authorization: Bearer {token}`

### OAuth2 소셜 로그인
- Google, Kakao, Naver 지원
- 리다이렉트 URL: `http://localhost:8080/login/oauth2/code/{provider}`

---

## 🤝 개발 규칙

### 키워드 (작업 내용 분류)

| 태그      | 설명                                                         |
|-----------|-------------------------------------------------------------|
| feat      | 새로운 기능 추가                                               |
| add       | 파일 추가                                                    |
| del       | 파일 삭제                                                    |
| fix       | 버그 수정                                                    |
| docs      | 문서 수정                                                    |
| style     | 스타일 변경, 세미콜론 누락 등 **코드 변경이 없는 경우**                |
| test      | 테스트 코드 추가, 리팩토링된 테스트 코드                            |
| chore     | 빌드 설정, 패키지 수정, 기타 작업                                 |
| refactor  | 코드 리팩토링 (기능 변화 없이 구조 개선)                            |
| ci        | CI 설정 (예: GitHub Actions) 관련 작업                         |

---

### 브랜치 명명 규칙

[키워드]-[issue_number]

예시:
이슈 번호 5번에 대한 새로운 기능 개발 → `feat-5`
이슈 번호 5번에 대한 수정 개발 → `refactor-5`

---

### 커밋 메시지 규칙

- 형식:
  키워드: 작업 내용
- 예시:
  feat: 회원가입 기능 추가

#### 작성 규칙

- 키워드 첫 글자는 **소문자**
- 키워드와 내용 사이는 **한 칸 띄어쓰기**
- **제목은 간결하게**

---

## 📝 라이센스

This project is licensed under the MIT License.

# chat-web

Chat Space WEB 레이어입니다.  
Thymeleaf로 HTML을 서버사이드 렌더링하고, Spring Security로 세션을 관리합니다.  
비즈니스 로직은 직접 처리하지 않고 chat-was REST API를 호출하는 프록시 역할을 합니다.

## 포트

| 환경 | 포트 |
|------|------|
| local / dev | 8080 |

## 실행

```bash
./gradlew bootRun
```

프로파일 기본값: `local` (`application.yml` → `spring.profiles.active`)

## 주요 기술

| 항목 | 내용 |
|------|------|
| Java | 17 |
| Spring Boot | 3.5.0 |
| 템플릿 엔진 | Thymeleaf |
| 인증 | Spring Security (세션 기반, 중복 로그인 방지) |
| WAS 통신 | Spring RestClient |
| 빌드 | Gradle 8.13 |

## 디렉터리 구조

```
src/main/
├── java/com/chat/web/
│   ├── auth/               # 로그인·회원가입 (화면 라우팅 + WAS 호출)
│   ├── chat/               # 채팅 REST API (프록시)
│   └── global/
│       ├── common/         # ApiResponse, SessionConstants
│       └── config/         # Security, RestClient, WAS URL 설정
└── resources/
    ├── templates/
    │   ├── auth/           # login.html, signup.html
    │   ├── main/           # index.html (채팅 메인 페이지)
    │   └── fragments/      # sidebar.html (Thymeleaf 공통 조각)
    └── static/
        ├── common-ui/      # chat-vue 빌드 결과물 (common-ui.umd.js)
        ├── css/            # chat.css
        ├── js/             # sidebar.js (사이드바 Vue 앱)
        └── vendor/         # Vue, axios, marked, Tailwind CSS (로컬 서빙)
```

## 화면 라우팅

| URL | 설명 |
|-----|------|
| `GET /login` | 로그인 페이지 |
| `POST /login` | 로그인 처리 |
| `GET /signup` | 회원가입 페이지 |
| `POST /signup` | 회원가입 처리 |
| `GET /` | 채팅 메인 (로그인 필요) |

## 채팅 API (WAS 프록시)

| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/api/chat/rooms` | 채팅방 목록 |
| GET | `/api/chat/rooms/search?q=` | 채팅방 검색 |
| POST | `/api/chat/send` | 메시지 전송 + AI 응답 |
| GET | `/api/chat/rooms/{roomId}/messages` | 메시지 히스토리 |
| POST | `/api/chat/rooms/{roomId}/delete` | 채팅방 삭제 |

## 프론트엔드 스크립트 로드 순서

```html
<script src="/vendor/tailwindcss.js"></script>
<script src="/vendor/vue.global.prod.js"></script>
<script src="/vendor/axios.min.js"></script>
<script src="/vendor/marked.min.js"></script>
<script src="/common-ui/common-ui.umd.js"></script>
```

`vendor/` 파일은 안정적이어서 브라우저가 장기 캐시합니다.  
`common-ui.umd.js`는 컴포넌트 수정 시에만 변경됩니다 (약 5 KB).

## 환경 설정 파일

| 파일 | 용도 |
|------|------|
| `application.yml` | 공통 기본값 (프로파일: local) |
| `application-local.yml` | 로컬 개발 환경 |
| `application-dev.yml` | 개발 서버 환경 |
| `application-prod.yml` | 운영 환경 |

## WAS URL 설정

`application-local.yml` 예시:

```yaml
was:
  base-url: http://localhost:8081
```

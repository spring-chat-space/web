# vendor 라이브러리 관리

이 폴더에는 프론트엔드에서 사용하는 외부 JS 라이브러리가 있습니다.
CDN 대신 로컬로 서빙하여 인터넷 없는 환경에서도 동작합니다.

## 파일 목록

| 파일 | 라이브러리 | 현재 버전 | 사용 위치 |
|------|-----------|----------|---------|
| `vue.global.prod.js` | Vue 3 | 3.5.35 | index.html, sidebar.js |
| `axios.min.js` | axios | 1.16.1 | index.html, sidebar.js |
| `marked.min.js` | marked | 15.0.12 | index.html (마크다운 렌더링) |
| `tailwindcss.js` | Tailwind CSS Play CDN | 3.4.17 | 모든 HTML 파일 |

## 버전 업데이트 방법

`package.json`에 현재 버전이 기록되어 있습니다.
버전을 바꾸려면 아래 순서대로 진행하세요.

### 1단계 — package.json 버전 수정

`chat-web/package.json`을 열고 원하는 버전으로 수정합니다.

```json
{
  "dependencies": {
    "axios": "1.16.1",
    "marked": "15.0.12",
    "vue": "3.5.35"
  }
}
```

### 2단계 — npm install

```bash
cd chat-web
npm install
```

### 3단계 — vendor 폴더로 복사

```bash
npm run copy-vendor
```

내부적으로 `copy-vendor.js`가 실행되며:
- `vue`, `axios`, `marked` → `node_modules`에서 복사
- `tailwindcss` → CDN에서 다운로드

### 4단계 — 이 파일(README.md)의 버전 표 업데이트

위 파일 목록 표의 버전을 실제 변경한 버전으로 수정해 두세요.

### 5단계 — git 커밋

변경된 vendor 파일들을 커밋합니다.

```bash
git add src/main/resources/static/vendor/
git commit -m "chore: vendor 라이브러리 버전 업데이트 (vue 3.5.35 → x.x.x)"
```

## 각 라이브러리 공식 페이지

- Vue 3: https://github.com/vuejs/core/releases
- axios: https://github.com/axios/axios/releases
- marked: https://github.com/markedjs/marked/releases
- Tailwind CSS: https://tailwindcss.com/docs/installation/play-cdn

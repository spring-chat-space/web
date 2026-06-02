# vendor 라이브러리 관리

이 폴더에는 프론트엔드에서 사용하는 외부 JS 라이브러리가 있습니다.
CDN 대신 로컬로 서빙하여 인터넷 없는 환경에서도 동작합니다.

## 파일 목록

| 파일 | 라이브러리 | 현재 버전 | 사용 위치 |
|------|-----------|----------|---------|
| `vue.global.prod.js` | Vue 3 | 3.5.35 | index.html, sidebar.js |
| `axios.min.js` | axios | 1.9.0 | index.html, sidebar.js |
| `marked.min.js` | marked | 15.0.12 | index.html (마크다운 렌더링) |
| `tailwindcss.js` | Tailwind CSS Play CDN | 3.4.17 | 모든 HTML 파일 |

## 로드 순서 (index.html)

```html
<script src="/vendor/tailwindcss.js"></script>
<script src="/vendor/vue.global.prod.js"></script>
<script src="/vendor/axios.min.js"></script>
<script src="/vendor/marked.min.js"></script>
<script src="/common-ui/common-ui.umd.js"></script>  <!-- chat-vue 빌드 결과물 -->
```

> Vue·axios는 변경 빈도가 낮아 브라우저 캐시를 오래 유지합니다.
> common-ui.umd.js는 컴포넌트 수정 시에만 재다운로드됩니다 (약 15 KB).

## 버전 업데이트 방법

각 라이브러리 공식 GitHub에서 배포용 JS 파일을 직접 다운로드하여 교체합니다.

| 라이브러리 | 다운로드 경로 |
|-----------|-------------|
| Vue 3 | https://github.com/vuejs/core/releases → `vue.global.prod.js` |
| axios | https://github.com/axios/axios/releases → `axios.min.js` |
| marked | https://github.com/markedjs/marked/releases → `marked.min.js` |
| Tailwind CSS | https://tailwindcss.com/docs/installation/play-cdn → Play CDN 스크립트 저장 |

파일 교체 후 이 README의 버전 표도 함께 수정해 두세요.

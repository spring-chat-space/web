/**
 * copy-vendor.js
 *
 * node_modules에 설치된 프론트엔드 라이브러리를
 * Spring Boot static 리소스 경로(src/main/resources/static/vendor)로 복사합니다.
 *
 * 사용법:
 *   npm install
 *   npm run copy-vendor   (또는 node copy-vendor.js)
 *
 * 복사 대상:
 *   vue        → vue.global.prod.js
 *   axios      → axios.min.js
 *   marked     → marked.min.js
 *   tailwindcss → tailwindcss.js  (CDN 다운로드)
 */

const fs   = require('fs');
const path = require('path');
const https = require('https');

const DEST = path.resolve(__dirname, 'src/main/resources/static/vendor');

// 복사할 파일 목록: [node_modules 내 소스 경로, 대상 파일명]
const COPIES = [
  ['vue/dist/vue.global.prod.js',     'vue.global.prod.js'],
  ['axios/dist/axios.min.js',         'axios.min.js'],
];

// marked는 버전에 따라 UMD 경로가 다르므로 후보를 순서대로 시도
const MARKED_CANDIDATES = [
  'marked/marked.min.js',
  'marked/lib/marked.umd.min.js',
  'marked/lib/marked.esm.js',
  'marked/src/marked.js',
];

const TAILWIND_CDN = 'https://cdn.tailwindcss.com';
const TAILWIND_DEST = 'tailwindcss.js';

/**
 * 대상 디렉터리가 없으면 생성합니다.
 */
function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
    console.log(`[mkdir] ${dir}`);
  }
}

/**
 * node_modules에서 static/vendor로 파일을 복사합니다.
 *
 * @param {string} src  - node_modules 기준 상대 경로
 * @param {string} dest - vendor 디렉터리 내 파일명
 */
function copyFile(src, dest) {
  const srcPath  = path.resolve(__dirname, 'node_modules', src);
  const destPath = path.join(DEST, dest);

  if (!fs.existsSync(srcPath)) {
    throw new Error(`소스 파일을 찾을 수 없습니다: ${srcPath}`);
  }

  fs.copyFileSync(srcPath, destPath);
  const size = (fs.statSync(destPath).size / 1024).toFixed(1);
  console.log(`[copy]  ${dest}  (${size} KB)  ← ${src}`);
}

/**
 * marked UMD 빌드를 후보 경로 순서대로 찾아 복사합니다.
 * 어떤 후보도 없으면 에러를 발생시킵니다.
 */
function copyMarked() {
  for (const candidate of MARKED_CANDIDATES) {
    const srcPath = path.resolve(__dirname, 'node_modules', candidate);
    if (fs.existsSync(srcPath)) {
      fs.copyFileSync(srcPath, path.join(DEST, 'marked.min.js'));
      const size = (fs.statSync(path.join(DEST, 'marked.min.js')).size / 1024).toFixed(1);
      console.log(`[copy]  marked.min.js  (${size} KB)  ← ${candidate}`);
      return;
    }
  }
  throw new Error('marked 빌드 파일을 node_modules에서 찾지 못했습니다. 후보: ' + MARKED_CANDIDATES.join(', '));
}

/**
 * URL에서 파일을 다운로드합니다. 301/302 리다이렉트(상대 경로 포함)를 따라갑니다.
 *
 * @param {string} url      - 다운로드 URL
 * @param {string} destPath - 저장할 절대 경로
 * @param {number} [maxRedirects=5] - 최대 리다이렉트 횟수
 * @returns {Promise<void>}
 */
function download(url, destPath, maxRedirects = 5) {
  return new Promise((resolve, reject) => {
    function get(currentUrl, remaining) {
      const file = fs.createWriteStream(destPath);

      https.get(currentUrl, (res) => {
        // 리다이렉트 처리 (상대 경로도 절대 경로로 변환)
        if (res.statusCode === 301 || res.statusCode === 302) {
          res.resume();
          file.destroy();
          if (remaining <= 0) return reject(new Error('리다이렉트 횟수 초과'));

          let location = res.headers.location;
          if (location && location.startsWith('/')) {
            const base = new URL(currentUrl);
            location = `${base.protocol}//${base.host}${location}`;
          }
          return get(location, remaining - 1);
        }

        if (res.statusCode !== 200) {
          res.resume();
          file.destroy();
          return reject(new Error(`HTTP ${res.statusCode}: ${currentUrl}`));
        }

        res.pipe(file);
        file.on('finish', () => {
          file.close(() => {
            const size = (fs.statSync(destPath).size / 1024).toFixed(1);
            console.log(`[dl]    ${path.basename(destPath)}  (${size} KB)  ← ${currentUrl}`);
            resolve();
          });
        });
        file.on('error', (err) => {
          if (fs.existsSync(destPath)) fs.unlinkSync(destPath);
          reject(err);
        });
      }).on('error', (err) => {
        file.destroy();
        if (fs.existsSync(destPath)) fs.unlinkSync(destPath);
        reject(err);
      });
    }

    get(url, maxRedirects);
  });
}

/**
 * 메인 실행 함수.
 * 1) vendor 디렉터리 준비
 * 2) vue, axios 복사
 * 3) marked 복사
 * 4) tailwindcss CDN 다운로드
 */
async function main() {
  console.log('=== copy-vendor 시작 ===\n');
  ensureDir(DEST);

  // vue, axios 복사
  for (const [src, dest] of COPIES) {
    copyFile(src, dest);
  }

  // marked 복사 (버전 호환 경로 탐색)
  copyMarked();

  // tailwindcss CDN 다운로드
  console.log(`[dl]    tailwindcss.js 다운로드 중...  ← ${TAILWIND_CDN}`);
  await download(TAILWIND_CDN, path.join(DEST, TAILWIND_DEST));

  console.log('\n=== copy-vendor 완료 ===');
  console.log(`대상 경로: ${DEST}`);
}

main().catch((err) => {
  console.error('\n[오류]', err.message);
  process.exit(1);
});

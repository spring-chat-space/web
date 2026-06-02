/**
 * sidebar.js — Chat Space 사이드바 Vue 앱
 *
 * fragments/sidebar.html의 #sidebar 엘리먼트에 독립적인 Vue createApp()을 마운트합니다.
 * 채팅방 목록 관리, 검색, 삭제 및 페이지 메인 앱과의 CustomEvent 통신을 담당합니다.
 *
 * 발행 이벤트 (→ 메인 Vue 앱):
 *   'sidebar:roomSelected' → 채팅방 선택됨 (detail: { roomId })
 *   'sidebar:newChat'      → 새 채팅 시작 요청
 *   'sidebar:roomDeleted'  → 채팅방 삭제됨 (detail: { roomId })
 *
 * 수신 이벤트 (← 메인 Vue 앱):
 *   'chat:roomCreated'     → 신규 채팅방 생성 완료 (detail: { roomId })
 *
 * 의존성: Vue 3 전역 빌드(vue.global.prod.js), axios.min.js, common-ui.umd.js (선택)
 * 로드 순서: vue.global.prod.js → axios.min.js → common-ui.umd.js → sidebar.js
 */
(function () {
  'use strict';

  const { createApp, ref, onMounted } = Vue;

  createApp({
    setup() {
      // ── 반응형 상태 ────────────────────────────────────
      /** 채팅방 목록 [{ roomId, title }] */
      const rooms             = ref([]);
      /** 목록 로딩 중 여부 */
      const isLoading         = ref(true);
      /** 현재 선택(활성화)된 채팅방 ID */
      const currentRoomId     = ref(null);
      /** 프로필 드롭다운 표시 여부 */
      const isProfileMenuOpen = ref(false);

      let _searchTimer = null;

      // ── 채팅방 목록 API 호출 ───────────────────────────
      /**
       * 전체 채팅방 목록을 서버에서 불러와 rooms 상태를 갱신합니다.
       */
      async function loadRooms() {
        isLoading.value = true;
        try {
          const { data } = await axios.get('/api/chat/rooms');
          rooms.value = data.result ?? [];
        } catch (e) {
          console.error('[Sidebar] 채팅방 목록 로드 실패:', e);
          rooms.value = [];
        } finally {
          isLoading.value = false;
        }
      }

      /**
       * 키워드로 채팅방을 검색합니다. 키워드가 없으면 전체 목록을 불러옵니다.
       *
       * @param {string} keyword - 검색 키워드
       */
      async function _searchRooms(keyword) {
        isLoading.value = true;
        try {
          const url = keyword
            ? `/api/chat/rooms/search?q=${encodeURIComponent(keyword)}`
            : '/api/chat/rooms';
          const { data } = await axios.get(url);
          rooms.value = data.result ?? [];
        } catch (e) {
          console.error('[Sidebar] 채팅방 검색 실패:', e);
        } finally {
          isLoading.value = false;
        }
      }

      /**
       * 검색 입력에 300ms 디바운스를 적용합니다.
       *
       * @param {string} value - 검색 입력값
       */
      function debounceSearch(value) {
        clearTimeout(_searchTimer);
        _searchTimer = setTimeout(() => _searchRooms(value), 300);
      }

      // ── 채팅방 액션 ────────────────────────────────────

      /**
       * 채팅방을 선택합니다. 사이드바 하이라이트 갱신 후 메인 앱에 이벤트를 발행합니다.
       *
       * @param {number} roomId - 선택할 채팅방 ID
       */
      function openRoom(roomId) {
        currentRoomId.value = roomId;
        window.dispatchEvent(new CustomEvent('sidebar:roomSelected', { detail: { roomId } }));
      }

      /**
       * 새 채팅을 시작합니다. 활성 채팅방을 해제하고 메인 앱에 이벤트를 발행합니다.
       */
      function newChat() {
        currentRoomId.value = null;
        window.dispatchEvent(new CustomEvent('sidebar:newChat'));
      }

      /**
       * 채팅방을 삭제합니다.
       * CommonUI.confirm()으로 삭제 전 사용자 확인을 받습니다.
       * 삭제 후 로컬 rooms 배열에서 즉시 제거하고 메인 앱에 이벤트를 발행합니다.
       *
       * @param {number} roomId - 삭제할 채팅방 ID
       */
      async function deleteRoom(roomId) {
        const ok = (typeof CommonUI !== 'undefined')
          ? await CommonUI.confirm('이 채팅방을 삭제하시겠습니까?', { type: 'danger', confirmLabel: '삭제' })
          : confirm('이 채팅방을 삭제하시겠습니까?');

        if (!ok) return;

        try {
          await axios.post(`/api/chat/rooms/${roomId}/delete`);
          // 로컬 배열에서 즉시 제거하여 재조회 없이 UI를 반영
          rooms.value = rooms.value.filter(r => r.roomId !== roomId);
          window.dispatchEvent(new CustomEvent('sidebar:roomDeleted', { detail: { roomId } }));
        } catch (e) {
          console.error('[Sidebar] 채팅방 삭제 실패:', e);
        }
      }

      // ── 사이드바 UI 제어 ───────────────────────────────

      /**
       * 사이드바를 접거나 펼칩니다 (collapsed CSS 클래스 토글).
       */
      function toggleSidebar() {
        document.getElementById('sidebar')?.classList.toggle('collapsed');
      }

      /**
       * 프로필 드롭다운 메뉴를 열거나 닫습니다.
       */
      function toggleProfileMenu() {
        isProfileMenuOpen.value = !isProfileMenuOpen.value;
      }

      // 프로필 메뉴 외부 클릭 시 자동 닫기
      document.addEventListener('click', function (e) {
        const btn  = document.getElementById('profileBtn');
        const menu = document.getElementById('profileMenu');
        if (btn && menu && !btn.contains(e.target) && !menu.contains(e.target)) {
          isProfileMenuOpen.value = false;
        }
      });

      // ── 메인 앱 이벤트 수신 ────────────────────────────
      // 메인 앱이 신규 채팅방을 생성하면 목록을 재조회하고 해당 방을 활성화합니다.
      window.addEventListener('chat:roomCreated', (e) => {
        currentRoomId.value = e.detail.roomId;
        loadRooms();
      });

      // ── 마운트 시 초기 목록 로드 ──────────────────────
      onMounted(loadRooms);

      return {
        rooms,
        isLoading,
        currentRoomId,
        isProfileMenuOpen,
        debounceSearch,
        openRoom,
        newChat,
        deleteRoom,
        toggleSidebar,
        toggleProfileMenu,
      };
    }
  }).mount('#sidebar');

  // index.html의 #sidebarToggleBtn (사이드바 외부 버튼)이 호출하는 전역 함수
  window.toggleSidebar = () => document.getElementById('sidebar')?.classList.toggle('collapsed');

})();

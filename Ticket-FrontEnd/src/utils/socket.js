import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { useNotificationStore } from "@/stores/notification"; // Pinia store 임포트

// API 기본 URL 설정
const API_BASE_URL =
  // 임시 수정
  import.meta.env.VITE_API_BASE_URL || "/api";
// import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

let stompClient = null;

function parseJwt(token) {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + c.charCodeAt(0).toString(16).padStart(2, "0"))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error("JWT 디코딩 실패:", e);
    return null;
  }
}

export function connectWebSocket(jwtToken, onMessageCallback) {
  const decoded = parseJwt(jwtToken);
  const userEmail = decoded?.sub;

  if (!userEmail) {
    console.error("[WebSocket] JWT 토큰에서 이메일을 찾을 수 없습니다.");
    return;
  }

  // SockJS URL 확인
  const wsUrl = `${API_BASE_URL}/ws-notify`;
  console.log("[WebSocket] resolved ws url", wsUrl);

  // 로컬 테스트 시
  // const socketFactory = () => new SockJS("http://localhost:8080/ws-notify");
  const socketFactory = () => {
    const sock = new SockJS(wsUrl);
    try {
      sock.onopen = () => console.log("[SockJS] onopen");
      sock.onclose = (e) => console.error("[SockJS] onclose", e);
      sock.onerror = (e) => console.error("[SockJS] onerror", e);
    } catch (_) {}
    return sock;
  };

  // Pinia store 인스턴스 가져오기
  const notificationStore = useNotificationStore();

  // StompClient 생성 시 factory 함수 넘기기 (자동 재연결 지원)
  stompClient = Stomp.over(socketFactory);

  // STOMP 내부 디버그 출력 활성화
  try {
    stompClient.debug = (msg) => console.log("[STOMP]", msg);
  } catch (_) {}

  // STOMP 에러 프레임 로깅
  try {
    stompClient.onStompError = (frame) => {
      console.error("[STOMP][ERROR]", {
        headers: frame.headers,
        body: frame.body,
      });
    };
  } catch (_) {}

  // 일부 구현체에서 제공하는 웹소켓 close 콜백
  try {
    if ("onWebSocketClose" in stompClient) {
      stompClient.onWebSocketClose = (evt) => console.error("[STOMP] onWebSocketClose", evt);
    }
  } catch (_) {}

  const connectHeaders = { Authorization: `Bearer ${jwtToken}` };
  console.log("[WebSocket] connect headers keys", Object.keys(connectHeaders));

  stompClient.connect(
    connectHeaders,
    () => {
      console.log("[WebSocket] 연결 성공", { userEmail });

      //  특정 유저의 알림을 받아오기 위한 구독 (Spring user-destination 표준)
      const subscribePath = `/user/queue/notify`;
      console.log("[WebSocket] subscribe path", subscribePath);
      stompClient.subscribe(subscribePath, (message) => {
        try {
          console.log("[WebSocket] message headers", message.headers);
        } catch (_) {}

        let payload;
        try {
          payload = JSON.parse(message.body);
        } catch (e) {
          // JSON 파싱 실패 시, 문자열을 message 필드에 넣어서 넘김
          payload = { message: message.body };
        }

        // 알림을 Pinia store에 추가
        try {
          notificationStore.addNotification(payload);
          console.log("[WebSocket] notification added to store", {
            preview: String(payload?.message || "").slice(0, 60),
            unreadCount: notificationStore.unreadCount,
            total: notificationStore.notifications.length,
          });
        } catch (storeErr) {
          console.error("[WebSocket] store update error", storeErr);
        }

        // callback으로 받은 메시지 처리 (선택적)
        try {
          if (typeof onMessageCallback === "function") {
            onMessageCallback(payload);
            console.log("[WebSocket] onMessageCallback executed");
          }
        } catch (cbErr) {
          console.error("[WebSocket] onMessageCallback error", cbErr);
        }
      });

      // console.log("[WebSocket] user/queue/notify 구독 시작");
    },
    (error) => {
      console.error("[WebSocket] 연결 실패:", error);
      try {
        console.error("[WebSocket] connect error details", {
          message: error?.message,
          headers: error?.headers,
          body: error?.body,
        });
      } catch (_) {}
    }
  );
}

export function disconnectWebSocket() {
  if (stompClient && stompClient.connected) {
    console.log("[WebSocket] disconnect called");
    stompClient.disconnect(() => {
      console.log("[WebSocket] 연결 해제됨");
    });
  } else {
    console.log("[WebSocket] 연결이 안 되어 있어서 해제할 게 없습니다.");
  }
}

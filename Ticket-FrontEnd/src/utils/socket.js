import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { useNotificationStore } from "@/stores/notification"; // Pinia store 임포트

// API 기본 URL 설정
const API_BASE_URL =
  // 임시 수정
  import.meta.env.VITE_API_BASE_URL || "api";
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
  console.log('jwt토큰 들어감? ', jwtToken)
  const decoded = parseJwt(jwtToken);
  const userEmail = decoded?.sub;

  if (!userEmail) {
    console.error("[WebSocket] JWT 토큰에서 이메일을 찾을 수 없습니다.");
    return;
  }

  // 로컬 테스트 시
  // const socketFactory = () => new SockJS("http://localhost:8080/ws-notify");
  const socketFactory = () => new SockJS(`${API_BASE_URL}/ws-notify`);

  // Pinia store 인스턴스 가져오기
  const notificationStore = useNotificationStore();

  // StompClient 생성 시 factory 함수 넘기기 (자동 재연결 지원)
  stompClient = Stomp.over(socketFactory);

  if (import.meta?.env?.DEV) console.log("[WebSocket] creating STOMP client");

  stompClient.connect(
    { Authorization: `Bearer ${jwtToken}` },
    () => {
      if (import.meta?.env?.DEV) console.log("[WebSocket] 연결 성공");

      //  특정 유저의 알림을 받아오기 위한 구독
      // 표준 사용자 큐 구독: 서버에서 convertAndSendToUser(user, '/queue/notify', ...) 사용 시 클라이언트는 이메일을 경로에 포함하지 않습니다.
      stompClient.subscribe(`/user/queue/notify`, (message) => {
        let payload;
        try {
          payload = JSON.parse(message.body);
        } catch (e) {
          payload = { message: message.body };
        }

        notificationStore.addNotification(payload);

        if (import.meta?.env?.DEV)
          console.log("[WebSocket] message received", payload);

        if (typeof onMessageCallback === "function") {
          onMessageCallback(payload);
        }
      });

      // 하위 호환/디버그: 이전 경로가 서버에 남아있는 경우 로그만 남김
      if (import.meta?.env?.DEV) {
        console.log("[WebSocket] Subscribed to /user/queue/notify for:", userEmail);
      }
    },
    (error) => {
      console.error("[WebSocket] 연결 실패:", error);
    }
  );
}

export function disconnectWebSocket() {
  if (stompClient && stompClient.connected) {
    stompClient.disconnect(() => {
      console.log("[WebSocket] 연결 해제됨");
    });
  } else {
    console.log("[WebSocket] 연결이 안 되어 있어서 해제할 게 없습니다.");
  }
}

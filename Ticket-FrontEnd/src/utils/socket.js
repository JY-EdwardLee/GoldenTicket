import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

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

  // 로컬 테스트 시
  // const socket = new SockJS("http://localhost:8080/ws-notify");
  const socket = new SockJS("http://i13a109.p.ssafy.io:8080/ws-notify");

  stompClient = Stomp.over(socket);
  // console.log("[WebSocket] Stomp 클라이언트 생성 완료");

  stompClient.connect(
    { Authorization: `Bearer ${jwtToken}` },
    () => {
      console.log("[WebSocket] 연결 성공");

      //  핵심 구독 부분
      stompClient.subscribe("/user/queue/notify", (message) => {
        let payload;
        try {
          payload = JSON.parse(message.body);
        } catch (e) {
          // JSON 파싱 실패 시, 문자열을 message 필드에 넣어서 넘김
          payload = { message: message.body };
        }

        onMessageCallback(payload);
      });

      // console.log("[WebSocket] user/queue/notify 구독 시작");
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

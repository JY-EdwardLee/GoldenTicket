# app.py

import os
from typing import Optional

from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

from langchain.chains import RetrievalQA
from langchain_upstage import UpstageEmbeddings, ChatUpstage
from pinecone import Pinecone, ServerlessSpec
from langchain_pinecone import Pinecone as LangchainPinecone

# --- .env 환경 변수 로드 ---
load_dotenv()

# --- 필수 환경 변수 체크 ---
if not os.getenv("OPENAI_API_KEY"):
    raise ValueError("OPENAI_API_KEY (GMS 키)를 환경변수에 설정하세요.")
if not os.getenv("PINECONE_API_KEY"):
    raise ValueError("PINECONE_API_KEY를 환경변수에 설정하세요.")
if not os.getenv("PINECONE_ENVIRONMENT"):
    raise ValueError("PINECONE_ENVIRONMENT를 환경변수에 설정하세요.")

# --- GMS LLM(ChatUpstage) 객체 생성 ---
chat_upstage = ChatUpstage(
    model_name="gpt-4.1-mini",
    temperature=0.3,
    max_tokens=4096,
    base_url="https://gms.ssafy.io/gmsapi/api.openai.com/v1",
    api_key=os.getenv("OPENAI_API_KEY")
)

# --- 임베딩 모델 생성 ---
embedding_upstage = UpstageEmbeddings(model="embedding-query")

# --- Pinecone 설정 ---
index_name = "ticket-chatbot"
pinecone_api_key = os.getenv("PINECONE_API_KEY")
pinecone_env = os.getenv("PINECONE_ENVIRONMENT")

# Pinecone 초기화
pc = Pinecone(api_key=pinecone_api_key)

# 인덱스 없으면 새로 생성
if index_name not in pc.list_indexes().names():
    pc.create_index(
        name=index_name,
        dimension=1536,
        metric='euclidean',
        spec=ServerlessSpec(cloud='aws', region=pinecone_env)
    )

# 인덱스 가져오기
index = pc.Index(index_name)

# 벡터스토어 구성 (text_key는 메타데이터 키 이름)
pinecone_vectorstore = LangchainPinecone(
    index=index,
    embedding=embedding_upstage,
    text_key="text"
)

# Retriever 생성
pinecone_retriever = pinecone_vectorstore.as_retriever(
    search_type="mmr",
    search_kwargs={"k": 3}
)

# --- FastAPI 앱 생성 ---
app = FastAPI(
    title="Golden Ticket Chatbot API",
    description="시니어 프로야구 팬을 위한 티켓 챗봇",
    version="1.0.0"
)

# --- CORS 설정 ---
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        
        "http://localhost:5713",
        "http://i13a109.p.ssafy.io",
        "http://localhost:8080",          # 자바 백엔드 개발환경 주소
        "http://i13a109.p.ssafy.io:8080"  # 자바 백엔드 배포주소
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- 메시지 요청 모델 정의 ---
class MessageRequest(BaseModel):
    question: str
    sessionId: Optional[str] = None
    userInfo: Optional[dict] = None

# --- 질문 처리 API ---
@app.post("/query")
def query_chatbot(req: MessageRequest):
    try:
        question = req.question
        user_info = req.userInfo or {}

        # 로그 출력
        print(f"질문: {question}")
        print(f"세션 ID: {req.sessionId}")
        print(f"유저 정보: {user_info}")

        # 사용자 정보 + 질문 포함한 프롬프트 컨텍스트 구성
        context = f"""
사용자 정보: {user_info}
질문: {question}

다음 규칙에 따라 답변을 작성해 주세요:

1. 주 사용자 연령층이 senior이기 때문에, 질문에 대해 친절하고 이해하기 쉬운 문장으로 간결하게 답변해 주세요.

2. "경기 일정"에 관한 질문일 경우 다음과 같이 답변해 주세요:
   - 챗봇은 최대 1주일 이내의 경기 일정만 알려드릴 수 있다고 안내하세요.
   - 더 자세한 일정은 "응모 페이지"에서 확인하실 수 있다고 덧붙여 주세요.

3. 사용자가 티켓 서비스의 특정 기능(예: 양도, 응모, 게시판 등)을 이용하려는 의도가 명확하면:
   - 아래 링크 목록 중 가장 적절한 하나를 추천해 주세요.

4. 사용자의 질문에 아래 조건이 모두 **포함**된 경우, 반드시 `action` 객체를 포함해 주세요:
   - "응모" 또는 "신청"이라는 단어가 포함됨
   - 날짜 정보가 포함됨 (연도는 생략 가능, 월과 일이 반드시 있어야 함)
   - 팀 이름이 포함됨

5. `action` 필드는 아래의 JSON 형식을 정확히 따라야 합니다 (주의: `"응모"` 같은 말이 아니라 `target`은 반드시 `'application'` 등의 코드값으로 넣어야 합니다):

"action": {{
  "type": "navigate",
  "target": "application",
  "params": {{
    "date": "2025-08-12",
    "team": "삼성 라이온즈"
  }}
}}

6.출력 형식은 반드시 아래 JSON 객체 형태를 따르세요.
문자열로 감싸지 말고, 코드 블록 없이, 그대로 JSON 객체로 반환하세요:
{{
  "reply": "[사용자에게 전달할 자연스러운 답변]",
  "link": "[추천할 링크 or null]",
  "action": {{ "type": "...", "target": "...", "params": {{...}} }} or null
}}

[사용 가능한 링크 목록]
- 양도: http://i13a109.p.ssafy.io/transfer
- 응모: http://i13a109.p.ssafy.io/application
- 공지사항/자유게시판/단체 관련 게시판: http://i13a109.p.ssafy.io/bulletin
- 이용 가이드: http://i13a109.p.ssafy.io/guide

+ **반드시** 아래 JSON 객체만을, 코드 블록 없이, 정확한 JSON 포맷으로 출력해 주세요.  
+ JSON 문자열로 중첩하지 말고, 바로 JSON 타입으로 반환하세요:
"""



        # QA 체인 실행 (문서 검색 기반 응답)
        qa_chain = RetrievalQA.from_chain_type(
            llm=chat_upstage,
            chain_type="stuff",
            retriever=pinecone_retriever,
            return_source_documents=True
        )

        # AI 응답 받기
        result = qa_chain(context)

        if result and "result" in result:
            import json
            raw_output = result["result"]

            try:
                # AI 응답을 JSON으로 파싱
                parsed = json.loads(raw_output)
                reply = parsed.get("reply", "답변이 없습니다.")
                link = parsed.get("link", None)
                action = parsed.get("action", None)
            except json.JSONDecodeError:
                # JSON 형식이 아닐 경우 처리
                reply = raw_output
                link = None
                action = None

            return {
                "reply": reply,
                "link": link,
                "action" : action
            }

        # 결과가 없을 경우
        return {
            "reply": "죄송해요, 질문하신 내용에 대한 정보를 찾을 수 없어요.",
            "link": None,
            "action" : None 
        }

    except Exception as e:
        import traceback
        traceback.print_exc()
        return {"reply": f"서버 내부 오류 발생: {str(e)}", "link": None, "action" : None}

# --- 헬스 체크 API ---
@app.get("/health")
@app.get("/")
async def health_check():
    return {"status": "ok"}

# --- 로컬 실행 ---
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

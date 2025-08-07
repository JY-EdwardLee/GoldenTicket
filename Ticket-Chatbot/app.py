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
    model_name="gpt-4.1-nano",
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
        "http://localhost:5173",
        "http://i13a109.p.ssafy.io"
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

다음 규칙에 따라 대답해줘:
1. 질문에 대해 친절하고 정확하게 답변해줘.
2. 만약 사용자가 티켓 서비스의 특정 페이지(예: 양도, 응모, 게시판 등)를 사용하고자 하는 의도가 느껴진다면, 아래의 페이지 중 가장 적절한 링크를 추천해줘.
3. 링크는 반드시 아래 중 하나만 사용하고, 없는 경우 null로 해줘.

[사용 가능한 링크 목록]
- 양도: http://i13a109.p.ssafy.io/transfer
- 응모: http://i13a109.p.ssafy.io/application
- 공지사항/자유게시판/단체 관련 게시판: http://i13a109.p.ssafy.io/bulletin
- 이용 가이드: http://i13a109.p.ssafy.io/guide

최종 응답은 반드시 아래 JSON 형태로 반환해줘:
{{
  "reply": "[여기에 자연스러운 답변 작성]",
  "link": "[해당 링크 or null]"
}}
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
            except json.JSONDecodeError:
                # JSON 형식이 아닐 경우 처리
                reply = raw_output
                link = None

            return {
                "reply": reply,
                "link": link
            }

        # 결과가 없을 경우
        return {
            "reply": "죄송해요, 질문하신 내용에 대한 정보를 찾을 수 없어요.",
            "link": None
        }

    except Exception as e:
        import traceback
        traceback.print_exc()
        return {"reply": f"서버 내부 오류 발생: {str(e)}", "link": None}

# --- 헬스 체크 API ---
@app.get("/health")
@app.get("/")
async def health_check():
    return {"status": "ok"}

# --- 로컬 실행 ---
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

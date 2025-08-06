# app.py
import os
from typing import List, Optional

from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from langchain.chains import RetrievalQA
from langchain_upstage import UpstageEmbeddings, ChatUpstage
from pinecone import Pinecone, ServerlessSpec  # ✅ 최신 방식
from langchain_pinecone import Pinecone as LangchainPinecone
from pydantic import BaseModel

# .env 파일에 저장된 API 키를 로드
load_dotenv()

# --- 환경변수 체크 ---
if not os.getenv("OPENAI_API_KEY"):
    raise ValueError("OPENAI_API_KEY (GMS 키)를 환경변수에 설정하세요.")

if not os.getenv("PINECONE_API_KEY"):
    raise ValueError("PINECONE_API_KEY를 환경변수에 설정하세요.")

if not os.getenv("PINECONE_ENVIRONMENT"):
    raise ValueError("PINECONE_ENVIRONMENT를 환경변수에 설정하세요.")

# --- OpenAI / GMS Chat 객체 생성 ---
chat_upstage = ChatUpstage(
    model_name="gpt-4.1-nano",  # GMS GPT-4.1 Nano 모델명
    temperature=0.3,
    max_tokens=4096,
    base_url="https://gms.ssafy.io/gmsapi/api.openai.com/v1",
    api_key=os.getenv("OPENAI_API_KEY")
)

# --- 임베딩 생성기 ---
embedding_upstage = UpstageEmbeddings(model="embedding-query")

# Pinecone 인덱스 이름
index_name = "ticket-chatbot"

# --- Pinecone 최신 방식 초기화 ---
pinecone_api_key = os.getenv("PINECONE_API_KEY")
pinecone_env = os.getenv("PINECONE_ENVIRONMENT")

pc = Pinecone(api_key=pinecone_api_key)

# 인덱스 없으면 생성
if index_name not in pc.list_indexes().names():
    pc.create_index(
        name=index_name,
        dimension=1536,
        metric='euclidean',
        spec=ServerlessSpec(
            cloud='aws',
            region=pinecone_env
        )
    )

# 인덱스 가져오기
index = pc.Index(index_name)

#  최신 방식: 반드시 text_key="text" 지정
pinecone_vectorstore = LangchainPinecone(
    index=index,
    embedding=embedding_upstage,
    text_key="text"  # Pinecone에 저장된 메타데이터 키 이름 (대부분 text)
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
        "http://i13a109.p.ssafy.io"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- 메시지 요청 모델 ---
class MessageRequest(BaseModel):
    question: str

# --- 질문 처리 API ---
@app.post("/query")
@app.post("/query")
def query_chatbot(req: MessageRequest):
    try:
        qa_chain = RetrievalQA.from_chain_type(
            llm=chat_upstage,
            chain_type="stuff",
            retriever=pinecone_retriever,
            return_source_documents=True
        )

        result = qa_chain(req.question)

        if result:
            return {"reply": result["result"]}
        else:
            return {"reply": "죄송해요, 질문하신 내용에 대한 정보를 찾을 수 없어요."}
    except Exception as e:
        # 에러 발생 시 상세 로그를 찍고 메시지 반환
        import traceback
        traceback.print_exc()
        return {"reply": f"서버 내부 오류 발생: {str(e)}"}

# --- 헬스 체크 API ---
@app.get("/health")
@app.get("/")
async def health_check():
    return {"status": "ok"}

# --- 로컬 실행 ---
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

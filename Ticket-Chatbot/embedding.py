import os
from dotenv import load_dotenv
from langchain_upstage import UpstageEmbeddings
from langchain.docstore.document import Document
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import Pinecone as PineconeVectorStore
from pinecone import Pinecone, ServerlessSpec

load_dotenv()

pinecone_api_key = os.getenv("PINECONE_API_KEY")
pinecone_environment = os.getenv("PINECONE_ENVIRONMENT", "us-east1-gcp")

embedding_upstage = UpstageEmbeddings(model="embedding-query")

# init() 함수 제거, 인스턴스 생성 시 API 키와 환경 변수 전달
pc = Pinecone(api_key=pinecone_api_key, environment=pinecone_environment)

index_name = "ticket-chatbot"

if index_name not in pc.list_indexes().names():
    pc.create_index(
        name=index_name,
        dimension=4096,
        metric="cosine",
        spec=ServerlessSpec(cloud="aws", region="us-east-1"),
    )

index = pc.Index(name=index_name)

# ========================================
#  [1] 현재는 PDF가 없으므로 더미 문서로 테스트
# ========================================

# 골든티켓 서비스를 설명하는 더미 텍스트 리스트
dummy_texts = [
    "골든티켓은 시니어 프로야구 팬들을 위한 티켓 양도 플랫폼입니다.",
    "사용자들은 경기 티켓을 안전하게 거래하고, 응원하는 팀에 대한 이야기를 나눌 수 있습니다.",
    "경기 일정, 좌석 위치, 구단 정보 등을 통합 제공하여 사용자 편의를 높입니다.",
    "시니어 팬 전용 커뮤니티 공간에서는 응원글과 후기, 사진 공유가 가능합니다.",
    "리뷰 시스템을 통해 양도자와 수령자의 신뢰를 쌓고, 건전한 거래 문화를 유지합니다."
]

# 각 문장을 LangChain의 Document 형식으로 변환
documents = [
    Document(page_content=text, metadata={"source": f"dummy_doc_{i+1}"})
    for i, text in enumerate(dummy_texts)
]

# 문서를 일정 길이로 나누기 (최대 500자, 겹치는 부분 50자)
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=500,
    chunk_overlap=50
)
split_docs = text_splitter.split_documents(documents)

# 분할된 문서를 임베딩 후 Pinecone에 저장
PineconeVectorStore.from_documents(
    split_docs,
    embedding_upstage,
    index_name=index_name
)

print(" 더미 데이터 임베딩 완료 및 Pinecone 저장 완료!")

# ========================================
# [2] PDF 파일 처리 (나중에 파일 추가되면 주석 해제)
# ========================================

# from langchain_upstage import UpstageDocumentParseLoader
# 
# pdf_dir = "pdfs"
# for filename in os.listdir(pdf_dir):
#     if filename.lower().endswith('.pdf'):
#         pdf_path = os.path.join(pdf_dir, filename)
#         print(f"Processing {pdf_path}...")
#         
#         try:
#             # PDF 파일을 파싱하여 문서로 변환
#             loader = UpstageDocumentParseLoader(
#                 pdf_path,
#                 output_format='html',
#                 coordinates=False
#             )
#             
#             docs = loader.load()
#             splits = text_splitter.split_documents(docs)
#             
#             # Pinecone에 저장
#             PineconeVectorStore.from_documents(
#                 splits,
#                 embedding_upstage,
#                 index_name=index_name
#             )
#             
#             print(f" {filename} 처리 완료")
#         
#         except Exception as e:
#             print(f" {filename} 처리 중 오류 발생: {str(e)}")
#             continue

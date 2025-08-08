import os
from dotenv import load_dotenv
from langchain_upstage import UpstageEmbeddings, UpstageDocumentParseLoader
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


# 문서를 일정 길이로 나누기 (최대 500자, 겹치는 부분 50자)
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=500,
    chunk_overlap=50
)

# ========================================
# [2] PDF 파일 처리 (나중에 파일 추가되면 주석 해제)
# ========================================

# from langchain_upstage import UpstageDocumentParseLoader

pdf_dir = "pdfs"
for filename in os.listdir(pdf_dir):
    if filename.lower().endswith('.pdf'):
        pdf_path = os.path.join(pdf_dir, filename)
        print(f"Processing {pdf_path}...")
        
        try:
            # PDF 파일을 파싱하여 문서로 변환
            loader = UpstageDocumentParseLoader(
                pdf_path,
                output_format='html',
                coordinates=False
            )
            
            docs = loader.load()
            splits = text_splitter.split_documents(docs)
            
            # Pinecone에 저장
            PineconeVectorStore.from_documents(
                splits,
                embedding_upstage,
                index_name=index_name
            )
            
            print(f" {filename} 처리 완료")
        
        except Exception as e:
            print(f" {filename} 처리 중 오류 발생: {str(e)}")
            continue

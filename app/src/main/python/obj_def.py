from pydantic import BaseModel, Json, validator


class KTSData(BaseModel):
    molecule: str
    brackets: str
    format: str

class KTSString(BaseModel):
    kts_content: str
    format: str


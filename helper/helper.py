# from vietnamese stopwords wiki 
vietnamese_stopwords = [
    "bị", "bởi", "cả", "các", "cái", "cần", "càng", "chỉ", "chiếc", "cho", "chứ", "chưa", "chuyện", "có", "có_thể", 
    "cứ", "của", "cùng", "cũng", "đã", "đang", "đây", "để", "đến_nỗi", "đều", "điều", "do", "đó", "được", "dưới", 
    "gì", "khi", "không", "là", "lại", "lên", "lúc", "mà", "mỗi", "một_cách", "này", "nên", "nếu", "ngay", "nhiều", 
    "như", "nhưng", "những", "nơi", "nữa", "phải", "qua", "ra", "rằng", "rất", "rồi", "sau", "sẽ", "so", "sự", "tại", 
    "theo", "thì", "trên", "trước", "từ", "từng", "và", "vẫn", "vào", "vậy", "vì", "việc", "với", "vừa"
]

def handle_unwanted(text):
    return " ".join(text.strip().replace(",", " ").replace(".", " ").replace('"', " ").replace("“", " ").split())

def to_list(text):
    return [word for word in text.split()]

def handle_categories(text):
    return [word.strip() for word in text.split(",")]

def handle_title(text):
    text = text.replace("-", " ").replace(",", " ").replace(".", " ").replace(":", " ").replace("'", " ").replace("(", " ").replace(")", " ")
    return [word.strip() for word in text.split(" ")]

def hanlde_tags(text):
    return " ".join(text).lower()


